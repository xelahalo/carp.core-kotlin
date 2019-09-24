package dk.cachet.carp.common


actual class UUID actual constructor( actual val stringRepresentation: String )
{
    init
    {
        require( UUIDRegex.matches( stringRepresentation ) ) { "Invalid UUID string representation." }
    }

    actual companion object
    {
        actual fun randomUUID(): UUID
        {
            // It does not seem like JS can generate true UUIDs, but this is a best effort:
            // https://stackoverflow.com/questions/105034/create-guid-uuid-in-javascript
            // Regardless, we do not need to support UUID generation in JS, as this is used as client-side code.
            // The only real reason for providing a best effort implementation here is to be able to run unit tests.
            val uuidString = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace( Regex( "[xy]" ) ) { match ->
                val random = js( "Math.random() * 16 | 0" )
                val char = if ( match.value == "x" ) random else js( "random & 0x3 | 0x8" )
                char.toString( 16 ) as CharSequence
            }

            return UUID( uuidString )
        }
    }

    override fun toString(): String
    {
        return stringRepresentation
    }

    override fun equals( other: Any? ): Boolean
    {
        if ( this === other ) return true
        if ( other !is UUID ) return false

        return stringRepresentation == other.stringRepresentation
    }

    override fun hashCode(): Int
    {
        return stringRepresentation.hashCode()
    }
}