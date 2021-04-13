package dk.cachet.carp.deployment.infrastructure

import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.data.input.CarpInputDataTypes
import dk.cachet.carp.common.application.data.input.Sex
import dk.cachet.carp.common.infrastructure.test.STUBS_SERIAL_MODULE
import dk.cachet.carp.deployment.application.users.ParticipantData
import kotlin.test.*


/**
 * Tests for [ParticipantData] relying on core infrastructure.
 */
class ParticipantDataTest
{
    @Test
    fun can_serialize_and_deserialize_using_JSON()
    {
        JSON = createDeploymentSerializer( STUBS_SERIAL_MODULE )

        val data = mapOf( CarpInputDataTypes.SEX to Sex.Male )
        val participantData = ParticipantData( UUID.randomUUID(), data )

        val serialized: String = participantData.toJson()
        val parsed: ParticipantData = ParticipantData.fromJson( serialized )

        assertEquals( participantData, parsed )
    }
}
