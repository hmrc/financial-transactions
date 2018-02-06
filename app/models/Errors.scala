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

import play.api.http.Status
import play.api.libs.json.{Format, Json}

sealed trait Errors

case class Error(code: String, reason: String) extends Errors

object Error {
  implicit val format: Format[Error] = Json.format[Error]
}

case class MultiError(failures: Seq[Error]) extends Errors

object MultiError {
  implicit val format: Format[MultiError] = Json.format[MultiError]
}

case class ErrorResponse(status: Int, error: Errors)


object UnauthenticatedError extends Error(Status.UNAUTHORIZED.toString, "Not authenticated")
object ForbiddenError extends Error(Status.FORBIDDEN.toString, "Not authorised")

object InvalidJsonResponse extends ErrorResponse(
  status = Status.INTERNAL_SERVER_ERROR,
  error = Error(
    code = Status.INTERNAL_SERVER_ERROR.toString,
    reason = "The response did not contain valid json."
  )
)

object UnexpectedJsonFormat extends ErrorResponse(
  status = Status.INTERNAL_SERVER_ERROR,
  error = Error(
    code = Status.INTERNAL_SERVER_ERROR.toString,
    reason = "The downstream service responded with json which did not match the expected format."
  )
)

object UnexpectedResponse extends ErrorResponse(
  status = Status.INTERNAL_SERVER_ERROR,
  error = Error(
    code = Status.INTERNAL_SERVER_ERROR.toString,
    reason = "The downstream service responded with an unexpected response."
  )
)

object InvalidTaxRegime extends Error(
  code = Status.BAD_REQUEST.toString,
  reason = "The supplied Tax Regime is invalid."
)