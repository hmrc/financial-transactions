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

import models.API1811.{BusinessError, Error, FinancialTransactionsHIP, HipWrappedError, TechnicalError}
import play.api.http.Status._
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.LoggerUtil

import scala.util.Try

object FinancialTransactionsHttpHIPParser extends LoggerUtil {

  type FinancialTransactionsHIPResponse = Either[Error, FinancialTransactionsHIP]

  implicit object FinancialTransactionsHIPReads extends HttpReads[FinancialTransactionsHIPResponse] {

    override def read(method: String, url: String, response: HttpResponse): FinancialTransactionsHIPResponse = {

      val status = response.status
      val body = Option(response.body).getOrElse("")

      logger.info(s"[FinancialTransactionsHIPReads] status=$status")

      status match {

        case CREATED =>
          parseJson(body) match {
            case Some(json) => handleSuccess(json)
            case None => invalidJsonError("Invalid JSON in success response")
          }

        case UNPROCESSABLE_ENTITY =>
          parseJson(body) match {
            case Some(json) => handle422(json, response)
            case None => fallbackError(response)
          }

        case BAD_REQUEST | FORBIDDEN | NOT_FOUND | CONFLICT |
             INTERNAL_SERVER_ERROR | SERVICE_UNAVAILABLE =>
          logger.error(s"[FinancialTransactionsHIPReads] error status=$status body=$body")
          handleJsonOrFallback(response)

        case _ =>
          logger.error(s"[FinancialTransactionsHIPReads] unexpected status=$status body=$body")
          fallbackError(response)
      }
    }

    private def handleSuccess(json: JsValue): FinancialTransactionsHIPResponse =
      json.validate[FinancialTransactionsHIP] match {

        case JsSuccess(value, _) =>
          logger.info("[FinancialTransactionsHIPReads] success parsed")
          Right(value)

        case JsError(errors) =>
          logger.error(s"[FinancialTransactionsHIPReads] validation failed: $errors")
          invalidJsonError("UNEXPECTED_JSON_FORMAT")
      }

    private def invalidJsonError(msg: String): Left[Error, Nothing] = {
      logger.error(s"[FinancialTransactionsHIPReads] $msg")
      Left(Error(INTERNAL_SERVER_ERROR, msg))
    }

    private def handle422(json: JsValue, response: HttpResponse): Left[Error, Nothing] =
      (json \ "errors").validate[BusinessError] match {

        case JsSuccess(err, _) =>
          (err.code, err.text) match {

            case ("016", _) =>
              Left(Error(NOT_FOUND, "ID number did not match any penalty data"))

            case ("018", _) =>
              Left(Error(NOT_FOUND, "ID number did not match any financial data"))

            case _ =>
              logger.error(s"[FinancialTransactionsHIPReads] unknown 422 error: ${response.body}")
              handleJsonOrFallback(response)
          }

        case _ =>
          logger.error(s"[FinancialTransactionsHIPReads] invalid 422 format")
          handleJsonOrFallback(response)
      }

    private def handleJsonOrFallback(response: HttpResponse): Left[Error, Nothing] =
      parseJson(response.body) match {

        case Some(json) => extractJsonError(json, response.status)

        case None => fallbackError(response)
      }

    private def extractJsonError(json: JsValue, status: Int): Left[Error, Nothing] = {

      val error = (json \ "response" \ "error").validate[TechnicalError]
      val failures = (json \ "response" \ "failures").validate[Seq[HipWrappedError]]
      val businessError = (json \ "errors").validate[BusinessError]

      val message = (error, failures, businessError) match {

        case (JsSuccess(e, _), _, _) =>
          s"${e.code} - ${e.message}"

        case (_, JsSuccess(fs, _), _) =>
          fs.map(f => s"${f.`type`} - ${f.reason}").mkString(", ")

        case (_, _, JsSuccess(e, _)) =>
          s"${e.code} - ${e.text}"

        case _ =>
          json.toString()
      }

      logger.error(s"[FinancialTransactionsHIPReads] parsed error: $message")
      Left(Error(status, message))
    }

    private def fallbackError(response: HttpResponse): Left[Error, Nothing] = {

      val body = Option(response.body).getOrElse("")

      val message = body match {
        case b if b.contains("Send timeout") => "TIMEOUT - Downstream timeout"
        case b if b.contains("502 Bad Gateway") => "GATEWAY_ERROR - Bad Gateway"
        case b if b.toLowerCase.contains("<html") => "GATEWAY_ERROR - HTML response"
        case b if b.isEmpty => "EMPTY_RESPONSE"
        case b => b.take(MULTIPLE_CHOICES)
      }

      logger.error(s"[FinancialTransactionsHIPReads] fallback error: $message")

      Left(Error(response.status, message))
    }

    private def parseJson(body: String): Option[JsValue] =
      Try(Json.parse(body)).toOption
  }
}
