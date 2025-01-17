package dk.cachet.carp.common.application.tasks

import dk.cachet.carp.common.application.data.DataType
import dk.cachet.carp.common.application.devices.DeviceConfiguration
import dk.cachet.carp.common.application.sampling.SamplingConfiguration
import kotlinx.serialization.*


/**
 * Defines data that needs to be measured/collected passively as part of a task defined by [TaskConfiguration].
 */
@Serializable
sealed class Measure
{
    /**
     * Defines data that needs to be measured/collected from a data stream on a [DeviceConfiguration].
     */
    @Serializable
    data class DataStream(
        /**
         * The type of data this measure collects.
         */
        val type: DataType,
        /**
         * Optionally, override the default configuration on how to sample the data stream of the matching [type] on the device.
         * In case `null` is specified, the default configuration is derived from the [DeviceConfiguration].
         */
        val overrideSamplingConfiguration: SamplingConfiguration? = null
    ) : Measure()

    /**
     * Specify that the data related to the trigger with [triggerId] which started or stopped the task should be measured.
     */
    @Serializable
    data class TriggerData( val triggerId: Int ) : Measure()
}
