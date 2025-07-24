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

import models.API1811.{BusinessError, FinancialTransactionsHIP, HipWrappedError, TechnicalError}
import play.api.http.Status._
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsError, JsSuccess, JsValue}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.LoggerUtil


object FinancialTransactionsHttpHIPParser extends LoggerUtil {
  trait FinancialTransactionsFailure

  case class FinancialTransactionsFailureResponse(status: Int) extends FinancialTransactionsFailure

  case object FinancialTransactionsMalformed extends FinancialTransactionsFailure

  case object FinancialTransactionsNoContent extends FinancialTransactionsFailure

  type FinancialTransactionsHIPResponse = Either[FinancialTransactionsFailure, FinancialTransactionsHIP]

  implicit object FinancialTransactionsHIPReads extends HttpReads[FinancialTransactionsHIPResponse] {
    override def read(method: String, url: String, response: HttpResponse): FinancialTransactionsHIPResponse = {
      response.status match {
        case CREATED =>
          handleSuccessResponse(response.json)

        case UNPROCESSABLE_ENTITY =>
          extractErrorResponseBodyFrom422(response.json)

        case status@(BAD_REQUEST | FORBIDDEN | NOT_FOUND | CONFLICT | UNPROCESSABLE_ENTITY | INTERNAL_SERVER_ERROR | SERVICE_UNAVAILABLE) =>
          logger.error(s"[FinancialTransactionsHIPReads][read] Received $status when trying to call HIP FinancialTransactions - with body: ${response.body}")
          handleErrorResponse(response)

        case status =>
          logger.error(s"[FinancialTransactionsHIPReads][read] Received unexpected response from HIP FinancialTransactions, status code: $status and body: ${response.body}")
          Left(FinancialTransactionsFailureResponse(status))
      }
    }

    private def handleSuccessResponse(json: JsValue): FinancialTransactionsHIPResponse = {
      logger.info(s"[FinancialTransactionsHIPReads][read] Success 201 response returned from API#5327")
      json.validate[FinancialTransactionsHIP] match {
        case JsSuccess(valid, _) =>
          logger.debug(s"[HIP Parser] Parsed FinancialTransactionsHIP: $valid")
          Right(valid)
        case JsError(errors) =>
          logger.debug(s"[FinancialTransactionsHIPParser][FinancialTransactionsHIPReads][read] Unable to validate HIP response with CREATED status: $errors")
          Left(FinancialTransactionsMalformed)
      }
    }

    private def extractErrorResponseBodyFrom422(json: JsValue): Left[FinancialTransactionsFailure, Nothing] = {
      def noDataFound(error: BusinessError): Boolean =
        (error.code == "016" && error.text == "Invalid ID Number") || (error.code == "018" && error.text == "No Data Identified")

      (json \ "errors").validate[BusinessError] match {
        case JsSuccess(error, _) if noDataFound(error) =>
          logger.error(s"[FinancialTransactionsHIPReads][read] - Error: ID number did not match any data")
          Left(FinancialTransactionsNoContent)
        case JsSuccess(error, _) =>
          logger.error(s"[FinancialTransactionsHIPReads][read] - 422 Error with code: ${error.code} - ${error.text}")
          Left(FinancialTransactionsFailureResponse(UNPROCESSABLE_ENTITY))
        case _ =>
          logger.error(s"[FinancialTransactionsHIPReads][read] - Unable to parse 422 error body to expected format. Error: $json")
          Left(FinancialTransactionsFailureResponse(UNPROCESSABLE_ENTITY))
      }
    }

    private def handleErrorResponse(response: HttpResponse): Left[FinancialTransactionsFailure, Nothing] = {
      val status = response.status
      val error = (response.json \ "response" \ "error").validate[TechnicalError]
      val errors = (response.json \ "response" \ "failures").validate[Seq[HipWrappedError]]
      val errorMsg = (error, errors) match {
        case (JsSuccess(error, _), _) => s"${error.code} - ${error.message}"
        case (_, JsSuccess(errors, _)) => errors.map(err => s"${err.`type`} - ${err.reason}").mkString(",\n")
        case _ => response.json.toString()
      }
      logger.error(s"[FinancialTransactionsHIPReads][read] $status error: $errorMsg")
      Left(FinancialTransactionsFailureResponse(status))
    }
  }
}