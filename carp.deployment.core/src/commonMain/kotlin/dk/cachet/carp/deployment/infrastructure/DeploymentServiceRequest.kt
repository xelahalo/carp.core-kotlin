package dk.cachet.carp.deployment.infrastructure

import dk.cachet.carp.common.application.DateTime
import dk.cachet.carp.common.application.StudyProtocolSnapshot
import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.devices.DeviceRegistration
import dk.cachet.carp.common.infrastructure.services.ServiceInvoker
import dk.cachet.carp.common.infrastructure.services.createServiceInvoker
import dk.cachet.carp.deployment.application.DeploymentService
import dk.cachet.carp.deployment.domain.MasterDeviceDeployment
import dk.cachet.carp.deployment.domain.StudyDeploymentStatus
import kotlinx.serialization.Serializable

private typealias Service = DeploymentService
private typealias Invoker<T> = ServiceInvoker<DeploymentService, T>


/**
 * Serializable application service requests to [DeploymentService] which can be executed on demand.
 */
@Serializable
sealed class DeploymentServiceRequest
{
    @Serializable
    data class CreateStudyDeployment( val protocol: StudyProtocolSnapshot ) :
        DeploymentServiceRequest(),
        Invoker<StudyDeploymentStatus> by createServiceInvoker( Service::createStudyDeployment, protocol )

    @Serializable
    data class RemoveStudyDeployments( val studyDeploymentIds: Set<UUID> ) :
        DeploymentServiceRequest(),
        Invoker<Set<UUID>> by createServiceInvoker( Service::removeStudyDeployments, studyDeploymentIds )

    @Serializable
    data class GetStudyDeploymentStatus( val studyDeploymentId: UUID ) :
        DeploymentServiceRequest(),
        Invoker<StudyDeploymentStatus> by createServiceInvoker( Service::getStudyDeploymentStatus, studyDeploymentId )

    @Serializable
    data class GetStudyDeploymentStatusList( val studyDeploymentIds: Set<UUID> ) :
        DeploymentServiceRequest(),
        Invoker<List<StudyDeploymentStatus>> by createServiceInvoker( Service::getStudyDeploymentStatusList, studyDeploymentIds )

    @Serializable
    data class RegisterDevice(
        val studyDeploymentId: UUID,
        val deviceRoleName: String,
        val registration: DeviceRegistration
    ) : DeploymentServiceRequest(),
        Invoker<StudyDeploymentStatus> by createServiceInvoker( Service::registerDevice, studyDeploymentId, deviceRoleName, registration )

    @Serializable
    data class UnregisterDevice( val studyDeploymentId: UUID, val deviceRoleName: String ) :
        DeploymentServiceRequest(),
        Invoker<StudyDeploymentStatus> by createServiceInvoker( Service::unregisterDevice, studyDeploymentId, deviceRoleName )

    @Serializable
    data class GetDeviceDeploymentFor( val studyDeploymentId: UUID, val masterDeviceRoleName: String ) :
        DeploymentServiceRequest(),
        Invoker<MasterDeviceDeployment> by createServiceInvoker( Service::getDeviceDeploymentFor, studyDeploymentId, masterDeviceRoleName )

    @Serializable
    data class DeploymentSuccessful( val studyDeploymentId: UUID, val masterDeviceRoleName: String, val deviceDeploymentLastUpdateDate: DateTime ) :
        DeploymentServiceRequest(),
        Invoker<StudyDeploymentStatus> by createServiceInvoker( Service::deploymentSuccessful, studyDeploymentId, masterDeviceRoleName, deviceDeploymentLastUpdateDate )

    @Serializable
    data class Stop( val studyDeploymentId: UUID ) :
        DeploymentServiceRequest(),
        Invoker<StudyDeploymentStatus> by createServiceInvoker( Service::stop, studyDeploymentId )
}
