# Pachyderm Client for Scala

This is a Scala client for [Pachyderm](https://www.pachyderm.io/).
It uses a modified version of the [pfs.proto](https://github.com/pachyderm/pachyderm/blob/master/src/client/pfs/pfs.proto) file
to automatically generate an asyncrhonous Future-based gRPC client.

Currently the client is pretty barebones to the Protobuf-based client,
but we will be adding some nice abstractions and utilities to make
things like committing multiple files asynchronously easy. In addition,
we will create an Akka-based version of the client that will allow for
truly asynchronous IO and easy integration with Akka-stream based
applications.

## Installation

`pachyderm-client` is cross-compiled for Scala 2.11 and 2.12 and is
available on Maven Central for release versions and OSS Sonatype
Snapshots for snapshot versions.

### Snapshot Versions

```sbt
// Add OSS Sonatype Snapshots repository
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

// Add pachyderm-client dependency
libraryDependencies ++= "ml.combust.pachyderm" %% "pachyderm-client" % "0.1.0-SNAPSHOT"
```

### Release Versions

Releases are hosted on Maven Central, which is a default SBT resolver.

```sbt
// Add pachyderm-client dependency
libraryDependencies ++= "ml.combust.pachyderm" %% "pachyderm-client" % "0.1.0"
```

## Basic Usage

To test locally, follow the [instruction](http://pachyderm.readthedocs.io/en/stable/getting_started/local_installation.html) for getting a local Pachyderm
cluster running. After you have Pachyderm running locally, make sure you
have port forwarding enabled.

```bash
pachctl port-forward &
```

By default Pachyderm runs on port 30650 and the IP address of the
Pachyderm daemon can be determined using:

```bash
minikube ip
```

Once you have the port and host IP for the Pachyderm daemon, we can
connect to it using the Scala client.

```scala
import sys.process.Process
import ml.combust.pachyderm.client.Client

// Use a shell process to get the host IP for Pachyderm
val hostIp = Process("minikube ip").lineStream.head

// Use default port to connect
val port = 30650

// Connect our gRPC client to Pachyderm
val client = Client(hostIp, port)

// Create a repo
client.createRepo("my_test_repo")

// List all repos
client.listRepo()

// Inspect a repo
client.inspectRepo("my_test_repo")

// Delete a repo
client.deleteRepo("my_test_repo")
```

### Working with Commits

TODO: Still working on nice utilities for simplifying commits

### Block API

TODO: Still need to make the Block API integration
