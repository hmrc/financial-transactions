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

package connectors.API1811.httpParsers

import models.API1811.{BusinessError, FinancialTransactionsHIP, TechnicalError}
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK, SERVICE_UNAVAILABLE}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.LoggerUtil

object FinancialTransactionsHttpHIPParser extends LoggerUtil {

  type FinancialTransactionsHIPResponse = Either[Either[BusinessError, TechnicalError], FinancialTransactionsHIP]

  implicit object FinancialTransactionsHIPReads extends HttpReads[FinancialTransactionsHIPResponse] {
    override def read(method: String, url: String, response: HttpResponse): FinancialTransactionsHIPResponse = {
      response.status match {

        case OK =>
          response.json.validate[FinancialTransactionsHIP] match {
            case JsSuccess(valid, _) =>
              logger.debug(s"[HIP Parser] Parsed FinancialTransactionsHIP: $valid")
              Right(valid)
            case JsError(errors) =>
              logger.warn(s"[HIP Parser] Failed to parse FinancialTransactionsHIP. Errors: $errors")
              Left(Left(BusinessError("unknown", "INVALID_SUCCESS_RESPONSE", s"JSON structure did not match: $errors")))
          }

        case BAD_REQUEST | NOT_FOUND =>
          parseBusinessError(response)

        case INTERNAL_SERVER_ERROR | SERVICE_UNAVAILABLE =>
          parseTechnicalError(response)

        case _ =>
          logger.warn(s"[HIP Parser] Unexpected status ${response.status}, body: ${response.body}")
          parseTechnicalError(response)
      }
    }

    private def parseBusinessError(response: HttpResponse): FinancialTransactionsHIPResponse = {
      val json = Json.parse(response.body)

      json.validate[BusinessError] match {
        case JsSuccess(error, _) =>
          logger.warn(s"[HIP Parser] Business error parsed (flat): $error")
          Left(Left(error))

        case JsError(_) =>
          (json \ "errors").validate[Seq[BusinessError]] match {
            case JsSuccess(errors, _) if errors.nonEmpty =>
              logger.warn(s"[HIP Parser] Business error parsed from 'errors[0]': ${errors.head}")
              Left(Left(errors.head))
            case _ =>
              logger.warn("[HIP Parser] Failed to parse BusinessError from any format, falling back to technical error")
              parseTechnicalError(response)
          }
      }
    }

    private def parseTechnicalError(response: HttpResponse): FinancialTransactionsHIPResponse = {
      val json = Json.parse(response.body)

      json.validate[TechnicalError] match {
        case JsSuccess(error, _) =>
          logger.warn(s"[HIP Parser] Technical error parsed (flat): $error")
          Left(Right(error))

        case JsError(_) =>
          (json \ "error").validate[TechnicalError] match {
            case JsSuccess(error, _) =>
              logger.warn(s"[HIP Parser] Technical error parsed from 'error' wrapper: $error")
              Left(Right(error))
            case JsError(errors) =>
              logger.error(s"[HIP Parser] Could not parse TechnicalError. Body: ${response.body.take(200)}")
              Left(Right(TechnicalError(
                code = "UNKNOWN",
                message = s"Unrecognized error format: $errors",
                logId = "unknown"
              )))
          }
      }
    }
  }
}