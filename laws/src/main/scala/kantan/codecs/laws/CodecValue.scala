package kantan.codecs.laws

import discipline.GenCodecValue

import kantan.codecs.laws.discipline.arbitrary._
import org.scalacheck.Gen._
import org.scalacheck.{Arbitrary, Gen}

import scala.collection.generic.CanBuildFrom

// TODO: investigate what type variance annotations can be usefully applied to CodecValue.
sealed abstract class CodecValue[E, D] extends Product with Serializable {
  def encoded: E
  def mapEncoded[EE](f: E ⇒ EE): CodecValue[EE, D]
  def mapDecoded[DD](f: D ⇒ DD): CodecValue[E, DD]
}

object CodecValue {
  final case class LegalValue[E, D](encoded: E, decoded: D) extends CodecValue[E, D] {
    override def mapDecoded[DD](f: D => DD) = copy(decoded = f(decoded))
    override def mapEncoded[EE](f: E ⇒ EE) = copy(encoded = f(encoded))
  }
  final case class IllegalValue[E, D](encoded: E) extends CodecValue[E, D] {
    override def mapDecoded[DD](f: D => DD) = IllegalValue(encoded)
    override def mapEncoded[EE](f: E => EE) = copy(encoded = f(encoded))
  }



  // - Helpers / bug workarounds ---------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbValue[E, D](implicit arbL: Arbitrary[LegalValue[E, D]], arbI: Arbitrary[IllegalValue[E, D]]): Arbitrary[CodecValue[E, D]] =
    Arbitrary(Gen.oneOf(arbL.arbitrary, arbI.arbitrary))


  // - Derived instances -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbLegalSeq[E, D, C1[_], C2[_]](implicit arb: Arbitrary[LegalValue[E, D]],
                                               cbfE: CanBuildFrom[Nothing, E, C1[E]],
                                               cbfD: CanBuildFrom[Nothing, D, C2[D]]
                                              ): Arbitrary[LegalValue[C1[E], C2[D]]] = Arbitrary {
    Gen.sized { size ⇒
      val be = cbfE()
      val bd = cbfD()

      for(values ← Gen.listOfN(size, arb.arbitrary)) yield {
        values.foreach { case LegalValue(e, d) ⇒
          be += e
          bd += d
        }
        LegalValue(be.result(), bd.result())
      }
    }
  }

  implicit def arbIllegalSeq[E, D, C1[_], C2[_]](implicit arb: Arbitrary[CodecValue.IllegalValue[E, D]],
                                                 cbfE: CanBuildFrom[Nothing, E, C1[E]]
                                                ): Arbitrary[CodecValue.IllegalValue[C1[E], C2[D]]] = Arbitrary {
    Gen.sized { size ⇒ choose(1, size).flatMap { size =>
      val be = cbfE()
      for(values ← Gen.listOfN(size, arb.arbitrary)) yield {
        values.foreach { case CodecValue.IllegalValue(e) ⇒ be += e }
        CodecValue.IllegalValue(be.result())
      }
    }}
  }

  implicit val arbLegalStrStr: Arbitrary[LegalString[String]] = Arbitrary(Arbitrary.arbitrary[String].map(s ⇒ LegalValue(s, s)))

  implicit def arbIllegalFromGen[E, D](implicit gen: GenCodecValue[E, D]): Arbitrary[IllegalValue[E, D]] =
    gen.arbIllegal

  implicit def arbLegalFromGen[E, D](implicit gen: GenCodecValue[E, D]): Arbitrary[LegalValue[E, D]] =
    gen.arbLegal
}
