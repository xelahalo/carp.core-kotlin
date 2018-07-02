package dk.cachet.carp.protocols.domain.data

import dk.cachet.carp.protocols.domain.InvalidConfigurationError
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test


/**
 * Tests for [DataType].
 */
class DataTypeTest
{
    @Test
    fun `mutable implementation triggers exception`()
    {
        class NoDataClass( override val category: DataCategory = DataCategory.Other ) : DataType()

        assertFailsWith<InvalidConfigurationError>
        {
            NoDataClass()
        }
    }
}