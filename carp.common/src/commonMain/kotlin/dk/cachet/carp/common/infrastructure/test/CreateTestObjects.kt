package dk.cachet.carp.common.infrastructure.test

import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.common.application.devices.AnyDeviceConfiguration
import dk.cachet.carp.common.application.devices.AnyPrimaryDeviceConfiguration
import dk.cachet.carp.common.application.devices.DeviceConfiguration
import dk.cachet.carp.common.application.devices.DeviceRegistration
import dk.cachet.carp.common.application.devices.PrimaryDeviceConfiguration
import dk.cachet.carp.common.application.sampling.SamplingConfiguration
import dk.cachet.carp.common.application.tasks.TaskConfiguration
import dk.cachet.carp.common.application.triggers.TriggerConfiguration
import dk.cachet.carp.common.infrastructure.serialization.COMMON_SERIAL_MODULE
import dk.cachet.carp.common.infrastructure.serialization.createDefaultJSON
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.*
import kotlin.reflect.KClass


/**
 * Stubs for testing extending from types in [dk.cachet.carp.common] module which need to be registered when using [Json] serializer.
 */
val STUBS_SERIAL_MODULE = SerializersModule {
    polymorphic( Data::class )
    {
        subclass( StubDataPoint::class )
        subclass( StubDataTimeSpan::class )
    }

    fun PolymorphicModuleBuilder<AnyPrimaryDeviceConfiguration>.registerPrimaryDeviceConfigurationSubclasses()
    {
        subclass( StubPrimaryDeviceConfiguration::class )
    }

    polymorphic( DeviceConfiguration::class )
    {
        subclass( StubDeviceConfiguration::class )
        registerPrimaryDeviceConfigurationSubclasses()
    }
    polymorphic( PrimaryDeviceConfiguration::class )
    {
        registerPrimaryDeviceConfigurationSubclasses()
    }
    polymorphic( SamplingConfiguration::class )
    {
        subclass( StubSamplingConfiguration::class )
    }
    polymorphic( TaskConfiguration::class )
    {
        subclass( StubTaskConfiguration::class )
    }
    polymorphic( TriggerConfiguration::class )
    {
        subclass( StubTriggerConfiguration::class )
    }
}


/**
 * Create a [Json] serializer with all stub types registered for polymorphic serialization.
 */
fun createTestJSON(): Json = createDefaultJSON( STUBS_SERIAL_MODULE )

/**
 * Replace the type name of [data] in this JSON string with [unknownTypeName].
 */
fun String.makeUnknown(
    data: StubDataPoint,
    unknownTypeName: String = "com.unknown.UnknownData"
): String =
    this.makeUnknown( data, Data::class, "data", data.data, unknownTypeName )

/**
 * Replace the type name of [deviceConfiguration] in this JSON string with [unknownTypeName].
 */
fun String.makeUnknown(
    deviceConfiguration: AnyDeviceConfiguration,
    unknownTypeName: String = "com.unknown.UnknownDeviceConfiguration"
): String =
    this.makeUnknown( deviceConfiguration, DeviceConfiguration::class, "roleName", deviceConfiguration.roleName, unknownTypeName )

/**
 * Replace the type name of [primaryDeviceConfiguration] in this JSON string with [unknownTypeName].
 */
fun String.makeUnknown(
    primaryDeviceConfiguration: AnyPrimaryDeviceConfiguration,
    unknownTypeName: String = "com.unknown.UnknownPrimaryDeviceConfiguration"
): String =
    this.makeUnknown( primaryDeviceConfiguration, PrimaryDeviceConfiguration::class, "roleName", primaryDeviceConfiguration.roleName, unknownTypeName )

/**
 * Replace the type name of [registration] in this JSON string with [unknownTypeName].
 */
fun String.makeUnknown(
    registration: DeviceRegistration,
    unknownTypeName: String = "com.unknown.UnknownDeviceRegistration"
): String =
    this.makeUnknown( registration, DeviceRegistration::class, "deviceId", registration.deviceId, unknownTypeName )

/**
 * Replace the type name of [taskConfiguration] in this JSON string with [unknownTypeName].
 */
fun String.makeUnknown(
    taskConfiguration: TaskConfiguration<*>,
    unknownTypeName: String = "com.unknown.UnknownTaskConfiguration"
): String =
    this.makeUnknown( taskConfiguration, TaskConfiguration::class, "name", taskConfiguration.name, unknownTypeName )

/**
 * Replace the type name of the [samplingConfiguration] with the specified [key] set to [value] in this JSON string with [unknownTypeName].
 */
fun String.makeUnknown(
    samplingConfiguration: SamplingConfiguration,
    key: String,
    value: String,
    unknownTypeName: String = "com.unknown.UnknownSamplingConfiguration"
): String =
    this.makeUnknown( samplingConfiguration, SamplingConfiguration::class, key, value, unknownTypeName )

/**
 * Replace the type name of the [trigger] with the specified [key] set to [value] in this JSON string with [unknownTypeName].
 */
fun String.makeUnknown(
    trigger: TriggerConfiguration<*>,
    key: String,
    value: String,
    unknownTypeName: String = "com.unknown.UnknownTriggerConfiguration"
): String =
    this.makeUnknown( trigger, TriggerConfiguration::class, key, value, unknownTypeName )

@OptIn( ExperimentalSerializationApi::class )
private fun <T : Any> String.makeUnknown(
    instance: T,
    klass: KClass<T>,
    key: String,
    value: String,
    unknownTypeName: String
): String
{
    // Get qualified type name.
    val serialModule = COMMON_SERIAL_MODULE + STUBS_SERIAL_MODULE
    val serializer = serialModule.getPolymorphic( klass, instance )
    val qualifiedName = serializer!!.descriptor.serialName

    // Construct regex to identify the object with the qualified name, and with the matching key/value pair set.
    // TODO: This regex uses negative lookahead to filter out JSON which contains multiple types with the same name.
    //       This is complex, and furthermore not 100% foolproof in rare cases (e.g., if the string is used not as a type name).
    //       Probably this should be rewritten with a JSON parser.
    val escapedQualifiedName = qualifiedName.replace( ".", "\\." )
    val objectRegex = Regex( "(\\{\"__type\":\")($escapedQualifiedName)(\",(?!.*?$escapedQualifiedName.*?\"$key\":\"$value\").*?\"$key\":\"$value\".*?\\})" )

    // Replace type name with an unknown type name to mimic it is not available at runtime.
    val match = objectRegex.find( this )
    require( match != null && match.groups.count() == 4 ) { "Could not find the specified object in the serialized string." }
    return this.replace( objectRegex, "$1$unknownTypeName$3" )
}
