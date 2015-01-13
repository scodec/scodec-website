scodec-core
===========

### Getting Started

```scala
scala> import scodec.Codec

scala> import scodec.codecs.implicits._

scala> case class Point(x: Int, y: Int)

scala> case class Line(start: Point, end: Point)

scala> case class Arrangement(lines: Vector[Line])

scala> val arr = Arrangement(Vector(
  Line(Point(0, 0), Point(10, 10)),
  Line(Point(0, 10), Point(10, 0))))
arr: Arrangement = ...

scala> val arrBinary = Codec.encode(arr).require
arrBinary: scodec.bits.BitVector =
  BitVector(288 bits, 0x0000000200000000000000000000000a0000000a000000000000000a0000000a00000000)

scala> val decoded = Codec[Arrangement].decode(arrBinary).require.valid
decoded: Arrangement = Arrangement(Vector(Line(Point(0,0),Point(10,10)), Line(Point(0,10),Point(10,0))))
```

We start by importing the primary type in scodec-core, the `Codec` type, along with all implicit codecs defined in `scodec.codecs.implicits`. The latter provides commonly useful implicit codecs, but is opinionated -- that is, it decides that a `String` is represented as a 32-bit signed integer whose value is the string length in bytes, followed by the UTF-8 encoding of the stirng.

Aside: the predefined implicit codecs are useful at the REPL and when your application does not require a specific binary format. However, scodec-core is designed to support "contract-first" binary formats -- ones in which the format is fixed in stone. For binary serialization to arbitrary formats, consider tools like [scala-pickling](https://github.com/scala/pickling), [Avro](http://avro.apache.org), and [protobuf](https://code.google.com/p/protobuf/).

We then create three case classes followed by instantiating them all and assigning the result to the `arr` val. We encode `arr` to binary using `Codec.encode` followed by a call to 'require', then decode the resulting binary back to an `Arrangement`. In this example, both encoding and decoding rely on an implicitly available `Codec[Arrangement]`, which is automatically derived based on *compile time* reflection on the structure of the `Arrangement` class and its product types.

We use `encode(...).require`, which throws an `IllegalArgumentException` if encoding fails, because we know that our arrangement codec cannot fail to encode. To decode, we summon the implicit arrangement codec via `Codec[Arrangement]` and then use `decode(...).require.value` for REPL convenience -- which throws an `IllegalArgumentException` if decoding fails and throws away any bits left over after decoding finishes. In this case, we know that decoding will succeed and there will be no remaining bits, so this is safe. It is generally better to avoid use of `require`, as it is unsafe -- because it may throw.

Running the same code with a different implicit `Codec[Int]` in scope changes the output accordingly:

```scala

scala> import scodec.codecs.implicits.{ implicitIntCodec => _, _ }

scala> implicit val ci = scodec.codecs.uint8
ci: scodec.Codec[Int] = 8-bit unsigned integer

...

scala> val arrBinary = Codec.encode(arr).require
arrBinary: scodec.bits.BitVector = BitVector(72 bits, 0x0200000a0a000a0a00)

scala> val decoded = Codec.decode[Arrangement](arrBinary).require.value
decoded: Arrangement = Arrangement(Vector(Line(Point(0,0),Point(10,10)), Line(Point(0,10),Point(10,0))))
```

In this case, we import all predefined implicits except for the `Codec[Int]` and then we define an implicit `Int` codec for 8-bit unsigned big endian integers. The resulting encoded binary is 1/4 the size. However, our arrangement codec is no longer total in encoding -- that is, it may result in errors. Consider:

```scala
scala> val arr2 = Arrangement(Vector(
  Line(Point(0, 0), Point(10, 10)),
  Line(Point(0, 10), Point(10, -1))))
arr2: Arrangement = Arrangement(Vector(Line(Point(0,0),Point(10,10)), Line(Point(0,10),Point(10,-1))))

scala> val encoded = Codec.encode(arr2)
encoded: scodec.Attempt[scodec.bits.BitVector] =
  Failure(lines/1/end/y: -1 is less than minimum value 0 for 8-bit unsigned integer)

scala> val encoded = Codec.encode(arr2).require
java.lang.IllegalArgumentException: lines/1/end/y: -1 is less than minimum value 0 for 8-bit unsigned integer
```

Attempting to encode an arrangement that contains a point with a negative number resulted in an error being returned from `encode` and an exception being thrown from `require`. The error includes the path to the error -- `lines/1/end/y`. In this case, the `lines` field on `Arrangement`, the line at the first index of that vector, the `end` field on that line, and the `y` field on that point.

If you prefer to avoid using implicits, do not fret! The above example makes use of implicits and uses Shapeless for compile time reflection, but this is built as a layer on top of the core algebra of scodec-core. The library supports a usage model where implicits are not used.

With the first example under our belts, let's look at the core algebra in detail.
