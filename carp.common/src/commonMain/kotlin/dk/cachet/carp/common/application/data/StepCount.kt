package dk.cachet.carp.common.application.data

import kotlinx.serialization.*


/**
 * Holds step count data as number of steps taken in a corresponding time interval.
 */
@Serializable
@SerialName( CarpDataTypes.STEP_COUNT_TYPE_NAME )
data class StepCount( val steps: Int, override val sensorSpecificData: Data? = null ) : SensorData
{
    init
    {
        require( steps >= 0 ) { "Number of steps needs to be a positive number." }
    }
}
