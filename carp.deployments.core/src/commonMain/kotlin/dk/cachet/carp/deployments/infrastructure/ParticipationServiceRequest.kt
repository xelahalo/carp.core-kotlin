package dk.cachet.carp.deployments.infrastructure

import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.common.application.data.input.InputDataType
import dk.cachet.carp.common.application.users.AccountIdentity
import dk.cachet.carp.common.infrastructure.services.ServiceInvoker
import dk.cachet.carp.common.infrastructure.services.createServiceInvoker
import dk.cachet.carp.deployments.application.ParticipationService
import dk.cachet.carp.deployments.application.users.ActiveParticipationInvitation
import dk.cachet.carp.deployments.application.users.DeanonymizedParticipation
import dk.cachet.carp.deployments.application.users.ParticipantData
import dk.cachet.carp.deployments.application.users.Participation
import dk.cachet.carp.deployments.application.users.StudyInvitation
import kotlinx.serialization.Serializable

// TODO: Due to a bug, `Service` and `Invoker` cannot be used here, although that would be preferred.
//       Change this once this is fixed: https://youtrack.jetbrains.com/issue/KT-24700
private typealias ParticipationServiceInvoker<T> = ServiceInvoker<ParticipationService, T>
// private typealias Service = ParticipationService
// private typealias Invoker<T> = ServiceInvoker<ParticipationService, T>


/**
 * Serializable application service requests to [ParticipationService] which can be executed on demand.
 */
@Serializable
sealed class ParticipationServiceRequest
{
    @Serializable
    data class AddParticipation( val studyDeploymentId: UUID, val externalParticipantId: UUID, val deviceRoleNames: Set<String>, val identity: AccountIdentity, val invitation: StudyInvitation ) :
        ParticipationServiceRequest(),
        ParticipationServiceInvoker<Participation> by createServiceInvoker( ParticipationService::addParticipation, studyDeploymentId, externalParticipantId, deviceRoleNames, identity, invitation )

    @Serializable
    data class DeanonymizeParticipations( val studyDeploymentId: UUID, val externalParticipantIds: Set<UUID> ) :
        ParticipationServiceRequest(),
        ParticipationServiceInvoker<Set<DeanonymizedParticipation>> by createServiceInvoker( ParticipationService::deanonymizeParticipations, studyDeploymentId, externalParticipantIds )

    @Serializable
    data class GetActiveParticipationInvitations( val accountId: UUID ) :
        ParticipationServiceRequest(),
        ParticipationServiceInvoker<Set<ActiveParticipationInvitation>> by createServiceInvoker( ParticipationService::getActiveParticipationInvitations, accountId )

    @Serializable
    data class GetParticipantData( val studyDeploymentId: UUID ) :
        ParticipationServiceRequest(),
        ParticipationServiceInvoker<ParticipantData> by createServiceInvoker( ParticipationService::getParticipantData, studyDeploymentId )

    @Serializable
    data class GetParticipantDataList( val studyDeploymentIds: Set<UUID> ) :
        ParticipationServiceRequest(),
        ParticipationServiceInvoker<List<ParticipantData>> by createServiceInvoker( ParticipationService::getParticipantDataList, studyDeploymentIds )

    @Serializable
    data class SetParticipantData( val studyDeploymentId: UUID, val inputDataType: InputDataType, val data: Data? ) :
        ParticipationServiceRequest(),
        ParticipationServiceInvoker<ParticipantData> by createServiceInvoker( ParticipationService::setParticipantData, studyDeploymentId, inputDataType, data )
}
