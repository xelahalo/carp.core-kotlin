declare module 'carp.deployment.core'
{
    import { kotlin } from 'kotlin'
    import ArrayList = kotlin.collections.ArrayList
    import HashSet = kotlin.collections.HashSet
    import { dk as cdk } from 'carp.common'
    import UUID = cdk.cachet.carp.common.UUID


    namespace dk.cachet.carp.deployment.domain
    {
        class DeviceDeploymentStatus
        {
            static get Companion(): DeviceDeploymentStatus$Companion
        }
        interface DeviceDeploymentStatus$Companion { serializer(): any }

        namespace DeviceDeploymentStatus
        {
            class Unregistered
            {
                constructor(
                    device: any,
                    requiresDeployment: Boolean,
                    remainingDevicesToRegisterBeforeDeployment: HashSet<String> )

                readonly device: any
                readonly requiresDeployment: Boolean
                readonly remainingDevicesToRegisterBeforeDeployment: HashSet<String>
            }
            class Registered
            {
                constructor(
                    device: any,
                    requiresDeployment: Boolean,
                    remainingDevicesToRegisterBeforeDeployment: HashSet<String> )

                readonly device: any
                readonly requiresDeployment: Boolean
                readonly remainingDevicesToRegisterBeforeDeployment: HashSet<String>
            }
            class Deployed
            {
                constructor( device: any )

                readonly device: any
                readonly requiresDeployment: Boolean
            }
            class NeedsRedeployment
            {
                constructor(
                    device: any,
                    remainingDevicesToRegisterBeforeDeployment: HashSet<String> )

                readonly device: any
                readonly requiresDeployment: Boolean
                readonly remainingDevicesToRegisterBeforeDeployment: HashSet<String>
            }
        }


        class StudyDeploymentStatus
        {
            static get Companion(): StudyDeploymentStatus$Companion
        }
        interface StudyDeploymentStatus$Companion { serializer(): any }

        namespace StudyDeploymentStatus
        {
            class Invited
            {
                constructor( studyDeploymentId: UUID, devicesStatus: ArrayList<DeviceDeploymentStatus> )

                readonly studyDeploymentId: UUID
                readonly devicesStatus: ArrayList<DeviceDeploymentStatus>
            }
            class DeployingDevices
            {
                constructor( studyDeploymentId: UUID, devicesStatus: ArrayList<DeviceDeploymentStatus> )

                readonly studyDeploymentId: UUID
                readonly devicesStatus: ArrayList<DeviceDeploymentStatus>
            }
            class DeploymentReady
            {
                constructor( studyDeploymentId: UUID, devicesStatus: ArrayList<DeviceDeploymentStatus> )

                readonly studyDeploymentId: UUID
                readonly devicesStatus: ArrayList<DeviceDeploymentStatus>
            }
            class Stopped
            {
                constructor( studyDeploymentId: UUID, devicesStatus: ArrayList<DeviceDeploymentStatus> )

                readonly studyDeploymentId: UUID
                readonly devicesStatus: ArrayList<DeviceDeploymentStatus>
            }
        }
    }


    namespace dk.cachet.carp.deployment.domain.users
    {
        class Participation
        {
            constructor( studyDeploymentId: UUID, id?: UUID )

            static get Companion(): Participation$Companion

            readonly studyDeploymentId: UUID
            readonly id: UUID
        }
        interface Participation$Companion { serializer(): any }


        class StudyInvitation
        {
            constructor( name: string, description: string )

            static get Companion(): StudyInvitation$Companion

            readonly name: string
            readonly description: string
        }
        interface StudyInvitation$Companion
        {
            serializer(): any;
            empty(): StudyInvitation;
        }
    }
}