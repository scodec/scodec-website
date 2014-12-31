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
  ...
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

```scala
trait Encoder[-A] {
  def encode(a: A): Err \/ BitVector
}
```

An encoder defines a single abstract operation, `encode`, which converts a value to binary or returns an error. This design differs from other libraries by allowing `encode` to be defined partially over type `A`. For example, this allows an integer encoder to be defined on a subset of the integers without having to resort to newtypes or wrapper types.

### contramap

A function can be mapped over an encoder, similar to `map` on decoder, but unlike `map`, the supplied function has its arrow reversed -- that is, we convert an `Encoder[A]` to an `Encoder[B]` with a function `B => A`. This may seem strange at first, but all we are doing is using the supplied function to convert a `B` to an `A` and then delegating the encoding logic to the original `Encoder[A]`. This operation is called `contramap`:

```scala
trait Encoder[-A] { self =>
  def encode(a: A): Err \/ BitVector
  def contramap[B](f: B => A): Encoder[B] = new Encoder[B] {
    def encode(b: B): Err \/ BitVector =
      self.encode(f(b))
  }
}
```

### econtramap

Like decoder's `map`, `contramap` takes a total function. To use a partial function, there's `econtramap`:

```scala
trait Encoder[-A] { self =>
  ...
  def econtramap[B](f: B => Err \/ A): Encoder[B] = new Encoder[B] {
    def encode(b: B): Err \/ BitVector =
      f(b) flatMap self.encode
  }
}
```

### flatMap?

Unlike decoder, there is no `flatMap` operation on encoder. Further, there's no "corresponding" operation -- in the way that `contramap` corresponds to `map` and `econtramap` corresponds to `emap`. To get a feel for why this is, try defining a `flatMap`-like method. For instance, you could try "reversing the arrows" and substituting `Encoder` for `Decoder`, yielding a method like `def flatMapLike[B](f: Encoder[B] => A): Encoder[B]` -- but you'll find there's no reasonable way to implement `encode` on the returned encoder.

## Codec

We can now implement `Codec` as the combination of an encoder and decoder:

```scala
trait Codec[A] extends Encoder[A] with Decoder[A]
```

A codec has no further abstract operations -- leaving it with only `encode` and `decode`, along with a number of derived operations like `map` and `contramap`. However, at least as presented here, calling `map` on a codec results in a decoder and calling `contramap` on a codec results in an encoder -- effectively "forgetting" how to encode and decode respectively. We need a new set of combinators for working with codecs that does not result in forgetting information.

### xmap

### exmap

### Additional transforms

## GenCodec

## Variance

You may have noticed the variance annotations in `Encoder`, `Decoder`, and `GenCodec`, and the lack of a variance annotation in `Codec`. Specifically:

 - `Encoder` is defined contravariantly in its type parameter
 - `Decoder` is defined covariantly in its type parameter
 - `GenCodec` is defined contravariantly in its first type parameter and covariantly in its second type parameter
 - `Codec` is defined invariantly in its type parameter

TODO
