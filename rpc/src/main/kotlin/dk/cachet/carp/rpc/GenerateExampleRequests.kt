package dk.cachet.carp.rpc

import dk.cachet.carp.common.application.services.ApplicationService


/**
 * Generate a single [ExampleRequest] using the corresponding request object for each of the methods in
 * application service [AS].
 */
fun <AS : ApplicationService<AS, *>> generateExampleRequests(
    applicationServiceInterface: Class<out ApplicationService<AS, *>>,
    requestObjectSuperType: Class<*>
): List<ExampleRequest<AS>>
{
    val requests = applicationServiceInterface.methods
    val requestObjects = requestObjectSuperType.classes

    return requests.map { request ->
        val requestObjectName = request.name.replaceFirstChar { it.uppercase() }
        val requestObject = requestObjects.singleOrNull { it.simpleName == requestObjectName }
        requireNotNull( requestObject )
            {
                "Could not find request object for ${applicationServiceInterface.name}.${request.name}. " +
                "Searched for: ${requestObjectSuperType.name}.$requestObjectName"
            }

        // TODO: For each request object, generate example JSON request and response.
        val requestObjectJson = ""
        val responseJson = ""

        ExampleRequest(
            applicationServiceInterface,
            request,
            ExampleRequest.JsonExample( requestObject, requestObjectJson ),
            ExampleRequest.JsonExample( request.returnType, responseJson )
        )
    }
}
