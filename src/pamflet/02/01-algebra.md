Core Algebra
============

We saw the `Codec` type when we used it to encode a value to binary and decode binary back to a value. The ability to decode and encode come from two fundamental traits, `Decoder` and `Encoder`. Let's look at these in turn.

## Decoder

```scala
trait Decoder[+A] {
  def decode(b: BitVector): Err \/ (BitVector, A)
}
```

A decoder defines a single abstract operation, `decode`, which converts a bit vector in to a pair containing the unconsumed bits and a decoded value, or returns an error. For example, a decoder that decodes a 32-bit integer returns an error when the supplied vector has less than 32-bits, and returns the supplied vector less 32-bits otherwise.

The result type is a disjunction with `scodec.Err` on the left side. `Err` is an open-for-subclassing data type that contains an error message and a context stack. The context stack contains strings that provide context on where the error occurred in a large structure. We saw an example of this earlier, where the context stack represented a path through the `Arrangement` class, in to a `Vector`, and then in to a `Line` and `Point`. The type is open-for-subclassing so that codecs can return domain specific error types and then pattern match on the received type. An `Err` is *not* a subtype of `Throwable`, so it cannot be used (directly) with `scala.util.Try`. Also note that codecs never throw exceptions (or should never!). All errors are communicated via the `Err` type.

### map

A function can be mapped over a decoder, resulting in our first combinator:

```scala
trait Decoder[+A] { self =>
  def decode(b: BitVector): Err \/ (BitVector, A)
  def map[B](f: A => B): Decoder[B] = new Decoder[B] {
    def decode(b: BitVector): Err \/ (BitVector, B) =
      self.decode(b) map { case (rem, a) => (rem, f(b)) }
  }
}
```

Note that the *implementation* of the `map` method is not particularly important -- rather, the type signature is the focus.

As a first use case for `map`, consider creating a decoder for the following case class by reusing the built-in `int32` codec:

```scala
case class Foo(x: Int)
val fooDecoder: Decoder[Foo] = int32 map { i => Foo(i) }
```

### emap

The `map` operation does not allow room for returning an error. We can define a variant of `map` that allows the supplied function to indicate error:

```scala
trait Decoder[+A] { self =>
  ...
  def emap[B](f: A => Err \/ B): Decoder[B] = new Decoder[B] {
    def decode(b: BitVector): Err \/ (BitVector, B) =
      self.decode(bits) flatMap { case (rem, a) =>
        f(a).map { b => (rem, b) }
      }
  }
}
```

### flatMap

Further generalizing, we can `flatMap` a function over a decoder to express that the "next" codec is *dependent* on the decoded value from the current decoder:

```scala
trait Decoder[+A] { self =>
  def decode(b: BitVector): Err \/ (BitVector, A)
  def flatMap[B](f: A => Decoder[B]): Decoder[B] = new Decoder[B] {
    def decode(b: BitVector): Err \/ (BitVector, B) =
      self.decode(b) flatMap { case (rem, a) =>
        val next: Codec[B] = f(a)
        next.decode(rem)
      }
  }
}
```

The resulting decoder first decodes a value of type `A` using the original decoder. If that's successful, it applies the decoded value to the supplied function to get a `Decoder[B]` and then decodes the bits remaining from decoding `A` using that decoder.

As mentioned previously, `flatMap` models a dependency between a decoded value and the decoder to use for the remaining bits. A good use case for this is a bit pattern that first encodes a count followed by a number of records. An implementation of this is not provided because we will see it later in a different context.

## Encoder

TODO

## Codec

TODO

## GenCodec

TODO
