package dk.cachet.carp.deployments.application.users

import dk.cachet.carp.common.application.devices.AnyPrimaryDeviceConfiguration
import dk.cachet.carp.common.application.devices.DeviceRegistration
import kotlinx.serialization.*


/**
 * Primary [device] and its current [registration] assigned to participants as part of a participant group.
 */
@Serializable
data class AssignedPrimaryDevice(
    val device: AnyPrimaryDeviceConfiguration,
    val registration: DeviceRegistration? = null
)
