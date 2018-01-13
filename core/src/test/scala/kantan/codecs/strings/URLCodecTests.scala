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

package kantan.codecs
package strings

import java.net.URL
import laws.discipline._, arbitrary._
import tagged._

class URLCodecTests extends DisciplineSuite {

  checkAll("StringDecoder[URL]", StringDecoderTests[URL].decoder[Int, Int])
  checkAll("StringDecoder[URL]", SerializableTests[StringDecoder[URL]].serializable)

  checkAll("StringEncoder[URL]", StringEncoderTests[URL].encoder[Int, Int])
  checkAll("StringEncoder[URL]", SerializableTests[StringEncoder[URL]].serializable)

  checkAll("StringCodec[URL]", StringCodecTests[URL].codec[Int, Int])

  checkAll("TaggedDecoder[URL]", DecoderTests[String, URL, DecodeError, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[URL]", EncoderTests[String, URL, tagged.type].encoder[Int, Int])

}
