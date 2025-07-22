/*
 * Copyright 2025 HM Revenue & Customs
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

package models.hip_API1812

import play.api.libs.json.{Json, Reads}

case class HIPErrorResponse(errors: HIPBusinessError)

case class HIPBusinessError(processingDate: String, code: String, text: String)

case class HIPTechnicalErrorResponse(error: HIPTechnicalError)

case class HIPTechnicalError(code: String, message: String, logID: String)

case class HIPFailure(`type`: String, reason: String)
case class HIPFailureResponse(failures: Seq[HIPFailure])
case class HIPOriginResponse(origin: String, response: HIPFailureResponse)

object HIPErrorResponse {
  implicit val reads: Reads[HIPErrorResponse] = Json.reads[HIPErrorResponse]
}

object HIPBusinessError {
  implicit val reads: Reads[HIPBusinessError] = Json.reads[HIPBusinessError]
}

object HIPTechnicalErrorResponse {
  implicit val reads: Reads[HIPTechnicalErrorResponse] = Json.reads[HIPTechnicalErrorResponse]
}

object HIPTechnicalError {
  implicit val reads: Reads[HIPTechnicalError] = Json.reads[HIPTechnicalError]
}

object HIPFailure {
  implicit val reads: Reads[HIPFailure] = Json.reads[HIPFailure]
}
object HIPFailureResponse {
  implicit val reads: Reads[HIPFailureResponse] = Json.reads[HIPFailureResponse]
}
object HIPOriginResponse {
  implicit val reads: Reads[HIPOriginResponse] = Json.reads[HIPOriginResponse]
}