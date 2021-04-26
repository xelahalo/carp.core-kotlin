# carp.common [![Maven Central](https://maven-badges.herokuapp.com/maven-central/dk.cachet.carp.common/carp.common/badge.svg?color=orange)](https://mvnrepository.com/artifact/dk.cachet.carp.common) [![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/dk.cachet.carp.common/carp.common?server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/dk/cachet/carp/common/)

Implements helper classes and base types relied upon by all subsystems.
Primarily, this contains the [built-in types](#built-in-types) used to [define study protocols](carp-protocols.md#domain-objects)
which subsequently get passed to the deployments and clients subsystem.

## Built-in types

### Data types

`DataType`s are identified by a given _name_ within a _namespace_ and prescribe the data contained within each data point when measured.
When a data type describes data over the course of a time interval, the time interval is stored within the header (shared by all data types) and not in data-type specific data.

All of the built-in data types belong to the namespace: **dk.cachet.carp**.

| Name | Description |
| --- | --- |
| [geolocation](../carp.common/src/commonMain/kotlin/dk/cachet/carp/common/application/data/Geolocation.kt) | Geographic location data, representing longitude and latitude. |
| [stepcount](../carp.common/src/commonMain/kotlin/dk/cachet/carp/common/application/data/StepCount.kt) | The number of steps a participant has taken in a specified time interval. |

### Device descriptors

| Class | Master | Description |
| --- | :---: | --- |
| [Smartphone](../carp.common/src/commonMain/kotlin/dk/cachet/carp/common/application/devices/Smartphone.kt) | Yes | An internet-connected phone with built-in sensors. |
| [AltBeacon](../carp.common/src/commonMain/kotlin/dk/cachet/carp/common/application/devices/AltBeacon.kt) | | A beacon meeting the open AltBeacon standard. |
| [BLEHeartRateSensor](../carp.common/src/commonMain/kotlin/dk/cachet/carp/common/application/devices/BLEHeartRateSensor.kt) | | A Bluetooth device which implements a Heart Rate service. |
| [CustomProtocolDevice](../carp.common/src/commonMain/kotlin/dk/cachet/carp/common/application/devices/CustomProtocolDevice.kt) | Yes | A master device which uses a single `CustomProtocolTask` to determine how to run a study on the device. |

### Tasks

| Class | Description |
| --- | --- |
| [ConcurrentTask](../carp.common/src/commonMain/kotlin/dk/cachet/carp/common/application/tasks/ConcurrentTask.kt) | Specifies that all containing measures should start immediately once triggered and run indefinitely until all containing measures have completed. |
| [CustomProtocolTask](../carp.common/src/commonMain/kotlin/dk/cachet/carp/common/application/tasks/CustomProtocolTask.kt) | Contains a definition on how to run tasks, measures, and triggers which differs from the CARP domain model. |

### Triggers

| Class | Description |
| --- | --- |
| [ElapsedTimeTrigger](../carp.common/src/commonMain/kotlin/dk/cachet/carp/common/application/triggers/ElapsedTimeTrigger.kt) | Triggers after a specified amount of time has elapsed since the start of a study deployment. |
| [ScheduledTrigger](../carp.common/src/commonMain/kotlin/dk/cachet/carp/common/application/triggers/ScheduledTrigger.kt) | Trigger using a recurring schedule starting on the date that the study starts, specified using [the iCalendar recurrence rule standard](https://icalendar.org/iCalendar-RFC-5545/3-8-5-3-recurrence-rule.html). |
| [ManualTrigger](../carp.common/src/commonMain/kotlin/dk/cachet/carp/common/application/triggers/ManualTrigger.kt) | Initiated by a user, i.e., the user decides when to start a task. |