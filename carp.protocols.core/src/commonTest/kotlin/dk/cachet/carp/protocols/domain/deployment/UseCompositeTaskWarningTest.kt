package dk.cachet.carp.protocols.domain.deployment

import dk.cachet.carp.common.application.triggers.TaskControl
import dk.cachet.carp.common.infrastructure.test.StubDeviceConfiguration
import dk.cachet.carp.common.infrastructure.test.StubPrimaryDeviceConfiguration
import dk.cachet.carp.common.infrastructure.test.StubTaskConfiguration
import dk.cachet.carp.common.infrastructure.test.StubTriggerConfiguration
import dk.cachet.carp.protocols.domain.start
import dk.cachet.carp.protocols.infrastructure.test.createEmptyProtocol
import kotlin.test.*


/**
 * Tests for [UseCompositeTaskWarning].
 */
class UseCompositeTaskWarningTest
{
    @Test
    fun isIssuePresent_true_when_multiple_tasks_are_sent_to_one_device_by_one_trigger()
    {
        val protocol = createEmptyProtocol()
        val device = StubPrimaryDeviceConfiguration()
        val trigger = StubTriggerConfiguration( device )
        with ( protocol )
        {
            addPrimaryDevice( device )
            addTaskControl( trigger, StubTaskConfiguration( "Task 1" ), device, TaskControl.Control.Start )
            addTaskControl( trigger, StubTaskConfiguration( "Task 2" ), device, TaskControl.Control.Start )
        }

        val warning = UseCompositeTaskWarning()
        assertTrue( warning.isIssuePresent( protocol ) )
    }

    @Test
    fun isIssuePresent_false_when_only_single_tasks_are_triggered_per_device()
    {
        val protocol = createEmptyProtocol()
        val device1 = StubPrimaryDeviceConfiguration()
        val device2 = StubDeviceConfiguration()
        val task = StubTaskConfiguration()
        with ( protocol )
        {
            addPrimaryDevice( device1 )
            addConnectedDevice( device2, device1 )
            val trigger1 = StubTriggerConfiguration( device1 )
            addTaskControl( trigger1.start( task, device1 ) )
            addTaskControl( trigger1.start( task, device2 ) )
            addTaskControl( StubTriggerConfiguration( device2 ).start( task, device1 ) )
        }

        val warning = UseCompositeTaskWarning()
        assertFalse( warning.isIssuePresent( protocol ) )
    }

    @Test
    fun getOverlappingTasks_returns_all_overlapping_tasks()
    {
        val protocol = createEmptyProtocol()
        val device = StubPrimaryDeviceConfiguration()
        val trigger = StubTriggerConfiguration( device )
        val task1 = StubTaskConfiguration( "Task 1" )
        val task2 = StubTaskConfiguration( "Task 2" )
        with ( protocol )
        {
            addPrimaryDevice( device )
            addTaskControl( trigger.start( task1, device ) )
            addTaskControl( trigger.start( task2, device ) )
        }

        val warning = UseCompositeTaskWarning()
        val overlapping = warning.getOverlappingTasks( protocol )
        val expectedOverlapping = setOf( UseCompositeTaskWarning.OverlappingTasks( trigger, device, listOf( task1, task2 ) ) )
        assertEquals( expectedOverlapping.count(), overlapping.intersect( expectedOverlapping ).count() )
    }
}
