---
title: Home
---
# Releases

This page lists the releases for the scodec modules. The projects adhere to typical Scala-style semantic versioning / binary compatibility. That is, releases that share the same major.minor version support forward binary compatibility. Code that was compiled against version `x.y` will link successfully with `x.y'` when `y < y'`. Note that the inverse is not necessarily true -- when `y > y'`.

Released versions are published to [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.scodec%22) and snapshot versions are published to the [Sonatype OSS Nexus](https://oss.sonatype.org/#nexus-search;gav~org.scodec~~~~). To use snapshot builds from SBT, add the following resolver:

    resolvers += "Sonatype Public" at "https://oss.sonatype.org/content/groups/public/"

## scodec-bits

    libraryDependencies += "org.scodec" %% "scodec-bits" % "1.0.9"

As of 1.0.7, scodec-bits is released for both the JVM and Scala.js.

As of 1.0.5, scodec-bits is released under the org.scodec group id. Prior to 1.0.5, it was released under the org.typelevel group id.

Version | Changes | ScalaDoc | Scala | Scala.js
--------|---------|----------|-------|---------
1.0.9   | [Changes](https://github.com/scodec/scodec-bits/blob/v1.0.9/CHANGELOG.md) | [API](/api/scodec-bits/1.0.9) | 2.10, 2.11, 2.12.0-M1 | 0.6
1.0.7   | [Changes](https://github.com/scodec/scodec-bits/blob/v1.0.7/CHANGELOG.md) | [API](/api/scodec-bits/1.0.7) | 2.10, 2.11, 2.12.0-M1 | 0.6
1.0.6   | [Changes](https://github.com/scodec/scodec-bits/blob/v1.0.6/CHANGELOG.md) | [API](/api/scodec-bits/1.0.6) | 2.10, 2.11 |
1.0.5   | [Changes](https://github.com/scodec/scodec-bits/blob/v1.0.5/CHANGELOG.md) | [API](/api/scodec-bits/1.0.5) | 2.10, 2.11 |
1.0.4   | [Changes](https://github.com/scodec/scodec-bits/blob/v1.0.4/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/bits/stable/1.0.4) | 2.10, 2.11 |
1.0.3   | [Changes](https://github.com/scodec/scodec-bits/blob/v1.0.3/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/bits/stable/1.0.3) | 2.10, 2.11 |
1.0.2   | [Changes](https://github.com/scodec/scodec-bits/blob/v1.0.2/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/bits/stable/1.0.2) | 2.10, 2.11 |
1.0.1   | [Changes](https://github.com/scodec/scodec-bits/blob/v1.0.1/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/bits/stable/1.0.1) | 2.10, 2.11 |
1.0.0   | [Changes](https://github.com/scodec/scodec-bits/blob/v1.0.0/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/bits/stable/1.0.0) | 2.10, 2.11 |

## scodec-core

    libraryDependencies += "org.scodec" %% "scodec-core" % "1.8.1"

    libraryDependencies ++= {
      if (scalaBinaryVersion.value startsWith "2.10")
        Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full))
      else Nil
    }

As of 1.8.0, scodec-core is released for both the JVM and Scala.js.

As of 1.7.0, scodec-core is released under the org.scodec group id. Prior to 1.7.0, it was released under the org.typelevel group id. Also as of 1.7.0, scalaz-core is no longer a dependency -- instead, interop with scalaz-core is now provided by the scodec-scalaz module.

Version | Changes | ScalaDoc | Scala | Scala.js | scodec-bits | scalaz | Shapeless
--------|---------|----------|-------|----------|-------------|--------|-----------
1.8.0   | [Changes](https://github.com/scodec/scodec/blob/v1.8.1/CHANGELOG.md) | [API](http://scodec.org/api/scodec-core/1.8.1) | 2.10, 2.11, 2.12.0-M1 | 0.6 | [1.0,2.0) | none | 2.2.4
1.8.0   | [Changes](https://github.com/scodec/scodec/blob/v1.8.0/CHANGELOG.md) | [API](http://scodec.org/api/scodec-core/1.8.0) | 2.10, 2.11, 2.12.0-M1 | 0.6 | [1.0,2.0) | none | 2.2.2
1.7.2   | [Changes](https://github.com/scodec/scodec/blob/v1.7.2/CHANGELOG.md) | [API](http://scodec.org/api/scodec-core/1.7.2) | 2.10, 2.11 | | [1.0,2.0) | none | 2.1.0
1.7.1   | [Changes](https://github.com/scodec/scodec/blob/v1.7.1/CHANGELOG.md) | [API](http://scodec.org/api/scodec-core/1.7.1) | 2.10, 2.11 | | [1.0,2.0) | none | 2.1.0
1.7.0   | [Changes](https://github.com/scodec/scodec/blob/v1.7.0/CHANGELOG.md) | [API](http://scodec.org/api/scodec-core/1.7.0) | 2.10, 2.11 | | [1.0,2.0) | none | 2.1.0
1.6.0   | [Changes](https://github.com/scodec/scodec/blob/v1.6.0/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.6.0) | 2.10, 2.11 | | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.5.0   | [Changes](https://github.com/scodec/scodec/blob/v1.5.0/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.5.0) | 2.10, 2.11 | | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.4.0   | [Changes](https://github.com/scodec/scodec/blob/v1.4.0/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.4.0) | 2.10, 2.11 | | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.3.2   | [Changes](https://github.com/scodec/scodec/blob/v1.3.2/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.3.2) | 2.10, 2.11 | | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.3.1   | [Changes](https://github.com/scodec/scodec/blob/v1.3.1/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.3.1) | 2.10, 2.11 | | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.3.0   | [Changes](https://github.com/scodec/scodec/blob/v1.3.0/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.3.0) | 2.10, 2.11 | | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.2.2   | [Changes](https://github.com/scodec/scodec/blob/v1.2.2/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.2.2) | 2.10, 2.11 | | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.2.1   | [Changes](https://github.com/scodec/scodec/blob/v1.2.1/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.2.1) | 2.10, 2.11 | | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.2.0   | [Changes](https://github.com/scodec/scodec/blob/v1.2.0/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.2.0) | 2.10, 2.11 | | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.1.0   | [Changes](https://github.com/scodec/scodec/blob/v1.1.0/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.1.0) | 2.10, 2.11 | | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.0.0   | [Changes](https://github.com/scodec/scodec/blob/v1.0.0/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.0.0) | 2.10, 2.11 | | [1.0,2.0) | [7.0,7.1) | 1.2.4

## scodec-scalaz

    libraryDependencies += "org.scodec" %% "scodec-scalaz" % "1.1.0"

The scodec-scalaz module provides interop between scodec-core and scalaz-core.

Version | Changes | ScalaDoc | Scala | scodec-core | scalaz
--------|---------|----------|-------|-------------|--------
1.1.0   | N/A     | [API](http://scodec.org/api/scodec-scalaz/1.1.0) | 2.10, 2.11 | [1.8,1.9) | [7.1,7.2)
1.0.0   | N/A     | [API](http://scodec.org/api/scodec-scalaz/1.0.0) | 2.10, 2.11 | [1.7,1.8) | [7.1,7.2)

## scodec-stream

    libraryDependencies += "org.scodec" %% "scodec-stream" % "0.10.0"

The scodec-stream module provides a streaming layer built on top of scodec-core and scalaz-stream. It provides no binary compatibility guarantees until scalaz-stream 1.0 is released.

Version | Changes | ScalaDoc | Scala | scodec-core | scalaz-stream
--------|---------|----------|-------|-------------|---------------
0.10.0  | N/A     | [API](http://scodec.org/api/scodec-stream/0.10.0) | 2.10, 2.11 | [1.8,1.9) | 0.7a
0.9.0   | N/A     | [API](http://scodec.org/api/scodec-stream/0.9.0)  | 2.10, 2.11 | [1.7,1.8) | 0.7a
0.8.0   | N/A     | [API](http://scodec.org/api/scodec-stream/0.8.0)  | 2.10, 2.11 | [1.7,1.8) | 0.7a
0.7.1   | N/A     | [API](http://scodec.org/api/scodec-stream/0.7.1) | 2.10, 2.11 | [1.7,1.8) | 0.6a
0.7.0   | N/A     | [API](http://scodec.org/api/scodec-stream/0.7.0) | 2.10, 2.11 | [1.7,1.8) | 0.6a

## scodec-protocols

    libraryDependencies += "org.scodec" %% "scodec-protocols" % "0.7.0"

The scodec-protocols module provides implementations of common networking protocols, with a focus on processing libpcap files. It is also a good source of example codecs for real world protocols. It provides no binary compatibility guarantees until scalaz-stream 1.0 is released.

Version | Changes | ScalaDoc | Scala | scodec-stream
--------|---------|----------|-------|---------------
0.10.0  | N/A     | [API](http://scodec.org/api/scodec-protocols/0.10.0)| 2.10, 2.11 | 0.10.0
0.9.0   | N/A     | [API](http://scodec.org/api/scodec-protocols/0.9.0) | 2.10, 2.11 | 0.9.0
0.8.0   | N/A     | [API](http://scodec.org/api/scodec-protocols/0.8.0) | 2.10, 2.11 | 0.8.0
0.7.1   | N/A     | [API](http://scodec.org/api/scodec-protocols/0.7.1) | 2.10, 2.11 | 0.7.1
0.7.1   | N/A     | [API](http://scodec.org/api/scodec-protocols/0.7.1) | 2.10, 2.11 | 0.7.1
0.7.0   | N/A     | [API](http://scodec.org/api/scodec-protocols/0.7.0) | 2.10, 2.11 | 0.7.0
0.6.2   | N/A     | [API](http://scodec.org/api/scodec-protocols/0.6.2) | 2.10, 2.11 | 0.6.2
0.6.1   | N/A     | [API](http://scodec.org/api/scodec-protocols/0.6.1) | 2.10, 2.11 | 0.6.1
0.6.0   | N/A     | [API](http://scodec.org/api/scodec-protocols/0.6.0) | 2.10, 2.11 | 0.6.0

## scodec-spire

    libraryDependencies += "org.scodec" %% "scodec-spire" % "0.2.0"

The scodec-spire module provides interop between scodec-core and spire.

Version | Changes | ScalaDoc | Scala | scodec-core | spire
--------|---------|----------|-------|-------------|--------
0.2.0   | N/A     | [API](http://scodec.org/api/scodec-spire/0.2.0) | 2.10, 2.11 | [1.8,1.9) | [0.10, 0.11)
0.1.0   | N/A     | [API](http://scodec.org/api/scodec-spire/0.1.0) | 2.10, 2.11 | [1.7,1.8) | [0.9, 0.10)

