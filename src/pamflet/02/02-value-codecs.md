Simple Value Codecs
===================

There are a number of pre-defined codecs for simple value types provided by the `scodec.codecs` object. In this section, we'll look at some of these.

## BitVector and ByteVector

One of the simplest codecs is an identity for `BitVector`s. That is, a `Codec[BitVector]` that returns the supplied bit vector from `encode` and `decode`. This is provided by the `scodec.codecs.bits` method. This codec has some interesting properties -- it is both _total_ and _greedy_. By total, we mean that it never returns an error from `encode` or `decode`. By greedy, we mean that the `decode` method always consumes the entire input bit vector and returns an empty bit vector as the remaining bits.

The greedy property may seem strange, or at least more specialized than codec for a fixed number of bits -- for instance, a constant width binary field. However, non-greedy codecs can often be built out of greedy codecs. We'll see a general combinator for doing so later, in the Framing section.

Nonetheless, constant width binary fields occur often enough to warrant their own built-in constructor. The `scodec.codecs.bits(size: Long)` method returns a `Codec[BitVector]` that decodes exactly `size` bits from the supplied vector, failing to decode with an `Err.InsufficientBits` error if there are less than `size` bits provided. If a bit vector less than `size` bits is supplied to `encode`, it is right-padded with 0s.

Similarly, the `scodec.codecs.bytes` and `scodec.codecs.bytes(size: Int)` methods return a greedy `Codec[ByteVector]` and a fixed-width `Codec[ByteVector]`, where the latter's size is specified in bytes instead of bits.

## Booleans

The `bool` codec is a `Codec[Boolean]` which encodes a 1-bit vector where `0` represents `false` and `1` represents true.

There's an overload of `bool` which takes a bit count -- `bool(n)` -- which also is a `Codec[Boolean]`. When decoding, it treats `n` consecutive
`0`s as `false` and all other vectors as `true`. When encoding, `true` is encoded as `n` consecutive `1`s.

## Numerics

Codecs are also provided for various numeric types -- `Int`, `Long`, `Short`, `Float`, and `Double`. Let's consider first the integral types, `Int`, `Long`, and `Short`, followed by the non-integral types.

### Integral Types

There are a number of predefined integral codecs defined by methods named according to the form:

```
 [u]int\${size}[L]
```

where `u` stands for unsigned, `size` is replaced by one of `8, 16, 24, 32, 64`, and `L` stands for little-endian.
For each codec of that form, the type is `Codec[Int]` or `Codec[Long]` depending on the specified size. Signed
integer codecs use the 2's complement encoding.

For example, `int32` supports 32-bit big-endian 2s complement signed integers, and `uint16L` supports 16-bit little-endian unsigned integers.
Note: `uint64` and `uint64L` are not provided because a 64-bit unsigned integer does not fit in to a `Long`.

Additionally, methods of the form `[u]int[L](size: Int)` and `[u]long[L](size: Int)` exist to build arbitrarily
sized codecs, within the limitations of `Int` and `Long`. Hence, a 13-bit unsigned integer codec is given by `uint(13)`.

Similarly, `Short` codecs are provided by `short16`, `short16L`, `short(size)`, `shortL(size)`, `ushort(size)`, and `ushortL(size)`. The signed methods take a size up to `16` and the unsigned methods take a size up to `15`.

### Non-Integral Types

`Float` and `Double` codecs are provided by `float` and `double`. Both use IEEE754, with the former represented as 32-bits and the latter represented as 64-bits.

## Strings

`String`s are supported by a variety of codecs.

The rawest form is the `string` method, which takes an implicit `java.nio.charset.Charset`. The resulting codec encodes strings using the supplied charset -- that is, all of the heavy lifting of converting each character to binary is handled directly by the charset. Hence, the `string` codec is nothing more than glue between the `Codec` type and `Charset`.

There are two convenience codecs defined as well, `utf8` and `ascii`, which are simply aliases for the `string(charset)`, passing the `UTF-8` and `US-ASCII` charsets.

Codecs returned from `string`, including `utf8` and `ascii`, are greedy. The byte size / character count is
*not* encoded in the binary, and hence, it is not safe to decode a vector that has been concatenated with another vector. For example:

```
scala> val pair = utf8 ~ uint8
pair: scodec.Codec[(String, Int)] = (UTF-8, 8-bit unsigned integer)

scala> val enc = pair.encode(("Hello", 48))
enc: scodec.Attempt[scodec.bits.BitVector] =
  Successful(BitVector(48 bits, 0x48656c6c6f30))

scala> pair.decode(enc.require)
res1: scodec.Attempt[scodec.DecodeResult[(String, Int)]] =
  Failure(cannot acquire 8 bits from a vector that contains
0 bits)
```

Here, we create a `Codec[(String, Int)]` using the `utf8` codec and a `uint8` codec. We then encoded the pair `("Hello", 48)` and then tried to decode the result. However, we got a failure indicating there were not enough bits. Let's try decoding the resulting vector using the `utf8` codec directly:

```
scala> utf8.decode(enc.require)
res2: scodec.Attempt[scodec.DecodeResult[String]] =
  Successful(DecodeResult(Hello0,BitVector(empty)))
```

The result is `"Hello0"`, not `"Hello"` as expected. The `utf8` codec decoded the entire vector, including the `0x30` byte that originally was written by the `uint8` codec.

This greediness property is a feature of `string` -- often, the size of a string field in a binary protocol is provided by some external mechanism. For example, by a record size field that is defined in another part of the message.

There are alternatives to `string`, `utf8`, and `ascii` that encode the string's byte size in the binary -- `string32`, `utf8_32`, and `ascii32`. Executing the same example as above with `utf8_32` instead of `utf8` yields the expected result:

```
scala> val pair = utf8_32 ~ uint8
pair: scodec.Codec[(String, Int)] = (string32(UTF-8), 8-bit unsigned integer)

scala> val enc = pair.encode(("Hello", 48))
enc: scodec.Attempt[scodec.bits.BitVector] =
  Successful(BitVector(80 bits, 0x0000000548656c6c6f30))

scala> val dec = pair.decode(enc.require)
dec: scodec.Attempt[scodec.DecodeResult[(String, Int)]] =
  Successful(DecodeResult((Hello,48),BitVector(empty)))
```

By looking at the encoded binary, we can see that the byte size of the string was encoded in a 32-bit field the preceeded the encoded string.

In order to handle size delimited string fields, like the above, except with artibrary size fields, we can use the `variableSizeBytes` combinator along with `string`, `utf8`, or `ascii`. The `variableSizeBytes` combinator is covered in more detail in a later section. For now though, consider the following example, which encodes the byte size of the string in an unsigned little-endian 18-bit integer field.

```
scala> val str18 = variableSizeBytes(uintL(18), utf8)
str18: scodec.Codec[String] = variableSizeBytes(18-bit unsigned integer, UTF-8)

scala> str18.encode("Hello")
res0: scodec.Attempt[scodec.bits.BitVector] =
  Successful(BitVector(58 bits, 0x050012195b1b1bc))
```

This has the same benefits as `utf8_32` except it allows for an arbitrary size field, rather than being limited to a 32-bit size field.
