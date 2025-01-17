import VerifyModule from './VerifyModule'

import { kotlin } from 'kotlin'
import ArrayList = kotlin.collections.ArrayList
import toMap = kotlin.collections.toMap_v2dak7$
import toSet = kotlin.collections.toSet_us0mfu$
import toList = kotlin.collections.toList_us0mfu$

import { kotlinx as kxd } from 'Kotlin-DateTime-library-kotlinx-datetime-js-legacy'
import Clock = kxd.datetime.Clock

import { dk as dkc } from 'carp.core-kotlin-carp.common'
import UUID = dkc.cachet.carp.common.application.UUID
import DefaultDeviceRegistration = dkc.cachet.carp.common.application.devices.DefaultDeviceRegistration
import Smartphone = dkc.cachet.carp.common.application.devices.Smartphone
import Username = dkc.cachet.carp.common.application.users.Username
import UsernameAccountIdentity = dkc.cachet.carp.common.application.users.UsernameAccountIdentity
import AssignedTo = dkc.cachet.carp.common.application.users.AssignedTo

import { dk } from 'carp.core-kotlin-carp.deployments.core'
import DeviceDeploymentStatus = dk.cachet.carp.deployments.application.DeviceDeploymentStatus
import PrimaryDeviceDeployment = dk.cachet.carp.deployments.application.PrimaryDeviceDeployment
import StudyDeploymentStatus = dk.cachet.carp.deployments.application.StudyDeploymentStatus
import ActiveParticipationInvitation = dk.cachet.carp.deployments.application.users.ActiveParticipationInvitation
import AssignedPrimaryDevice = dk.cachet.carp.deployments.application.users.AssignedPrimaryDevice
import ParticipantData = dk.cachet.carp.deployments.application.users.ParticipantData
import RoleData = dk.cachet.carp.deployments.application.users.ParticipantData.RoleData
import Participation = dk.cachet.carp.deployments.application.users.Participation
import ParticipantInvitation = dk.cachet.carp.deployments.application.users.ParticipantInvitation
import ParticipantStatus = dk.cachet.carp.deployments.application.users.ParticipantStatus
import StudyInvitation = dk.cachet.carp.deployments.application.users.StudyInvitation
import DeploymentServiceRequest = dk.cachet.carp.deployments.infrastructure.DeploymentServiceRequest
import ParticipationServiceRequest = dk.cachet.carp.deployments.infrastructure.ParticipationServiceRequest


describe( "carp.deployments.core", () => {
    it( "verify module declarations", async () => {
        const now = Clock.System.now()
        const exampleDevice = new Smartphone( "test", toMap( [] ) )
        const studyInvitation = new StudyInvitation( "Some study" )
        
        const instances = [
            DeviceDeploymentStatus.Companion,
            [ "DeviceDeploymentStatus", new DeviceDeploymentStatus.Unregistered( null, true, toSet( [] ), toSet( [] ) ) ],
            new DeviceDeploymentStatus.Unregistered( null, true, toSet( [] ), toSet( [] ) ),
            new DeviceDeploymentStatus.Registered( null, true, toSet( [] ), toSet( [] ) ),
            new DeviceDeploymentStatus.Deployed( null ),
            new DeviceDeploymentStatus.NeedsRedeployment( null, toSet( [] ), toSet( [] ) ),
            [ "DeviceDeploymentStatus$NotDeployed", new DeviceDeploymentStatus.Unregistered( null, true, toSet( [] ), toSet( [] ) ) ],
            new PrimaryDeviceDeployment(
                exampleDevice,
                new DefaultDeviceRegistration( "some role" ),
                toSet( [] ), toMap( [] ), toSet( [] ), toMap( [] ), toSet( [] ), toSet( [] ), "" ),
            PrimaryDeviceDeployment.Companion,
            [ "StudyDeploymentStatus", new StudyDeploymentStatus.Invited( now, UUID.Companion.randomUUID(), new ArrayList<DeviceDeploymentStatus>( [] ), new ArrayList<ParticipantStatus>( [] ), null ) ],
            new StudyDeploymentStatus.Invited( now, UUID.Companion.randomUUID(), new ArrayList<DeviceDeploymentStatus>( [] ), new ArrayList<ParticipantStatus>( [] ), null ),
            new StudyDeploymentStatus.DeployingDevices( now, UUID.Companion.randomUUID(), new ArrayList<DeviceDeploymentStatus>( [] ), new ArrayList<ParticipantStatus>( [] ), null ),
            new StudyDeploymentStatus.Running( now, UUID.Companion.randomUUID(), new ArrayList<DeviceDeploymentStatus>( [] ), new ArrayList<ParticipantStatus>( [] ), now ),
            new StudyDeploymentStatus.Stopped( now, UUID.Companion.randomUUID(), new ArrayList<DeviceDeploymentStatus>( [] ), new ArrayList<ParticipantStatus>( [] ), null, now ),
            StudyDeploymentStatus.Companion,
            new ActiveParticipationInvitation( new Participation( UUID.Companion.randomUUID() ), studyInvitation, toSet( [] ) ),
            ActiveParticipationInvitation.Companion,
            new AssignedPrimaryDevice( exampleDevice, null ),
            AssignedPrimaryDevice.Companion,
            new ParticipantData( UUID.Companion.randomUUID(), toMap( [] ), toList( [] ) ),
            ParticipantData.Companion,
            new RoleData( "some role", toMap( [] ) ),
            RoleData.Companion,
            new ParticipantInvitation( UUID.Companion.randomUUID(), toSet( [] ), new UsernameAccountIdentity( new Username( "Test" ) ), studyInvitation ),
            ParticipantInvitation.Companion,
            new ParticipantStatus( UUID.Companion.randomUUID(), AssignedTo.All, toSet( [] ) ),
            ParticipantStatus.Companion,
            new Participation( UUID.Companion.randomUUID() ),
            Participation.Companion,
            studyInvitation,
            StudyInvitation.Companion,
            [ "DeploymentServiceRequest", new DeploymentServiceRequest.Stop( UUID.Companion.randomUUID() ) ],
            [ "ParticipationServiceRequest", new ParticipationServiceRequest.GetParticipantData( UUID.Companion.randomUUID() ) ]
        ]

        const moduleVerifier = new VerifyModule( 'carp.core-kotlin-carp.deployments.core', instances )
        await moduleVerifier.verify()
    } )
} )
