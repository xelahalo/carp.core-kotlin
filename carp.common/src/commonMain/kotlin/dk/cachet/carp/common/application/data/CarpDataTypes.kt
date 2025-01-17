/**
 * Namespace which contains CARP data type definitions.
 */
package dk.cachet.carp.common.application.data


/**
 * All CARP data types.
 */
object CarpDataTypes : DataTypeMetaDataMap()
{
    /**
     * The [DataType] namespace of all CARP data type definitions.
     */
    const val CARP_NAMESPACE: String = "dk.cachet.carp"


    internal const val GEOLOCATION_TYPE_NAME = "$CARP_NAMESPACE.geolocation"
    /**
     * Geographic location data, representing latitude and longitude within the World Geodetic System 1984.
     */
    val GEOLOCATION = add(
        GEOLOCATION_TYPE_NAME,
        "Geolocation",
        DataTimeType.POINT
    )

    internal const val STEP_COUNT_TYPE_NAME = "$CARP_NAMESPACE.stepcount"
    /**
     * Step count data, representing the number of steps a participant has taken in a specified time interval.
     */
    val STEP_COUNT = add(
        STEP_COUNT_TYPE_NAME,
        "Step count",
        DataTimeType.TIME_SPAN
    )

    internal const val ECG_TYPE_NAME = "$CARP_NAMESPACE.ecg"
    /**
     * Electrocardiography (ECG) data, representing electrical activity of the heart for a single lead.
     */
    val ECG = add(
        ECG_TYPE_NAME,
        "Electrocardiography (ECG)",
        DataTimeType.POINT
    )

    internal const val PPG_TYPE_NAME = "$CARP_NAMESPACE.ppg"
    /**
     * Photoplethysmography (PPG) data, representing blood volume changes measured at the skin's surface.
     */
    val PPG = add(
        PPG_TYPE_NAME,
        "Photoplethysmography (PPG)",
        DataTimeType.POINT
    )

    internal const val HEART_RATE_TYPE_NAME = "$CARP_NAMESPACE.heartrate"
    /**
     * Represents the number of heart contractions (beats) per minute.
     */
    val HEART_RATE = add(
        HEART_RATE_TYPE_NAME,
        "Heart rate",
        DataTimeType.POINT
    )

    internal const val INTERBEAT_INTERVAL_TYPE_NAME = "$CARP_NAMESPACE.interbeatinterval"
    /**
     * The time interval between two consecutive heartbeats.
     */
    val INTERBEAT_INTERVAL = add(
        INTERBEAT_INTERVAL_TYPE_NAME,
        "Interbeat interval",
        DataTimeType.TIME_SPAN
    )

    internal const val SENSOR_SKIN_CONTACT_TYPE_NAME = "$CARP_NAMESPACE.sensorskincontact"
    /**
     * Determines whether a sensor requiring contact with skin is making proper contact at a specific point in time.
     */
    val SENSOR_SKIN_CONTACT = add(
        SENSOR_SKIN_CONTACT_TYPE_NAME,
        "Sensor skin contact",
        DataTimeType.POINT
    )

    internal const val NON_GRAVITATIONAL_ACCELERATION_TYPE_NAME = "$CARP_NAMESPACE.nongravitationalacceleration"
    /**
     * Rate of change in velocity, excluding gravity, along perpendicular x, y, and z axes in the device's coordinate system.
     */
    val NON_GRAVITATIONAL_ACCELERATION = add(
        NON_GRAVITATIONAL_ACCELERATION_TYPE_NAME,
        "Acceleration without gravity",
        DataTimeType.POINT
    )

    internal const val EDA_TYPE_NAME = "$CARP_NAMESPACE.eda"
    /**
     * Single-channel electrodermal activity, represented as skin conductance.
     */
    val EDA = add(
        EDA_TYPE_NAME,
        "Electrodermal activity",
        DataTimeType.POINT
    )

    internal const val ACCELERATION_TYPE_NAME = "$CARP_NAMESPACE.acceleration"
    /**
     * Rate of change in velocity, including gravity, along perpendicular x, y, and z axes in the device's coordinate system.
     */
    val ACCELERATION = add(
        ACCELERATION_TYPE_NAME,
        "Acceleration including gravity",
        DataTimeType.POINT
    )

    internal const val ANGULAR_VELOCITY_TYPE_NAME = "$CARP_NAMESPACE.angularvelocity"
    /**
     * Rate of rotation around perpendicular x, y, and z axes.
     */
    val ANGULAR_VELOCITY = add(
        ANGULAR_VELOCITY_TYPE_NAME,
        "Angular velocity",
        DataTimeType.POINT
    )

    internal const val SIGNAL_STRENGTH_TYPE_NAME = "$CARP_NAMESPACE.signalstrength"
    /**
     * The received signal strength of a wireless device.
     */
    val SIGNAL_STRENGTH = add(
        SIGNAL_STRENGTH_TYPE_NAME,
        "Signal strength",
        DataTimeType.POINT
    )

    internal const val TRIGGERED_TASK_TYPE_NAME = "$CARP_NAMESPACE.triggeredtask"
    /**
     * A task which was started or stopped by a trigger, referring to identifiers in the study protocol.
     */
    val TRIGGERED_TASK = add(
        TRIGGERED_TASK_TYPE_NAME,
        "Triggered task",
        DataTimeType.POINT
    )

    internal const val COMPLETED_TASK_TYPE_NAME = "$CARP_NAMESPACE.completedtask"
    /**
     * An interactive task which was completed over the course of a specified time interval.
     */
    val COMPLETED_TASK = add(
        COMPLETED_TASK_TYPE_NAME,
        "Completed task",
        DataTimeType.TIME_SPAN
    )
}
