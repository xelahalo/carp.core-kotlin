package dk.cachet.carp.common.application.users

import dk.cachet.carp.common.application.data.input.InputDataType
import kotlinx.serialization.Serializable


/**
 * Describes a participant [attribute] to be [inputBy] specified users in a study.
 */
@Serializable
data class ExpectedParticipantData( val attribute: ParticipantAttribute, val inputBy: InputBy = InputBy.Anyone )
{
    @Serializable
    sealed class InputBy
    {
        @Serializable
        object Anyone : InputBy()

        @Serializable
        class Roles( val roleNames: Set<String> ) : InputBy()
    }

    val inputDataType: InputDataType
        get() = attribute.inputDataType
}


/**
 * Determines whether the set contains any of the following conflicts:
 * - contains differing participant attributes with the same input type
 * - contains multiple attributes of the same input type which can be input by the same role
 *
 * @throws IllegalArgumentException if [exceptionOnConflict] is set to true and the set contains a conflict.
 */
fun Set<ExpectedParticipantData>.hasNoConflicts( exceptionOnConflict: Boolean = false ): Boolean
{
    val expectedDataByInputType = this.groupBy { it.inputDataType }

    // Check for differing `ParticipantAttribute`s with the same input type.
    val noConflictingAttributes = expectedDataByInputType
        .all {
            val firstAttribute: ParticipantAttribute? = it.value.firstOrNull()?.attribute
            it.value.all { expectedData -> expectedData.attribute == firstAttribute }
        }
    if ( exceptionOnConflict )
    {
        require( noConflictingAttributes )
            { "Expected data contains differing participant attributes with the same input type." }
    }

    // Check for multiple attributes of the same input type which can be input by the same role.
    val noMultipleInputType = expectedDataByInputType
        .all { (_, expectedData) ->
            if ( ExpectedParticipantData.InputBy.Anyone in expectedData.map { it.inputBy } )
            {
                // Any additional expected data would specify roles and thus conflict with the `Anyone` configuration.
                expectedData.size == 1
            }
            else
            {
                // Duplicates indicate the same role is configured to input the same input type multiple times.
                val canBeInputBy = expectedData.flatMap {
                    (it.inputBy as ExpectedParticipantData.InputBy.Roles).roleNames
                }
                canBeInputBy.size == canBeInputBy.distinct().size
            }
        }
    if ( exceptionOnConflict )
    {
        require( noMultipleInputType )
            { "Expected data contains attributes of the same input type which can be input by the same role." }
    }

    return noConflictingAttributes && noMultipleInputType
}