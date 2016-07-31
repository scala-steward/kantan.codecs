/*
 * Copyright 2016 Nicolas Rinaudo
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

package kantan.codecs.strings.joda.time

import kantan.codecs.Result
import kantan.codecs.strings.{DecodeError, StringCodec}
import org.joda.time._
import org.joda.time.format.DateTimeFormatter

trait JodaTimeInstances {
  private def failure(input: String, tpe: String): DecodeError =
    DecodeError(s"Not a valid $tpe: '$input'")

  implicit def dateTimeCodec(implicit format: DateTimeFormatter): StringCodec[DateTime] =
    StringCodec(s ⇒ Result.nonFatalOr(failure(s, "DateTime"))(format.parseDateTime(s)))(format.print)

  implicit def localDateCodec(implicit format: DateTimeFormatter): StringCodec[LocalDate] =
    StringCodec(s ⇒ Result.nonFatalOr(failure(s, "LocalDate"))(format.parseLocalDate(s)))(format.print)

  implicit def localDateTimeCodec(implicit format: DateTimeFormatter): StringCodec[LocalDateTime] =
    StringCodec(s ⇒ Result.nonFatalOr(failure(s, "LocalDateTime"))(format.parseLocalDateTime(s)))(format.print)

  implicit def localTimeCodec(implicit format: DateTimeFormatter): StringCodec[LocalTime] =
    StringCodec(s ⇒ Result.nonFatalOr(failure(s, "LocalTime"))(format.parseLocalTime(s)))(format.print)
}
