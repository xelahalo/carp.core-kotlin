package dk.cachet.carp.protocols.domain.tasks

import dk.cachet.carp.protocols.domain.tasks.measures.Measure
import kotlinx.serialization.Serializable


@Serializable
data class StubTaskDescriptor(
    override val name: String = "Stub task",
    override val measures: List<Measure> = listOf() ) : TaskDescriptor()