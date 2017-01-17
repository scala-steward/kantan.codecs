/*
 * Copyright 2017 Nicolas Rinaudo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kantan.codecs.strings

import java.text.DateFormat
import java.util.Date
import kantan.codecs.{Decoder, Result}

/** Provides instance creation and summing methods for [[StringDecoder]].
  *
  * Default [[StringDecoder]] instances are provided in [[codecs]].
  */
object StringDecoder {
  /** Summons an implicit instance of `StringDecoder[D]` if one can be found, fails compilation otherwise.
    *
    * This is equivalent to calling `implicitly[StringDecoder[D]]`, with the following advantages:
    *  - more pleasant syntax.
    *  - faster, since the resulting bytecode doesn't contain a method call.
    */
  def apply[D](implicit ev: StringDecoder[D]): StringDecoder[D] = macro imp.summon[StringDecoder[D]]

  /** Creates an instance of [[StringDecoder]] from the specified decoding function.
    *
    * @param f how to decode to `D`.
    * @tparam D decoded type
    */
  def from[D](f: String ⇒ Result[DecodeError, D]): StringDecoder[D] = Decoder.from(f)

  /** Creates an instance of [[StringDecoder]] from the specified, potentially unsafe, decoding function.
    *
    * This method expects the specified decoding function to be able to fail by throwing exceptions. These will be
    * caught and wrapped in [[DecodeError]].
    *
    * {{{
    * scala> val decoder = StringDecoder.decoder("Int")(_.toInt)
    *
    * scala> decoder("1")
    * res1: kantan.codecs.Result[DecodeError, Int] = Success(1)
    *
    * scala> decoder("foobar")
    * res2: kantan.codecs.Result[DecodeError, Int] = Failure(DecodeError(Not a valid Int: 'foobar'))
    * }}}
    *
    * @param typeName name of the decoded type (used in error messages).
    * @param f decoding function.
    * @tparam D decoded type.
    */
  def decoder[D](typeName: String)(f: String ⇒ D): String ⇒ Result[DecodeError, D] =
    s ⇒ Result.nonFatal(f(s)).leftMap(t ⇒ DecodeError(s"Not a valid $typeName: '$s'", t))

  /** Creates a [[StringDecoder]] instance for `java.util.Date`.
    *
    * {{{
    * scala> import java.text.SimpleDateFormat
    * scala> import java.util.Date
    *
    * scala> implicit val decoder = StringDecoder.dateDecoder(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz"))
    *
    * scala> decoder.decode("2016-01-17T22:03:12.012UTC")
    * res1: kantan.codecs.Result[DecodeError, Date] = Success(Sun Jan 17 23:03:12 CET 2016)
    * }}}
    *
    * @param format format used when parsing date values.
    */
  def dateDecoder(format: DateFormat): StringDecoder[Date] =
    StringDecoder.from(StringDecoder.decoder("Date")(s ⇒ format.synchronized(format.parse(s))))
}
