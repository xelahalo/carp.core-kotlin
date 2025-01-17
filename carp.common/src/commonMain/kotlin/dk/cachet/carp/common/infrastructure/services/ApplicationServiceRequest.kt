package dk.cachet.carp.common.infrastructure.services

import dk.cachet.carp.common.application.services.ApiVersion
import dk.cachet.carp.common.application.services.ApplicationService
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Required


/**
 * A request for [TService] stored in memory, which can be invoked using [invokeOn].
 */
interface ApplicationServiceRequest<TService : ApplicationService<TService, *>, out TReturn>
{
    @Required
    val apiVersion: ApiVersion

    fun getResponseSerializer(): KSerializer<out TReturn>
    suspend fun invokeOn( service: TService ): TReturn
}
