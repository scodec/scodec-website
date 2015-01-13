Core Algebra
============

We saw the `Codec` type when we used it to encode a value to binary and decode binary back to a value. The ability to decode and encode come from two fundamental traits, `Decoder` and `Encoder`. Let's look at these in turn.

## Decoder

```scala
case class DecodeResult[A](value: A, remainder: BitVector) { ... }

trait Decoder[+A] {
  def decode(b: BitVector): Attempt[DecodeResult[A]]
}
```

A decoder defines a single abstract operation, `decode`, which attempts to convert a bit vector into a `DecodeResult`. A `DecodeResult` is a case class made up of a value of type `A` and a remainder -- bits that are left over, or unconsumed, after decoding has completed. For example, a decoder that decodes a 32-bit integer returns an error when the supplied vector has less than 32-bits, and returns the supplied vector less 32-bits otherwise.

The result type is an `Attempt[A]`, which is equivalent to an `Either[scodec.Err, A]`. `Err` is an open-for-subclassing data type that contains an error message and a context stack. The context stack contains strings that provide context on where the error occurred in a large structure. We saw an example of this earlier, where the context stack represented a path through the `Arrangement` class, into a `Vector`, and then into a `Line` and `Point`. The type is open-for-subclassing so that codecs can return domain specific error types and then pattern match on the received type. An `Err` is *not* a subtype of `Throwable`, so it cannot be used (directly) with `scala.util.Try`. Also note that codecs never throw exceptions (or should never!). All errors are communicated via the `Err` type.

### map

A function can be mapped over a decoder, resulting in our first combinator:

```scala
trait Decoder[+A] { self =>
  def decode(b: BitVector): Attempt[DecodeResult[A]]
  def map[B](f: A => B): Decoder[B] = new Decoder[B] {
    def decode(b: BitVector): Attempt[DecodeResult[B]] =
      self.decode(b) map { result => result map f }
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
  def emap[B](f: A => Attempt[B]): Decoder[B] = new Decoder[B] {
    def decode(b: BitVector): Attempt[DecodeResult[B]] =
      self.decode(bits) flatMap { result =>
        f(result.value).map { b => DecodeResult(b, result.remainder) }
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
    def decode(b: BitVector): Attempt[DecodeResult[B]] =
      self.decode(b) flatMap { result =>
        val next: Codec[B] = f(result.value)
        next.decode(result.remainder)
      }
  }
}
```

The resulting decoder first decodes a value of type `A` using the original decoder. If that's successful, it applies the decoded value to the supplied function to get a `Decoder[B]` and then decodes the bits remaining from decoding `A` using that decoder.

As mentioned previously, `flatMap` models a dependency between a decoded value and the decoder to use for the remaining bits. A good use case for this is a bit pattern that first encodes a count followed by a number of records. An implementation of this is not provided because we will see it later in a different context.

## Encoder

```scala
trait Encoder[-A] {
  def encode(a: A): Attempt[BitVector]
}
```

An encoder defines a single abstract operation, `encode`, which converts a value to binary or returns an error. This design differs from other libraries by allowing `encode` to be defined partially over type `A`. For example, this allows an integer encoder to be defined on a subset of the integers without having to resort to newtypes or wrapper types.

### contramap

A function can be mapped over an encoder, similar to `map` on decoder, but unlike `map`, the supplied function has its arrow reversed -- that is, we convert an `Encoder[A]` to an `Encoder[B]` with a function `B => A`. This may seem strange at first, but all we are doing is using the supplied function to convert a `B` to an `A` and then delegating the encoding logic to the original `Encoder[A]`. This operation is called `contramap`:

```scala
trait Encoder[-A] { self =>
  def encode(a: A): Attempt[BitVector]
  def contramap[B](f: B => A): Encoder[B] = new Encoder[B] {
    def encode(b: B): Attempt[BitVector] =
      self.encode(f(b))
  }
}
```

### econtramap

Like decoder's `map`, `contramap` takes a total function. To use a partial function, there's `econtramap`:

```scala
trait Encoder[-A] { self =>
  ...
  def econtramap[B](f: B => Attempt[A]): Encoder[B] = new Encoder[B] {
    def encode(b: B): Attempt[BitVector] =
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

The codec equivalent to `map` and `contramap` is called `xmap`:

```scala
trait Codec[A] extends Encoder[A] with Decoder[A] { self =>
  def xmap[B](f: A => B, g: B => A): Codec[B] = new Codec[B] {
    def encode(b: B) = self.contramap(g).encode(b)
    def decode(b: BitVector) = self.map(f).decode(b)
  }
}
```

Here, we've defined `xmap` in terms of `map` and `contramap`. The `xmap` operation is one of the most commonly used operations in scodec-core. Consider this example:

```scala
case class Point(x: Int, y: Int)

val tupleCodec: Codec[(Int, Int)] = ...
val pointCodec: Codec[Point] = tupleCodec.xmap[Point](t => Point(t._1, t._2), pt => (pt.x, pt.y))
```

We convert a `Codec[(Int, Int)]` into a `Codec[Point]` using the `xmap` operation, passing two functions -- one that converts from a `Tuple2[Int, Int]` to a `Point`, and another that converts a `Point` to a `Tuple2[Int, Int]`. Note: there are a few simpler ways to define codecs for case classes that we'll see later.

### exmap

In a similar fashion to `emap` and `econtramap`, the `exmap` operation is like `xmap` but allows both functions to be defined partially:

```scala
trait Codec[A] extends Encoder[A] with Decoder[A] { self =>
  ...
  def exmap[B](f: A => Attempt[B], g: B => Attempt[A]): Codec[B] = new Codec[B] {
    def encode(b: B) = self.econtramap(g).encode(b)
    def decode(b: BitVector) = self.emap(f).decode(b)
  }
}
```

### Additional transforms

Unlike `map`, `emap`, `contramap`, and `econtramap`, `xmap` and  `exmap` each take two functions. `xmap` takes two total functions and `exmap` takes two partial functions. There are two other operations that take two conversion functions -- where one of the functions is total and the other is partial.

```scala
trait Codec[A] extends Encoder[A] with Decoder[A] { self =>
  ...
  def narrow[B](f: A => Attempt[B], g: B => A): Codec[B] = exmap(f, right compose g)
  def widen[B](f: A => B, g: B => Attempt[A]): Codec[B] = exmap(right compose f, g)
}
```

Finally, there's a variant of `widen` where the partial function is represented as a `B => Option[A]` instead of a `B => Attempt[A]`.

```scala
trait Codec[A] extends Encoder[A] with Decoder[A] { self =>
  ...
  def widenOpt[B](f: A => B, g: B => Option[A]): Codec[B] = ...
}
```

The `widenOpt` operation is provided to make manual authored case class codecs simpler -- by passing the `apply` and `unapply` methods from a case class companion. For instance, the earlier example becomes:

```scala
case class Point(x: Int, y: Int)

val tupleCodec: Codec[(Int, Int)] = ...
val pointCodec: Codec[Point] = tupleCodec.widenOpt(Point.apply, Point.unapply)
```

## GenCodec

The `xmap` and related operations allow us to transform a `Codec[A]` into a `Codec[B]`. Nonetheless, we can improve the definitions of the decoder and encoder specific methods (`map`, `contramap`, etc.). With the types as presented, we said that calling `map` on a codec forgot the encoding logic and returned a decoder, and that calling `contramap` on a codec forgot the decoding logic and returned an encoder.

We can remedy this somewhat by introducing a new type that is similar to `Codec` in that it is both an `Encoder` and a `Decoder` -- but dissimilar in that it allows the encoding type to differ from the decoding type.

```scala
trait GenCodec[-A, +B] extends Encoder[A] with Decoder[B] { self =>
  override def map[C](f: B => C): GenCodec[A, C] = GenCodec(this, super.map(f))
  override def contramap[C](f: C => A): GenCodec[C, B] = GenCodec(super.contramap(f), this)
  ...
  def fuse[AA <: A, BB >: B](implicit ev: BB =:= AA): Codec[BB] = new Codec[BB] {
    def encode(c: BB) = self.encode(ev(c))
    def decode(bits: BitVector) = self.decode(bits)
  }
}

object GenCodec {
  def apply[A, B](encoder: Encoder[A], decoder: Decoder[B]): GenCodec[A, B] = new GenCodec[A, B] {
    override def encode(a: A) = encoder.encode(a)
    override def decode(bits: BitVector) = decoder.decode(bits)
  }
}

trait Codec[A] extends GenCodec[A, A] { ... }
```

A `GenCodec` represents the pairing of an `Encoder` and a `Decoder`, with potentially different types. Each of the combinators from `Encoder` and `Decoder` are overridden such that they return `GenCodec`s that "remember" the behavior of the non-transformed type. For instance, the `map` operation on a `GenCodec` changes the decoding behavior while remembering the encoding behavior.

Hence, `GenCodec` has two type parameters -- the first is the encoding type and the second is the decoding type. Any time that the two types are equal, the `fuse` method can be used to convert the `GenCodec[A, A]` to a `Codec[A]`.

`GenCodec` is useful because it allows *incremental* transforms to be applied to a codec. Further, it plays an important role in the categorical view of codecs, which is discussed later. Still, direct usage of `GenCodec` is rare.

## Variance

You may have noticed the variance annotations in `Encoder`, `Decoder`, and `GenCodec`, and the lack of a variance annotation in `Codec`. Specifically:

 - `Encoder` is defined contravariantly in its type parameter
 - `Decoder` is defined covariantly in its type parameter
 - `GenCodec` is defined contravariantly in its first type parameter and covariantly in its second type parameter
 - `Codec` is defined invariantly in its type parameter

The variance annotations -- specifically the contravariant ones -- can cause problems with implicit search. At the current time, the implicit search problems cannot be fixed without making `Encoder` invariant. The authors of scodec believe the utility provided by subtyping variance outweighs the inconvenience of the implicit search issues they cause. If you disagree, please weigh-in on the [mailing list](https://groups.google.com/forum/#!forum/typelevel) or the [related pull request](https://github.com/scodec/scodec/pull/26).

## For the categorically minded

The core types have a number of type class instances. Note that this section assumes a strong familiarity with the major typeclasses of functional programming and can be safely skipped.

`Decoder` has a monad instance, where `flatMap` is defined as above and the point operation is defined as:

```scala
def point(a: A): Decoder[A] = new Decoder[A] {
  def decode(b: BitVector): Attempt[DecodeResult[A]] =
    Attempt.successful(DecodeResult(a, b))
}
```

`Encoder` has a contravariant functor instance, defined using the `contramap` operation from above. It also has a corepresentable instance with `Attempt[BitVector]`.

`GenCodec` has a profunctor instance, where `mapfst` is implemented using `contramap` and `mapsnd` is implemented using `map`.

`Codec` has an invariant (aka exponential) functor instance, using the `xmap` operation from above.

Instances for the Scalaz versions of each these type classes are located in the scodec-scalaz module, which is discussed later.

scodec-core defines one additional type class, `Transform`, which abstracts over the type constructor in the transform operations. It defines a single abstract operation -- `exmap` -- and defines concrete versions of `xmap`, `narrow`, `widen`, etc. in terms of `exmap`. This type class is unlikely to be useful outside of scodec libraries due to the use of `scodec.Err`. It exists in order to share transform API between `Codec` and another scodec-core type we'll see later.

## Manually creating codecs

Codecs are typically created by transforming or combining other codecs. However, we can create a codec manually by writing a class that extends `Codec` and implements `encode` and `decode`.

```scala
class BitVectorCodec(size: Long) extends Codec[BitVector] {
  def encode(b: BitVector) = {
    if (b.size == size) Attempt.successful(b)
    else Attempt.failure(Err(s"expected size \${size} but got \${b.size}"))
  }
  def decode(b: BitVector) = {
    val (result, remaining) = b.splitAt(size)
    if (result.size != size)
      Attempt.failure(new Err.InsufficientBits(size, result.size))
    else Attempt.successful(DecodeResult(result, remaining))
  }
}
```

Besides the fundamental types -- `Codec`, `Decoder`, and `Encoder` -- scodec-core is focused on providing *combinators*. That is, providing ways to combine two or more codecs in to a new codec, or transform a single codec in to another. We've seen a few examples of combinators (e.g., `xmap`) and we'll see many more.

The combinators exist to make codecs easier to read and write. They promote correctness by allowing a codec to be built from components that are known to be correct. They increase readability by encapsulating boilerplate and wiring logic, leaving the structure of the codec evident.  However, it is easy to get distracted by searching for an elegant combinator based codec implementation when a manually authored codec is appropriate. As you work with scodec, you'll develop an intuition for when to write codecs manually.

## Summary

The `Codec` type is the work horse of scodec-core, which combines a `Decoder` with an `Encoder`. Codecs can be transformed in a variety of ways -- and we'll see many more ways in later sections. However, we can always fall back to implementing `Codec` or even `Decoder` or `Encoder` directly if a combinator based approach proves inconvenient.
