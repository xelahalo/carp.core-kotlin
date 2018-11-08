package dk.cachet.carp.deployment.application

import dk.cachet.carp.common.UUID
import dk.cachet.carp.deployment.domain.*
import dk.cachet.carp.protocols.domain.*
import dk.cachet.carp.protocols.domain.devices.DeviceRegistration


/**
 * Application service which allows instantiating [StudyProtocol]'s as [Deployment]'s.
 */
class DeploymentManager( private val repository: DeploymentRepository )
{
    /**
     * Instantiate a deployment for a given [StudyProtocolSnapshot].
     *
     * @throws InvalidConfigurationError when [protocol] is invalid.
     * @return The [DeploymentStatus] of the newly created deployment.
     */
    fun createDeployment( protocol: StudyProtocolSnapshot ): DeploymentStatus
    {
        val newDeployment = Deployment( protocol )

        repository.add( newDeployment )

        return newDeployment.getStatus()
    }

    /**
     * Get the status for a deployment with the given [deploymentId].
     *
     * @param deploymentId The id of the [Deployment] to return [DeploymentStatus] for.
     *
     * @throws IllegalArgumentException when a deployment with [deploymentId] does not exist.
     */
    fun getDeploymentStatus( deploymentId: UUID ): DeploymentStatus
    {
        val deployment: Deployment = repository.getBy( deploymentId )

        return deployment.getStatus()
    }

    /**
     * Register the device with the specified [deviceRoleName] for the deployment with [deploymentId].
     *
     * @param deploymentId The id of the [Deployment] to register the device for.
     * @param deviceRoleName The role name of the device in the deployment to register.
     * @param registration A matching configuration for the device with [deviceRoleName].
     *
     * @throws IllegalArgumentException when a deployment with [deploymentId] does not exist,
     * [deviceRoleName] is not present in the deployment or is already registered,
     * or [registration] is invalid for the specified device or uses a device ID which has already been used as part of registration of a different device.
     */
    fun registerDevice( deploymentId: UUID, deviceRoleName: String, registration: DeviceRegistration ): DeploymentStatus
    {
        val deployment = repository.getBy( deploymentId )

        val device = deployment.registrableDevices.firstOrNull { it.device.roleName == deviceRoleName }
            ?: throw IllegalArgumentException( "The specified device role name could not be found in the deployment." )
        deployment.registerDevice( device.device, registration )

        repository.update( deployment )

        return deployment.getStatus()
    }
}