scodec-bits
===========

The scodec-bits library contains data structures for working with binary. It has no dependencies, which allows it to be used by other libraries without causing dependency conflicts.

There are two primary data structures in the library, `ByteVector` and `BitVector`. Both are immutable collections and have performance characteristics that are optimized for use in the other scodec modules. However, each type has been designed for general purpose usage, even when other scodec modules are not used. For instance, `ByteVector` can be safely used as a replacement for immutable byte arrays.

ByteVector
----------

The `ByteVector` type is isomorphic to a `scala.collection.immutable.Vector[Byte]` but has much better performance characteristics. A `ByteVector` is represented as a balanced binary tree of chunks. Most operations have asymptotic performance that is logarithmic in the depth of this tree. There are also quite a number of convenience based features, like value based equality, a sensible `toString`, and many conversions to/from other data types.

It is important to note that `ByteVector` does not extend any types from the Scala collections framework. For instance, `ByteVector` is *not* a `scala.collection.immutable.Traversable[Byte]`. This allows some deviance, like `Long` based indexing instead of `Int` based indexing from standard collections. Additionally, it avoids a large category of bugs, especially as the standard library collections are refactored. Nonetheless, the methods on `ByteVector` are named to correspond with the methods in the standard library when possible.

### Getting Started

Let's create a `ByteVector` from a literal hexadecimal string:

```scala
scala> import scodec.bits._
import scodec.bits._

scala> val x: ByteVector = hex"deadbeef"
x: scodec.bits.ByteVector = ByteVector(4 bytes, 0xdeadbeef)

scala> val y: ByteVector = hex"DEADBEEF"
y: scodec.bits.ByteVector = ByteVector(4 bytes, 0xdeadbeef)

scala> x == y
res0: Boolean = true
```

We first start by importing all members of the `scodec.bits` package, which contains the entirety of this library. We then create two byte vectors from hexadecimal literals, using the `hex` string interpolator. Finally, we compare them for equality, which returns true, because each vector contains the same bytes.

### Constructing Byte Vectors

There are a variety of ways to construct byte vectors. The `hex` string interpolator is useful for testing and REPL experiments but often, it is necessary to construct byte vectors from other data types. Most commonly, a byte vector must be created from a standard library collection, like a `Vector[Byte]` or `Array[Byte]`, or a Java NIO `ByteBuffer`. This is accomplished with the `apply` method on the `ByteVector` companion.

When constructing a `ByteVector` from an array, the array contents are *copied*. This is the safest behavior, as any mutations to the original byte array do not cause problems with the immuable `ByteVector`. However, the cost of copying can be prohibitive in some situations. To address this, a byte array can be converted to a `ByteVector` with a constant time operation -- `ByteVector.view(array)`. Using `view` requires the byte array to never be modified after the vector is constructed.

`ByteVector`s can also be created from strings in various bases, like hexadecimal, binary, or base 64. For example, to convert a hexadecimal string to a `ByteVector`, use `ByteVector.fromHex(string)`. There are quite a number of methods related to base conversions -- explore the ScalaDoc of the `ByteVector` companion object for details.

BitVector
---------

The `BitVector` type is similar to `ByteVector` with the exception of indexing bits instead of bytes. This allows access and update of specific bits (via `apply` and `update`) as well as storage of a bit count that is not evenly divisible by 8.

### Getting Started

```scala
scala> val x: BitVector = bin"00110110101"
x: scodec.bits.BitVector = BitVector(11 bits, 0x36a)

scala> val y: BitVector = bin"00110110100"
y: scodec.bits.BitVector = BitVector(11 bits, 0x368)

scala> x == y
res0: Boolean = false

scala> val z = y.update(10, true)
z: scodec.bits.BitVector = BitVector(11 bits, 0x36a)

scala> x == z
res1: Boolean = true
```

In this example, we create two 10-bit vectors using the `bin` string interpolator that differ in only the last bit. We then create a third vector, `z`, by updating the 10th bit of `y` to true. Comparing `x` and `y` for equality returns false whereas comparing `x` and `z` returns true.

### Constructing Bit Vectors

`BitVector`s are constructed in much the same way as `ByteVector`s. That is, typically via the `apply` and `view` methods in the `BitVector` companion object. Additionally, any `ByteVector` can be converted to a `BitVector` via the `bits` method (e.g., `myByteVector.bits`) and any `BitVector` can be converted to a `ByteVector` via the `bytes` method.

TODO unfold, nio

Transforms
----------

Both `ByteVector`s and `BitVector`s support a number of different transformations.

### Collection Like Transforms

TODO

### Bitwise Transforms

TODO

Base Conversions
----------------

TODO

Cyclic Redundancy Checks
------------------------

TODO
