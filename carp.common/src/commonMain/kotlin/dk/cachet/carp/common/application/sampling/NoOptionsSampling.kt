package dk.cachet.carp.common.application.sampling

import dk.cachet.carp.common.application.data.DataType
import kotlinx.serialization.Serializable


/**
 * Sampling scheme which does not allow any sampling configuration.
 */
class NoOptionsSamplingScheme( dataType: DataType ) :
    DataTypeSamplingScheme<NoOptionsSamplingConfigurationBuilder>( dataType )
{
    override fun createSamplingConfigurationBuilder(): NoOptionsSamplingConfigurationBuilder =
        NoOptionsSamplingConfigurationBuilder
}


/**
 * A sampling configuration which does not provide any configuration options.
 */
@Serializable
object NoOptionsSamplingConfiguration : SamplingConfiguration


/**
 * A [SamplingConfiguration] builder for [DataTypeSamplingScheme]s which cannot be configured.
 */
object NoOptionsSamplingConfigurationBuilder : SamplingConfigurationBuilder
{
    override fun build(): NoOptionsSamplingConfiguration = NoOptionsSamplingConfiguration
}