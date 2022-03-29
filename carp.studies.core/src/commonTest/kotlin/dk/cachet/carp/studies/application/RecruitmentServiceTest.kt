package dk.cachet.carp.studies.application

import dk.cachet.carp.common.application.EmailAddress
import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.data.input.CarpInputDataTypes
import dk.cachet.carp.common.application.devices.Smartphone
import dk.cachet.carp.common.application.services.EventBus
import dk.cachet.carp.common.application.users.ParticipantAttribute
import dk.cachet.carp.protocols.application.StudyProtocolSnapshot
import dk.cachet.carp.protocols.application.users.ExpectedParticipantData
import dk.cachet.carp.protocols.domain.StudyProtocol
import dk.cachet.carp.studies.application.users.AssignParticipantDevices
import dk.cachet.carp.studies.application.users.ParticipantGroupStatus
import kotlinx.coroutines.test.runTest
import kotlin.test.*


private val unknownId: UUID = UUID.randomUUID()


/**
 * Tests for implementations of [RecruitmentService].
 */
interface RecruitmentServiceTest
{
    data class DependentServices(
        val recruitmentService: RecruitmentService,
        val studyService: StudyService,
        val eventBus: EventBus
    )

    /**
     * Create a recruitment service and study service it depends on to be used in the tests.
     */
    fun createService(): DependentServices


    @Test
    fun adding_and_retrieving_participant_succeeds() = runTest {
        val (recruitmentService, studyService) = createService()
        val study = studyService.createStudy( UUID.randomUUID(), "Test" )
        val studyId = study.studyId

        val participant = recruitmentService.addParticipant( studyId, EmailAddress( "test@test.com" ) )

        // Get single participant.
        val studyParticipant = recruitmentService.getParticipant( studyId, participant.id )
        assertEquals( participant, studyParticipant )

        // Get all participants.
        val studyParticipants = recruitmentService.getParticipants( studyId )
        assertEquals( participant, studyParticipants.single() )
    }

    @Test
    fun addParticipant_fails_for_unknown_studyId() = runTest {
        val (recruitmentService, _) = createService()

        val email = EmailAddress( "test@test.com" )
        assertFailsWith<IllegalArgumentException> { recruitmentService.addParticipant( unknownId, email ) }
    }

    @Suppress( "ReplaceAssertBooleanWithAssertEquality" )
    @Test
    fun addParticipant_twice_returns_same_participant() = runTest {
        val (recruitmentService, studyService) = createService()
        val study = studyService.createStudy( UUID.randomUUID(), "Test" )
        val studyId = study.studyId

        val email = EmailAddress( "test@test.com" )
        val p1 = recruitmentService.addParticipant( studyId, email )
        val p2 = recruitmentService.addParticipant( studyId, email )
        assertTrue( p1 == p2 )
    }

    @Test
    fun getParticipant_fails_for_unknown_id() = runTest {
        val (recruitmentService, studyService) = createService()
        val study = studyService.createStudy( UUID.randomUUID(), "Test" )

        // Unknown study id.
        assertFailsWith<IllegalArgumentException> { recruitmentService.getParticipant( unknownId, unknownId ) }

        // Unknown participant id.
        assertFailsWith<IllegalArgumentException> { recruitmentService.getParticipant( study.studyId, unknownId ) }
    }

    @Test
    fun getParticipants_fails_for_unknown_studyId() = runTest {
        val (recruitmentService, _) = createService()

        assertFailsWith<IllegalArgumentException> { recruitmentService.getParticipants( unknownId ) }
    }

    @Test
    fun inviteNewParticipantGroup_succeeds() = runTest {
        val (recruitmentService, studyService) = createService()
        val (studyId, protocolSnapshot) = createLiveStudy( studyService )
        val participant = recruitmentService.addParticipant( studyId, EmailAddress( "test@test.com" ) )

        val deviceRoles = protocolSnapshot.primaryDevices.map { it.roleName }.toSet()
        val assignParticipant = AssignParticipantDevices( participant.id, deviceRoles )
        val groupStatus = recruitmentService.inviteNewParticipantGroup( studyId, setOf( assignParticipant ) )
        assertEquals( participant, groupStatus.participants.single() )
        val participantGroups = recruitmentService.getParticipantGroupStatusList( studyId )
        val participantInGroup = participantGroups.single().participants.single()
        assertEquals( participant, participantInGroup )
    }

    @Test
    fun inviteNewParticipantGroup_fails_for_unknown_studyId() = runTest {
        val (recruitmentService, _) = createService()
        val assignParticipant = AssignParticipantDevices( UUID.randomUUID(), setOf( "Test device" ) )

        assertFailsWith<IllegalArgumentException>
        {
            recruitmentService.inviteNewParticipantGroup( unknownId, setOf( assignParticipant ) )
        }
    }

    @Test
    fun inviteNewParticipantGroup_fails_for_empty_group() = runTest {
        val (recruitmentService, studyService) = createService()
        val (studyId, _) = createLiveStudy( studyService )

        assertFailsWith<IllegalArgumentException> { recruitmentService.inviteNewParticipantGroup( studyId, setOf() ) }
    }

    @Test
    fun inviteNewParticipantGroup_fails_for_unknown_participants() = runTest {
        val (recruitmentService, studyService) = createService()
        val (studyId, protocolSnapshot) = createLiveStudy( studyService )

        val deviceRoles = protocolSnapshot.primaryDevices.map { it.roleName }.toSet()
        val assignParticipant = AssignParticipantDevices( unknownId, deviceRoles )
        assertFailsWith<IllegalArgumentException>
        {
            recruitmentService.inviteNewParticipantGroup( studyId, setOf( assignParticipant ) )
        }
    }

    @Test
    fun inviteNewParticipantGroup_fails_for_unknown_device_roles() = runTest {
        val (recruitmentService, studyService) = createService()
        val (studyId, _) = createLiveStudy( studyService )
        val participant = recruitmentService.addParticipant( studyId, EmailAddress( "test@test.com" ) )

        val assignParticipant = AssignParticipantDevices( participant.id, setOf( "Unknown device" ) )
        assertFailsWith<IllegalArgumentException>
        {
            recruitmentService.inviteNewParticipantGroup( studyId, setOf( assignParticipant ) )
        }
    }

    @Test
    fun inviteNewParticipantGroup_fails_when_not_all_devices_assigned() = runTest {
        val (recruitmentService, studyService) = createService()
        val (studyId, _) = createLiveStudy( studyService )
        val participant = recruitmentService.addParticipant( studyId, EmailAddress( "test@test.com" ) )

        val assignParticipant = AssignParticipantDevices( participant.id, setOf() )
        assertFailsWith<IllegalArgumentException>
        {
            recruitmentService.inviteNewParticipantGroup( studyId, setOf( assignParticipant ) )
        }
    }

    @Test
    fun inviteNewParticipantGroup_multiple_times_returns_same_group() = runTest {
        val (recruitmentService, studyService) = createService()
        val (studyId, protocolSnapshot) = createLiveStudy( studyService )
        val participant = recruitmentService.addParticipant( studyId, EmailAddress( "test@test.com" ) )
        val deviceRoles = protocolSnapshot.primaryDevices.map { it.roleName }.toSet()
        val assignParticipant = AssignParticipantDevices( participant.id, deviceRoles )
        val groupStatus = recruitmentService.inviteNewParticipantGroup( studyId, setOf( assignParticipant ) )

        // Deploy the same group a second time.
        val groupStatus2 = recruitmentService.inviteNewParticipantGroup( studyId, setOf( assignParticipant ) )
        assertEquals( groupStatus, groupStatus2 )
    }

    @Test
    fun inviteNewParticipantGroup_for_previously_stopped_group_returns_new_group() = runTest {
        val (recruitmentService, studyService) = createService()
        val (studyId, protocolSnapshot) = createLiveStudy( studyService )
        val participant = recruitmentService.addParticipant( studyId, EmailAddress( "test@test.com" ) )
        val deviceRoles = protocolSnapshot.primaryDevices.map { it.roleName }.toSet()
        val assignParticipant = AssignParticipantDevices( participant.id, deviceRoles )
        val groupStatus = recruitmentService.inviteNewParticipantGroup( studyId, setOf( assignParticipant ) )

        // Stop previous group. A new deployment with the same participants should be a new participant group.
        recruitmentService.stopParticipantGroup( studyId, groupStatus.id )
        val groupStatus2 = recruitmentService.inviteNewParticipantGroup( studyId, setOf( assignParticipant ) )
        assertNotEquals( groupStatus, groupStatus2 )
    }

    @Test
    fun getParticipantGroupStatusList_returns_multiple_deployments() = runTest {
        val (recruitmentService, studyService) = createService()
        val (studyId, protocolSnapshot) = createLiveStudy( studyService )
        val deviceRoles = protocolSnapshot.primaryDevices.map { it.roleName }.toSet()

        val p1 = recruitmentService.addParticipant( studyId, EmailAddress( "test@test.com" ) )
        val assignedP1 = AssignParticipantDevices( p1.id, deviceRoles )
        recruitmentService.inviteNewParticipantGroup( studyId, setOf( assignedP1 ) )

        val p2 = recruitmentService.addParticipant( studyId, EmailAddress( "test2@test.com" ) )
        val assignedP2 = AssignParticipantDevices( p2.id, deviceRoles )
        recruitmentService.inviteNewParticipantGroup( studyId, setOf( assignedP2 ) )

        val participantGroups = recruitmentService.getParticipantGroupStatusList( studyId )
        assertEquals( 2, participantGroups.size )
        val deployedParticipants = participantGroups.flatMap { it.participants }.toSet()
        assertEquals( setOf( p1, p2 ), deployedParticipants )
    }

    @Test
    fun getParticipantGroupStatusLists_fails_for_unknown_studyId() = runTest {
        val (recruitmentService, _) = createService()

        assertFailsWith<IllegalArgumentException> { recruitmentService.getParticipantGroupStatusList( unknownId ) }
    }

    @Test
    fun stopParticipantGroup_succeeds() = runTest {
        val (recruitmentService, studyService) = createService()
        val (studyId, protocolSnapshot) = createLiveStudy( studyService )
        val participant = recruitmentService.addParticipant( studyId, EmailAddress( "test@test.com" ) )
        val deviceRoles = protocolSnapshot.primaryDevices.map { it.roleName }.toSet()
        val assignParticipant = AssignParticipantDevices( participant.id, deviceRoles )
        val groupStatus = recruitmentService.inviteNewParticipantGroup( studyId, setOf( assignParticipant ) )

        val stoppedGroupStatus = recruitmentService.stopParticipantGroup( studyId, groupStatus.id )
        assertTrue( stoppedGroupStatus is ParticipantGroupStatus.Stopped )
    }

    @Test
    fun stopParticipantGroup_fails_with_unknown_studyId() = runTest {
        val (recruitmentService, _) = createService()

        assertFailsWith<IllegalArgumentException>
        {
            recruitmentService.stopParticipantGroup( unknownId, UUID.randomUUID() )
        }
    }

    @Test
    fun stopParticipantGroup_fails_with_unknown_groupId() = runTest {
        val (recruitmentService, studyService) = createService()
        val (studyId, _) = createLiveStudy( studyService )

        assertFailsWith<IllegalArgumentException> { recruitmentService.stopParticipantGroup( studyId, unknownId ) }
    }


    private suspend fun createLiveStudy( service: StudyService ): Pair<UUID, StudyProtocolSnapshot>
    {
        // Create deployable protocol.
        val protocol = StudyProtocol( UUID.randomUUID(), "Test protocol" )
        protocol.addPrimaryDevice( Smartphone( "User's phone" ) )
        val expectedData = ExpectedParticipantData(
            ParticipantAttribute.DefaultParticipantAttribute( CarpInputDataTypes.SEX )
        )
        protocol.addExpectedParticipantData( expectedData )
        val validSnapshot = protocol.getSnapshot()

        // Create live study from protocol.
        val status = service.createStudy( UUID.randomUUID(), "Test" )
        val studyId = status.studyId
        service.setProtocol( studyId, validSnapshot )
        service.goLive( studyId )

        return Pair( studyId, validSnapshot )
    }
}
