/*
 * Copyright 2018 HM Revenue & Customs
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

package models

import play.api.libs.json.{Format, Json}

sealed trait DesErrors

case class DesError(code: String, reason: String) extends DesErrors

object DesError {
  implicit val format: Format[DesError] = Json.format[DesError]
}

case class DesMultiError(failures: Seq[DesError]) extends DesErrors

object DesMultiError {
  implicit val format: Format[DesMultiError] = Json.format[DesMultiError]
}

object UnexpectedDesResponse extends DesError(
  code = "UNEXPECTED_DES_RESPONSE",
  reason = s"The DES response did not match the expected format"
)