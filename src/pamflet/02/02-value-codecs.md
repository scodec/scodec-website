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

TODO

## Strings

TODO

