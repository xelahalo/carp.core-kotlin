package bhrp.studyprotocol.domain.triggers

import bhrp.studyprotocol.domain.InvalidConfigurationError
import bhrp.studyprotocol.domain.common.Immutable
import bhrp.studyprotocol.domain.devices.DeviceDescriptor


/**
 * Any condition on a device ([DeviceDescriptor]) which is (or can be) initiated at a certain point in time when the condition applies.
 * The condition can either be time-bound, based on data streams, initiated by a user of the platform, or a combination of these.
 */
abstract class Trigger
    : Immutable( InvalidConfigurationError( "Implementations of Trigger should be data classes and may not contain any mutable properties." ) )
{
    /**
     * The device from which the trigger originates.
     */
    abstract val sourceDevice: DeviceDescriptor
}