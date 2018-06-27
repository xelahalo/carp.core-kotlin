package dk.cachet.carp.protocols.domain.tasks

import kotlinx.serialization.Serializable


/**
 * A [TaskDescriptor] which specifies that all containing measures and/or outputs should start immediately once triggered.
 */
@Serializable
data class StartAllTask(
    override val name: String,
    override val measures: List<Measure> ) : TaskDescriptor()