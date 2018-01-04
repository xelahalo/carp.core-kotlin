package bhrp.studyprotocol.domain.devices

import bhrp.studyprotocol.domain.*
import org.junit.jupiter.api.*
import kotlin.test.*


/**
 * Tests for [DeviceDescriptor].
 */
class DeviceDescriptorTest
{
    @Test
    fun `mutable implementation triggers exception`()
    {
        class NoDataClass( override val roleName: String = "Not a data class" ) : DeviceDescriptor()

        assertFailsWith<InvalidConfigurationError>
        {
            NoDataClass()
        }
    }
}