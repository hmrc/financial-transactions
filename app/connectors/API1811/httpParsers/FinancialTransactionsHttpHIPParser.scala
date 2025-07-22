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

import models.API1811.{BusinessError, ErrorResponse, FinancialTransactionsHIP, HipWrappedError, TechnicalError}
import play.api.http.Status.{BAD_REQUEST, CREATED, INTERNAL_SERVER_ERROR, NOT_FOUND, SERVICE_UNAVAILABLE}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.LoggerUtil

object FinancialTransactionsHttpHIPParser extends LoggerUtil {

  type FinancialTransactionsHIPResponse = Either[ErrorResponse, FinancialTransactionsHIP]

  implicit object FinancialTransactionsHIPReads extends HttpReads[FinancialTransactionsHIPResponse] {
    override def read(method: String, url: String, response: HttpResponse): FinancialTransactionsHIPResponse = {
      response.status match {

        case CREATED =>
          logger.debug(s"[FinancialTransactionsHIPParser][FinancialTransactionsHIPReads][read] Json response: ${response.json}")
          response.json.validate[FinancialTransactionsHIP] match {
            case JsSuccess(valid, _) =>
              logger.debug(s"[HIP Parser] Parsed FinancialTransactionsHIP: $valid")
              Right(valid)
            case JsError(errors) =>
              logger.debug(s"[FinancialTransactionsHIPParser][FinancialTransactionsHIPReads][read] Unable to validate HIP response with CREATED status: $errors")
              Left(BusinessError("unknown", "INVALID_SUCCESS_RESPONSE", s"JSON structure did not match: $errors"))
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
          Left(error)

        case JsError(_) =>
          (json \ "errors").validate[Seq[BusinessError]] match {
            case JsSuccess(errors, _) if errors.nonEmpty =>
              logger.warn(s"[HIP Parser] Business error parsed from 'errors[0]': ${errors.head}")
              Left(errors.head)

            case _ =>
              (json \ "response").validate[Seq[HipWrappedError]] match {
                case JsSuccess(errors, _) if errors.nonEmpty =>
                  val wrapped = errors.head
                  logger.warn(s"[HIP Parser] HIP wrapped error: $wrapped")
                  Left(BusinessError("HIP", wrapped.`type`, wrapped.reason))

                case _ =>
                  logger.warn("[HIP Parser] Failed to parse BusinessError from any format, falling back to technical error")
                  parseTechnicalError(response)
              }
          }
      }
    }

    private def parseTechnicalError(response: HttpResponse): FinancialTransactionsHIPResponse = {
      val json = Json.parse(response.body)

      json.validate[TechnicalError] match {
        case JsSuccess(error, _) =>
          logger.warn(s"[HIP Parser] Technical error parsed (flat): $error")
          Left(error)

        case JsError(_) =>
          (json \ "error").validate[TechnicalError] match {
            case JsSuccess(error, _) =>
              logger.warn(s"[HIP Parser] Technical error parsed from 'error' wrapper: $error")
              Left(error)
            case JsError(errors) =>
              logger.error(s"[HIP Parser] Could not parse TechnicalError. Body: ${response.body.take(200)}")
              Left(TechnicalError(code = "UNKNOWN", message = s"Unrecognized error format: $errors", logId = "unknown"))
          }
      }
    }
  }
}