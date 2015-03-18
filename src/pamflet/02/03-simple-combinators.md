Simple Construtors and Combinators
==================================

## Constants

Constant codecs are codecs that always encode a specific bit pattern. For example, the `constant(bin"1110")` always encodes the bit pattern `1110`. When decoding, a constant codec consumes the same number of bits as its constant value, and then either validates that the consumed bits match the constant value or ignores the consumed bits. Codecs created by `constant(...)` perform validation when decoding and codecs created by `constantLenient(...)` ignore the consumed bits.

For example, decoding `0000` with each technique yields:

```scala
scala> val x = constant(bin"1110").decode(bin"0000")
x: scodec.Attempt[scodec.DecodeResult[Unit]] =
  Failure(expected constant BitVector(4 bits, 0xe) but got BitVector(4 bits, 0x0))

scala> val y = constantLenient(bin"1110").decode(bin"0000")
y: scodec.Attempt[scodec.DecodeResult[Unit]] =
  Successful(DecodeResult((),BitVector(empty)))
```

The `constant(...)` method returns a `Codec[Unit]`, which may seem odd at first glance. Unit codecs occur frequently in scodec. They are used for a variety of use cases, including encoding a predefined value, decoding a specific pattern, manipulating the remainder during decoding, or raising errors from encoding/decoding.

### Literal Constants

When working with binary formats that make heavy use of constant values, manually wrapping each constant bit pattern with a `constant` method can be verbose. This verbosity can be avoided by importing implicit conversions that allow treating binary literals as codecs.

```scala
scala> import scodec.codecs.literals._
import scodec.codecs.literals._

scala> val c: Codec[Unit] = bin"1110"
c: scodec.Codec[Unit] = constant(BitVector(4 bits, 0xe))
```

## Unit Codecs

Any codec can be turned in to a unit codec -- that is, a `Codec[Unit]` -- using the `unit` method on the `Codec` type. The resulting codec encodes and decodes using the original codec, but the decoded value is thrown away, and the value to encode is "fixed" at tht time the unit codec is generated.

For example:

```scala
scala> val c = int8.unit(-1)
c: scodec.Codec[Unit] = ...

scala> val enc = c.encode(())
enc: scodec.Attempt[scodec.bits.BitVector] =
  Successful(BitVector(8 bits, 0xff))

scala> val dec = c.decode(bin"00000000 00000001")
dec: scodec.Attempt[scodec.DecodeResult[Unit]] =
  Successful(DecodeResult((),BitVector(8 bits, 0x01)))
```

In this example, the value to encode is fixed to `-1` at the time `c` is created. Hence, every call to encode results in a call to `int8.encode(-1)`. Decoding is interesting in that it consumed 8 bits of the vector.

In general, converting a codec to a unit codec may seem like a useless operation. However, it plays an important role in both tuple codecs and heterogeneous list codecs, which are both covered at length later.

## Context

TODO - withContext, | syntax

## Miscellaneous

TODO - complete, compact, withToString
