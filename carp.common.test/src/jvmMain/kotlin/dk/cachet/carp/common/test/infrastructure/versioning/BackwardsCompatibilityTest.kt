package dk.cachet.carp.common.test.infrastructure.versioning

import dk.cachet.carp.common.application.ApplicationServiceInfo
import dk.cachet.carp.common.application.services.ApiVersion
import dk.cachet.carp.common.application.services.ApplicationService
import dk.cachet.carp.common.application.services.EventBus
import dk.cachet.carp.common.application.services.IntegrationEvent
import dk.cachet.carp.common.infrastructure.services.ApplicationServiceRequest
import dk.cachet.carp.common.infrastructure.test.createTestJSON
import dk.cachet.carp.common.infrastructure.versioning.ApplicationServiceApiMigrator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.*
import kotlinx.serialization.builtins.*
import kotlinx.serialization.json.*
import org.apache.commons.io.FileUtils
import java.io.File
import kotlin.reflect.KClass
import kotlin.test.*


/**
 * Tests whether old API requests are handled correctly by migrating them to the current API version,
 * and transforming the response to be compatible with the old request.
 */
@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
@Suppress( "FunctionName" )
abstract class BackwardsCompatibilityTest<TService : ApplicationService<TService, *>>(
    applicationServiceKlass: KClass<TService>
)
{
    private val serviceInfo = ApplicationServiceInfo.of( applicationServiceKlass.java )
    private val currentVersion = serviceInfo.apiVersion

    private val testRequestsFolder: File =
        File( "src/commonTest/resources/test-requests/${serviceInfo.serviceName}" )
    private lateinit var availableTestVersions: List<ApiVersion>
    private val loggedRequestsSerializer = ListSerializer( serializer<LoggedJsonRequest>() )


    @BeforeTest
    fun setup()
    {
        // Get available test versions.
        val directories = testRequestsFolder.listFiles()?.filter { it.isDirectory }.orEmpty()
        availableTestVersions = directories.map {
            val versionMatch = assertNotNull(
                Regex( """(\d)\.(\d)""" ).find( it.name ),
                "Unexpected version folder in \"$testRequestsFolder\": ${it.name}"
            )
            val version = ApiVersion(
                major = versionMatch.groups[ 1 ]!!.value.toInt(),
                minor = versionMatch.groups[ 2 ]!!.value.toInt()
            )
            assertFalse(
                version.isMoreRecent( currentVersion ),
                "Impossible to have test sources for version \"$version\" " +
                "which is more recent than the current API version \"$currentVersion\"."
            )
            version
        }
    }


    abstract fun createService(): Pair<TService, EventBus>

    private val json = createTestJSON()

    /**
     * Before releasing a new version, you need to copy the output of `OutputTestRequests`
     * to test resources under a matching version folder to make this test pass.
     */
    @Test
    fun versioned_test_requests_for_current_api_version_available()
    {
        val version = currentVersion.toString()
        val testDirectory = File( testRequestsFolder, version )
        assertTrue(
            testDirectory.exists(),
            "No test request sources for version \"$version\" found at \"${testDirectory.absolutePath}\"."
        )

        for ( file in FileUtils.listFiles( testDirectory, arrayOf( "json" ), true ) )
        {
            val requests = json.decodeFromString( loggedRequestsSerializer, file.readText() )
            val requestVersionField = ApplicationServiceRequest<*, *>::apiVersion.name
            assertTrue(
                requests.all { it.request[ requestVersionField ]?.jsonPrimitive?.isString == true },
                "Not all request objects in \"${file.absolutePath}\" are versioned with \"$requestVersionField\"."
            )
            val eventVersionField = IntegrationEvent<*>::apiVersion.name
            assertTrue(
                requests
                    .flatMap { it.precedingEvents + it.publishedEvents }
                    .all { (it as? JsonObject)?.get( eventVersionField )?.jsonPrimitive?.isString == true },
                "Not all events in \"${file.absolutePath}\" are versioned with \"$eventVersionField\"."
            )
        }
    }

    @Test
    fun can_replay_backwards_compatible_test_requests() = runTest {
        val compatibleTests = availableTestVersions.filter { it.major == currentVersion.major }

        val testFiles = compatibleTests.flatMap { version ->
            val testDirectory = File( testRequestsFolder, version.toString() )
            FileUtils.listFiles( testDirectory, arrayOf( "json" ), true )
        }

        testFiles
            .associateWith { json.decodeFromString( loggedRequestsSerializer, it.readText() ) }
            .forEach { replayLoggedRequests( it.key.absolutePath, it.value ) }
    }


    @Suppress( "UNCHECKED_CAST" )
    private suspend fun replayLoggedRequests( fileName: String, loggedRequests: List<LoggedJsonRequest> )
    {
        val (service, eventBus) = createService()
        val apiMigrator = serviceInfo.apiMigrator as ApplicationServiceApiMigrator<TService>

        loggedRequests.forEachIndexed { index, logged ->
            val replayErrorBase = "Couldn't replay requests in: $fileName. Request #${index + 1}"

            // Publish preceding events.
            logged.precedingEvents.map { it as JsonObject }.forEach {
                // Migrate event using API migrator of dependent service.
                val eventType = it[ json.configuration.classDiscriminator ]!!.jsonPrimitive.content
                val publisher: ApplicationServiceInfo = checkNotNull( serviceInfo.getEventPublisher( eventType ) )
                    { "The event \"$eventType\" isn't an expected event processed by \"${serviceInfo.serviceKlass}\"." }
                val event = publisher.apiMigrator.migrateEvent( json, it )

                // Cast to bypass generic constraint checking. We know the types line up.
                eventBus.publish( publisher.serviceKlass.kotlin as KClass<Nothing>, event as IntegrationEvent<Nothing> )
            }

            // Validate whether request outcome corresponds to log.
            val request = apiMigrator.migrateRequest( json, logged.request )
            val response: JsonElement? =
                try { request.invokeOn( service ) }
                catch ( ex: Exception )
                {
                    if ( logged !is LoggedJsonRequest.Failed ) throw ex
                    else assertEquals(
                        logged.exceptionType,
                        ex::class.simpleName,
                        "$replayErrorBase failed with the wrong exception type."
                    )
                    null
                }

            // Validate response in case request should succeed.
            if ( logged is LoggedJsonRequest.Succeeded )
            {
                assertEquals( logged.response, response, "$replayErrorBase returned the wrong response." )
            }
        }
    }
}
