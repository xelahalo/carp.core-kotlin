package dk.cachet.carp.protocols.domain.devices

import dk.cachet.carp.common.Trilean
import dk.cachet.carp.protocols.domain.data.DataType
import dk.cachet.carp.protocols.domain.data.SamplingConfiguration
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass


@Serializable
data class StubMasterDeviceDescriptor( override val roleName: String = "Stub master device" ) :
    MasterDeviceDescriptor<DefaultDeviceRegistration, DefaultDeviceRegistrationBuilder>()
{
    override val samplingConfiguration: Map<DataType, SamplingConfiguration> = emptyMap()

    override fun createDeviceRegistrationBuilder(): DefaultDeviceRegistrationBuilder = DefaultDeviceRegistrationBuilder()
    override fun getRegistrationClass(): KClass<DefaultDeviceRegistration> = DefaultDeviceRegistration::class
    override fun isValidConfiguration( registration: DefaultDeviceRegistration ) = Trilean.TRUE
}
