declare module 'carp.core-kotlin-carp.studies.core'
{
    import { kotlin } from 'kotlin'
    import ArrayList = kotlin.collections.ArrayList
    import HashSet = kotlin.collections.HashSet

    import { kotlinx as kxd } from 'Kotlin-DateTime-library-kotlinx-datetime-js-legacy'
    import Instant = kxd.datetime.Instant

    import { dk as cdk } from 'carp.core-kotlin-carp.common'
    import EmailAddress = cdk.cachet.carp.common.application.EmailAddress
    import UUID = cdk.cachet.carp.common.application.UUID
    import AccountIdentity = cdk.cachet.carp.common.application.users.AccountIdentity
    import AssignedTo = cdk.cachet.carp.common.application.users.AssignedTo
    import ApplicationServiceRequest = cdk.cachet.carp.common.infrastructure.services.ApplicationServiceRequest
    import ApiVersion = cdk.cachet.carp.common.application.services.ApiVersion

    import { dk as ddk } from 'carp.core-kotlin-carp.deployments.core'
    import StudyDeploymentStatus = ddk.cachet.carp.deployments.application.StudyDeploymentStatus
    import StudyInvitation = ddk.cachet.carp.deployments.application.users.StudyInvitation

    import { dk as pdk } from 'carp.core-kotlin-carp.protocols.core'
    import StudyProtocolSnapshot = pdk.cachet.carp.protocols.application.StudyProtocolSnapshot


    namespace dk.cachet.carp.studies.application
    {
        class StudyDetails
        {
            constructor(
                studyId: UUID, ownerId: UUID, name: string, createdOn: Instant,
                description: string | null,
                invitation: StudyInvitation,
                protocolSnapshot: StudyProtocolSnapshot | null )

            static get Companion(): StudyDetails$Companion

            readonly studyId: UUID
            readonly ownerId: UUID
            readonly name: string
            readonly createdOn: Instant
            readonly description: string | null
            readonly invitation: StudyInvitation
            readonly protocolSnapshot: StudyProtocolSnapshot | null
        }
        interface StudyDetails$Companion { serializer(): any }


        abstract class StudyStatus
        {
            readonly studyId: UUID
            readonly name: string
            readonly createdOn: Instant
            readonly studyProtocolId: UUID | null
            readonly canSetInvitation: boolean
            readonly canSetStudyProtocol: boolean
            readonly canDeployToParticipants: boolean

            static get Companion(): StudyStatus$Companion
        }
        interface StudyStatus$Companion { serializer(): any }

        namespace StudyStatus
        {
            class Configuring extends StudyStatus
            {
                constructor(
                    studyId: UUID,
                    name: string,
                    createdOn: Instant,
                    studyProtocolId: UUID | null,
                    canSetInvitation: boolean,
                    canSetStudyProtocol: boolean,
                    canDeployToParticipants: boolean,
                    canGoLive: boolean )
    
                readonly canGoLive: boolean
            }
            class Live extends StudyStatus
            {
                constructor(
                    studyId: UUID,
                    name: string,
                    createdOn: Instant,
                    studyProtocolId: UUID | null,
                    canSetInvitation: boolean,
                    canSetStudyProtocol: boolean,
                    canDeployToParticipants: boolean )
            }
        }
    }


    namespace dk.cachet.carp.studies.application.users
    {
        class AssignedParticipantRoles
        {
            constructor( participantId: UUID, assignedRoles: AssignedTo )

            static get Companion(): AssignedParticipantRoles$Companion

            readonly participantId: UUID
            readonly assignedRoles: AssignedTo
        }
        function participantIds_skpkn2$( assignedGroup: ArrayList<AssignedParticipantRoles> ): HashSet<UUID>
        function participantRoles_skpkn2$( assignedGroup: ArrayList<AssignedParticipantRoles> ): HashSet<string>
        interface AssignedParticipantRoles$Companion { serializer(): any }


        class Participant
        {
            constructor( accountIdentity: AccountIdentity, id?: UUID )

            static get Companion(): Participant$Companion

            readonly accountIdentity: AccountIdentity
            readonly id: UUID
        }
        interface Participant$Companion { serializer(): any }


        abstract class ParticipantGroupStatus
        {
            static get Companion(): ParticipantGroupStatus$Companion

            readonly id: UUID
            readonly participants: HashSet<Participant>
        }
        interface ParticipantGroupStatus$Companion { serializer(): any }

        namespace ParticipantGroupStatus
        {
            class Staged extends ParticipantGroupStatus
            {
                constructor( id: UUID, participants: HashSet<Participant> )
            }
            abstract class InDeployment extends ParticipantGroupStatus
            {
                readonly invitedOn: Instant
                readonly studyDeploymentStatus: StudyDeploymentStatus
            }
            class Invited extends InDeployment
            {
                constructor(
                    id: UUID,
                    participants: HashSet<Participant>,
                    invitedOn: Instant,
                    studyDeploymentStatus: StudyDeploymentStatus )
            }
            class Running extends InDeployment
            {
                constructor(
                    id: UUID,
                    participants: HashSet<Participant>,
                    invitedOn: Instant,
                    studyDeploymentStatus: StudyDeploymentStatus,
                    startedOn: Instant )

                readonly startedOn: Instant
            }
            class Stopped extends InDeployment
            {
                constructor(
                    id: UUID,
                    participants: HashSet<Participant>,
                    invitedOn: Instant,
                    studyDeploymentStatus: StudyDeploymentStatus,
                    startedOn: Instant | null,
                    stoppedOn: Instant )

                readonly startedOn: Instant | null
                readonly stoppedOn: Instant
            }
        }
    }


    namespace dk.cachet.carp.studies.infrastructure
    {
        import AssignedParticipantRoles = dk.cachet.carp.studies.application.users.AssignedParticipantRoles


        abstract class StudyServiceRequest implements ApplicationServiceRequest
        {
            readonly apiVersion: ApiVersion

            static get Serializer(): any
        }

        namespace StudyServiceRequest
        {
            class CreateStudy extends StudyServiceRequest
            {
                constructor( ownerId: UUID, name: string, description?: string | null, invitation?: StudyInvitation | null )
            }
            class SetInternalDescription extends StudyServiceRequest
            {
                constructor( studyId: UUID, name: string, description: string | null )
            }
            class GetStudyDetails extends StudyServiceRequest
            {
                constructor( studyId: UUID )
            }
            class GetStudyStatus extends StudyServiceRequest
            {
                constructor( studyId: UUID )
            }
            class GetStudiesOverview extends StudyServiceRequest
            {
                constructor( ownerId: UUID )
            }
            class SetInvitation extends StudyServiceRequest
            {
                constructor( studyId: UUID, invitation: StudyInvitation )
            }
            class SetProtocol extends StudyServiceRequest
            {
                constructor( studyId: UUID, protocol: StudyProtocolSnapshot )
            }
            class RemoveProtocol extends StudyServiceRequest
            {
                constructor( studyId: UUID )
            }
            class GoLive extends StudyServiceRequest
            {
                constructor( studyId: UUID )
            }
            class Remove extends StudyServiceRequest
            {
                constructor( studyId: UUID )
            }
        }


        abstract class RecruitmentServiceRequest implements ApplicationServiceRequest
        {
            readonly apiVersion: ApiVersion

            static get Serializer(): any
        }

        namespace RecruitmentServiceRequest
        {
            class AddParticipant extends RecruitmentServiceRequest
            {
                constructor( studyId: UUID, email: EmailAddress )
            }
            class GetParticipant extends RecruitmentServiceRequest
            {
                constructor( studyId: UUID, participantId: UUID )
            }
            class GetParticipants extends RecruitmentServiceRequest
            {
                constructor( studyId: UUID )
            }
            class InviteNewParticipantGroup extends RecruitmentServiceRequest
            {
                constructor( studyId: UUID, group: HashSet<AssignedParticipantRoles> )
            }
            class GetParticipantGroupStatusList extends RecruitmentServiceRequest
            {
                constructor( studyId: UUID )
            }
            class StopParticipantGroup extends RecruitmentServiceRequest
            {
                constructor( studyId: UUID, groupId: UUID )
            }
        }
    }
}
