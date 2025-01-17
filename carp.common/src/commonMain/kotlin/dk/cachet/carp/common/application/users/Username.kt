package dk.cachet.carp.common.application.users

import dk.cachet.carp.common.infrastructure.serialization.createCarpStringPrimitiveSerializer
import kotlinx.serialization.*


/**
 * A unique name which identifies an account.
 */
@Serializable( UsernameSerializer::class )
data class Username( val name: String )
{
    override fun toString(): String = name
}


/**
 * A custom serializer for [Username].
 */
object UsernameSerializer : KSerializer<Username> by createCarpStringPrimitiveSerializer( { Username( it ) } )
