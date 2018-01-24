package bhrp.studyprotocols.domain.common

import org.junit.jupiter.api.*
import kotlin.test.*


/**
 * Tests for [Immutable].
 *
 * TODO: Verify whether this works for generics (with type parameters), nullable types, and other potential special cases.
 */
class ImmutableTest
{
    /**
     * A correct implementation of [Immutable] because it is a data class and only contains immutable properties.
     */
    private data class ValidImmutable( val validMember: String = "Valid" )

    /**
     * An incorrect implementation of [Immutable] because it is not defined as a data class.
     */
    private class NoDataClass : Immutable()

    @Test
    fun `implementations should be data classes`()
    {
        ValidImmutable()

        // Invalid implementation (not a data class).
        assertFailsWith<Immutable.NotImmutableError>
        {
            NoDataClass()
        }
    }


    /**
     * An incorrect implementation of [Immutable] because it contains a mutable property.
     */
    private data class ContainsVar( var invalidVar: String = "Invalid" ) : Immutable()

    private data class TypeWithVar( var invalidVar: String )
    /**
     * An incorrect implementation of [Immutable] because its member contains a mutable property.
     */
    private data class ContainsRecursiveVar( val containsVar: TypeWithVar = TypeWithVar("Invalid" ) ) : Immutable()

    @Test
    fun `implementations should only contain immutable properties`()
    {
        // All members need to be defined as 'val'.
        assertFailsWith<Immutable.NotImmutableError>
        {
            ContainsVar()
        }

        // All members (recursively) need to be defined as 'val'.
        assertFailsWith<Immutable.NotImmutableError>
        {
            ContainsRecursiveVar()
        }
    }


    abstract class AbstractImmutable : Immutable()
    private data class ImplementsImmutable( val oneMember: String ) : AbstractImmutable()
    private data class ContainsImmutable( val abstractMember: AbstractImmutable ) : Immutable()

    @Test
    fun `implementations may contain properties which guarantee immutability by deriving from Immutable`()
    {
        ContainsImmutable( ImplementsImmutable( "" ) )
    }
}