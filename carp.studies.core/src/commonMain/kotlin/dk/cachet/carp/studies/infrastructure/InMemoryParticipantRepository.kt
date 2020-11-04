package dk.cachet.carp.studies.infrastructure

import dk.cachet.carp.common.UUID
import dk.cachet.carp.studies.domain.users.Participant
import dk.cachet.carp.studies.domain.users.ParticipantRepository


class InMemoryParticipantRepository : ParticipantRepository
{
    private val participants: MutableMap<UUID, MutableList<Participant>> = mutableMapOf()


    /**
     * Adds a new [participant] for the study with [studyId] to the repository.
     *
     * @throws IllegalArgumentException when a participant with the specified ID already exists within the study.
     */
    override suspend fun addParticipant( studyId: UUID, participant: Participant )
    {
        val studyParticipants = participants.getOrPut( studyId ) { mutableListOf() }
        require( studyParticipants.none { it.id == participant.id } )
        studyParticipants.add( participant )
    }

    /**
     * Returns the participants which were added to the study with the specified [studyId].
     */
    override suspend fun getParticipants( studyId: UUID ): List<Participant> = participants[ studyId ] ?: listOf()
}