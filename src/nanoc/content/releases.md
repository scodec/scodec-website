---
title: Home
---
# Releases

This page lists the releases for the scodec modules. The projects adhere to typical Scala-style semantic versioning / binary compatibility. That is, releases that share the same major.minor version support forward binary compatibility. Code that was compiled against version `x.y` will link successfully with `x.y'` when `y < y'`. Note that the inverse is not necessarily true -- when `y > y'`.

## scodec-bits

As of 1.0.5, scodec-bits is released under the org.scodec group id. Prior to 1.0.5, it was released under the org.typelevel group id.

Version | Changes | ScalaDoc | Scala
--------|---------|----------|-------
1.0.4   | [Changes](https://github.com/scodec/scodec-bits/blob/v1.0.4/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/bits/stable/1.0.4) | 2.10, 2.11 |
1.0.3   | [Changes](https://github.com/scodec/scodec-bits/blob/v1.0.3/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/bits/stable/1.0.3) | 2.10, 2.11 |
1.0.2   | [Changes](https://github.com/scodec/scodec-bits/blob/v1.0.2/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/bits/stable/1.0.2) | 2.10, 2.11 |
1.0.1   | [Changes](https://github.com/scodec/scodec-bits/blob/v1.0.1/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/bits/stable/1.0.1) | 2.10, 2.11 |
1.0.0   | [Changes](https://github.com/scodec/scodec-bits/blob/v1.0.0/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/bits/stable/1.0.0) | 2.10, 2.11 |

## scodec-core

As of 1.7.0, scodec-bits is released under the org.scodec group id. Prior to 1.7.0, it was released under the org.typelevel group id. Also as of 1.7.0, scalaz-core is no longer a dependency -- instead, interop with scalaz-core is now provided by the scodec-scalaz module.

Version | Changes | ScalaDoc | Scala | scodec-bits | scalaz | Shapeless
--------|---------|----------|-------|-------------|--------|-----------
1.6.0   | [Changes](https://github.com/scodec/scodec/blob/v1.6.0/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.6.0) | 2.10, 2.11 | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.5.0   | [Changes](https://github.com/scodec/scodec/blob/v1.5.0/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.5.0) | 2.10, 2.11 | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.4.0   | [Changes](https://github.com/scodec/scodec/blob/v1.4.0/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.4.0) | 2.10, 2.11 | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.3.2   | [Changes](https://github.com/scodec/scodec/blob/v1.3.2/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.3.2) | 2.10, 2.11 | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.3.1   | [Changes](https://github.com/scodec/scodec/blob/v1.3.1/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.3.1) | 2.10, 2.11 | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.3.0   | [Changes](https://github.com/scodec/scodec/blob/v1.3.0/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.3.0) | 2.10, 2.11 | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.2.2   | [Changes](https://github.com/scodec/scodec/blob/v1.2.2/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.2.2) | 2.10, 2.11 | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.2.1   | [Changes](https://github.com/scodec/scodec/blob/v1.2.1/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.2.1) | 2.10, 2.11 | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.2.0   | [Changes](https://github.com/scodec/scodec/blob/v1.2.0/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.2.0) | 2.10, 2.11 | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.1.0   | [Changes](https://github.com/scodec/scodec/blob/v1.1.0/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.1.0) | 2.10, 2.11 | [1.0,2.0) | [7.1,7.2) | 2.0.0
1.0.0   | [Changes](https://github.com/scodec/scodec/blob/v1.0.0/CHANGELOG.md) | [API](http://docs.typelevel.org/api/scodec/core/stable/1.0.0) | 2.10, 2.11 | [1.0,2.0) | [7.0,7.1) | 1.2.4
