package dk.cachet.carp.studies.infrastructure

import dk.cachet.carp.common.application.EmailAddress
import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.services.EventBus
import dk.cachet.carp.common.infrastructure.services.ApplicationServiceLoggingProxy
import dk.cachet.carp.common.infrastructure.services.EventBusLog
import dk.cachet.carp.common.infrastructure.services.LoggedRequest
import dk.cachet.carp.studies.application.RecruitmentService
import dk.cachet.carp.studies.application.StudyService
import dk.cachet.carp.studies.application.users.AssignedParticipantRoles
import dk.cachet.carp.studies.application.users.Participant
import dk.cachet.carp.studies.application.users.ParticipantGroupStatus


/**
 * A proxy for a recruitment [service] which notifies of incoming requests and responses through [log]
 * and keeps a history of requests and published events in [loggedRequests].
 */
class RecruitmentServiceLoggingProxy(
    service: RecruitmentService,
    eventBus: EventBus,
    log: (LoggedRequest<RecruitmentService, RecruitmentService.Event>) -> Unit = { }
) :
    ApplicationServiceLoggingProxy<RecruitmentService, RecruitmentService.Event>(
        service,
        EventBusLog(
            eventBus,
            EventBusLog.Subscription( RecruitmentService::class, RecruitmentService.Event::class ),
            EventBusLog.Subscription( StudyService::class, StudyService.Event::class )
        ),
        log
    ),
    RecruitmentService
{
    override suspend fun addParticipant( studyId: UUID, email: EmailAddress ): Participant =
        log( RecruitmentServiceRequest.AddParticipant( studyId, email ) )

    override suspend fun getParticipant( studyId: UUID, participantId: UUID ): Participant =
        log( RecruitmentServiceRequest.GetParticipant( studyId, participantId ) )

    override suspend fun getParticipants( studyId: UUID ): List<Participant> =
        log( RecruitmentServiceRequest.GetParticipants( studyId ) )

    override suspend fun inviteNewParticipantGroup(
        studyId: UUID,
        group: Set<AssignedParticipantRoles>
    ): ParticipantGroupStatus =
        log( RecruitmentServiceRequest.InviteNewParticipantGroup( studyId, group ) )

    override suspend fun getParticipantGroupStatusList( studyId: UUID ): List<ParticipantGroupStatus> =
        log( RecruitmentServiceRequest.GetParticipantGroupStatusList( studyId ) )

    override suspend fun stopParticipantGroup( studyId: UUID, groupId: UUID ): ParticipantGroupStatus =
        log( RecruitmentServiceRequest.StopParticipantGroup( studyId, groupId ) )
}
