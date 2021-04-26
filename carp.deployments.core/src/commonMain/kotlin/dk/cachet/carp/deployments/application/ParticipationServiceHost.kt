package dk.cachet.carp.deployments.application

import dk.cachet.carp.common.application.services.ApplicationServiceEventBus
import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.common.application.data.input.CarpInputDataTypes
import dk.cachet.carp.common.application.data.input.InputDataType
import dk.cachet.carp.common.application.data.input.InputDataTypeList
import dk.cachet.carp.common.application.devices.AnyMasterDeviceDescriptor
import dk.cachet.carp.common.application.users.AccountIdentity
import dk.cachet.carp.deployments.application.users.ActiveParticipationInvitation
import dk.cachet.carp.deployments.application.users.ParticipantData
import dk.cachet.carp.deployments.application.users.Participation
import dk.cachet.carp.deployments.application.users.StudyInvitation
import dk.cachet.carp.deployments.domain.users.AccountService
import dk.cachet.carp.deployments.domain.users.ParticipantGroup
import dk.cachet.carp.deployments.domain.users.ParticipationRepository
import dk.cachet.carp.deployments.domain.users.filterActiveParticipationInvitations


/**
 * Application service which allows inviting participants, retrieving participations for study deployments,
 * and managing data related to participants which is input by users.
 */
class ParticipationServiceHost(
    private val participationRepository: ParticipationRepository,
    private val accountService: AccountService,
    private val eventBus: ApplicationServiceEventBus<ParticipationService, ParticipationService.Event>,
    /**
     * Supported [InputDataType]'s for participant data input by users.
     */
    private val participantDataInputTypes: InputDataTypeList = CarpInputDataTypes
) : ParticipationService
{
    init
    {
        eventBus.subscribe {
            // Create a ParticipantGroup per study deployment (as long as it exists).
            event { created: DeploymentService.Event.StudyDeploymentCreated ->
                val group = ParticipantGroup.fromNewDeployment(
                    created.studyDeploymentId,
                    created.protocol.toObject() )
                participationRepository.putParticipantGroup( group )
            }
            event { removed: DeploymentService.Event.StudyDeploymentsRemoved ->
                participationRepository.removeParticipantGroups( removed.deploymentIds )
            }

            // Notify participant group that associated study deployment has stopped.
            event { stopped: DeploymentService.Event.StudyDeploymentStopped ->
                val group = participationRepository.getParticipantGroup( stopped.studyDeploymentId )
                checkNotNull( group )
                group.studyDeploymentStopped()
                participationRepository.putParticipantGroup( group )
            }

            // Keep track of master device registration changes.
            event { registrationChange: DeploymentService.Event.DeviceRegistrationChanged ->
                if ( registrationChange.device !is AnyMasterDeviceDescriptor ) return@event

                val group = participationRepository.getParticipantGroup( registrationChange.studyDeploymentId )
                checkNotNull( group )
                group.updateDeviceRegistration( registrationChange.device, registrationChange.registration )
                participationRepository.putParticipantGroup( group )
            }
        }
    }


    /**
     * Let the person with the specified [identity] participate in the study deployment with [studyDeploymentId],
     * using the master devices with the specified [assignedMasterDeviceRoleNames].
     * In case no account is associated to the specified [identity], a new account is created.
     * An [invitation] (and account details) is delivered to the person managing the [identity],
     * or should be handed out manually to the relevant participant by the person managing the specified [identity].
     *
     * @throws IllegalArgumentException when:
     * - there is no study deployment with [studyDeploymentId]
     * - any of the [assignedMasterDeviceRoleNames] are not part of the study protocol deployment
     * @throws IllegalStateException when:
     * - the specified [identity] was already invited to participate in this deployment and a different [invitation] is specified than a previous request
     * - this deployment has stopped
     */
    override suspend fun addParticipation(
        studyDeploymentId: UUID,
        assignedMasterDeviceRoleNames: Set<String>,
        identity: AccountIdentity,
        invitation: StudyInvitation
    ): Participation
    {
        val group = participationRepository.getParticipantGroupOrThrowBy( studyDeploymentId )
        val assignedMasterDevices = assignedMasterDeviceRoleNames.map { group.getAssignedMasterDevice( it ) }

        var account = accountService.findAccount( identity )

        // Retrieve or create participation.
        var participation = account?.let { group.getParticipation( it ) }
        val isNewParticipation = participation == null
        participation = participation ?: Participation( studyDeploymentId )

        // Ensure an account exists for the given identity and an invitation has been sent out.
        val deviceDescriptors = assignedMasterDeviceRoleNames.map { roleToUse ->
            group.assignedMasterDevices.first { it.device.roleName == roleToUse } }.map { it.device }
        if ( account == null )
        {
            account = accountService.inviteNewAccount( identity, invitation, participation, deviceDescriptors )
        }
        else if ( isNewParticipation )
        {
            accountService.inviteExistingAccount( account.id, invitation, participation, deviceDescriptors )
        }

        // Add participation to study deployment.
        if ( isNewParticipation )
        {
            val masterDevices = assignedMasterDevices.map { it.device }.toSet()
            group.addParticipation( account, participation, invitation, masterDevices )
            participationRepository.putParticipantGroup( group )
        }
        else
        {
            // This participation was already added and an invitation has been sent.
            // Ensure the request is the same, otherwise, an 'update' might be expected, which is not supported.
            val previousInvitation = participationRepository.getParticipationInvitations( account.id ).first { it.participation == participation }
            check( previousInvitation.invitation == invitation && previousInvitation.assignedMasterDeviceRoleNames == assignedMasterDeviceRoleNames )
                { "This person is already invited to participate in this study and the current invite deviates from the previous one." }
        }

        return participation
    }

    /**
     * Get all participations of active study deployments the account with the given [accountId] has been invited to.
     */
    override suspend fun getActiveParticipationInvitations( accountId: UUID ): Set<ActiveParticipationInvitation>
    {
        // Get participant group for each of the account's invitations.
        val invitations = participationRepository.getParticipationInvitations( accountId )
        val deploymentIds = invitations.map { it.participation.studyDeploymentId }.toSet()
        val groups = participationRepository.getParticipantGroupList( deploymentIds )

        return filterActiveParticipationInvitations( invitations, groups )
    }

    /**
     * Get currently set data for all expected participant data in the study deployment with [studyDeploymentId].
     * Data which is not set equals null.
     *
     * @throws IllegalArgumentException when there is no study deployment with [studyDeploymentId].
     */
    override suspend fun getParticipantData( studyDeploymentId: UUID ): ParticipantData
    {
        val group = participationRepository.getParticipantGroupOrThrowBy( studyDeploymentId )

        return ParticipantData( group.studyDeploymentId, group.data.toMap() )
    }

    /**
     * Get currently set data for all expected participant data for a set of study deployments with [studyDeploymentIds].
     * Data which is not set equals null.
     *
     * @throws IllegalArgumentException when [studyDeploymentIds] contains an ID for which no deployment exists.
     */
    override suspend fun getParticipantDataList( studyDeploymentIds: Set<UUID> ): List<ParticipantData>
    {
        val groups = participationRepository.getParticipantGroupListOrThrow( studyDeploymentIds )

        return groups.map { ParticipantData( it.studyDeploymentId, it.data.toMap() ) }
    }

    /**
     * Set participant [data] for the given [inputDataType] in the study deployment with [studyDeploymentId].
     *
     * @throws IllegalArgumentException when:
     *   - there is no study deployment with [studyDeploymentId]
     *   - [inputDataType] is not configured as expected participant data in the study protocol
     *   - [data] is invalid data for [inputDataType]
     * @return All data for the specified study deployment, including the newly set data.
     */
    override suspend fun setParticipantData( studyDeploymentId: UUID, inputDataType: InputDataType, data: Data? ): ParticipantData
    {
        val group = participationRepository.getParticipantGroupOrThrowBy( studyDeploymentId )
        group.setData( participantDataInputTypes, inputDataType, data )
        participationRepository.putParticipantGroup( group )

        return ParticipantData( group.studyDeploymentId, group.data.toMap() )
    }
}