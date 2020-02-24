package dk.cachet.carp.studies.domain

import dk.cachet.carp.common.DateTime
import dk.cachet.carp.common.UUID
import kotlinx.serialization.Serializable


/**
 * Describes the status of a [Study]: the number of participants, progress towards study goal, etc.
 */
@Serializable
data class StudyStatus(
    val studyId: UUID,
    /**
     * A descriptive name for the study, as assigned by the [StudyOwner].
     */
    val name: String,
    /**
     * The date when this study was created.
     */
    val creationDate: DateTime,
    /**
     * Determines whether the study in its current state is ready to be deployed to participants.
     */
    val canDeployToParticipants: Boolean,
    /**
     * Determines whether a study protocol has been locked in and the study may be deployed to real participants.
     */
    val isLive: Boolean
)
