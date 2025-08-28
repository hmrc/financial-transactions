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
import play.api.libs.json.{JsError, JsSuccess, JsValue}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.LoggerUtil

object FinancialTransactionsHttpHIPParser extends LoggerUtil {

  type FinancialTransactionsHIPResponse = Either[Error, FinancialTransactionsHIP]

  implicit object FinancialTransactionsHIPReads extends HttpReads[FinancialTransactionsHIPResponse] {
    override def read(method: String, url: String, response: HttpResponse): FinancialTransactionsHIPResponse =
      response.status match {
        case CREATED =>
          handleSuccessResponse(response.json)

        case UNPROCESSABLE_ENTITY =>
          extractErrorResponseBodyFrom422(response)

        case status @ (BAD_REQUEST | FORBIDDEN | NOT_FOUND | CONFLICT | UNPROCESSABLE_ENTITY | INTERNAL_SERVER_ERROR | SERVICE_UNAVAILABLE) =>
          logger.error(
            s"[FinancialTransactionsHIPReads][read] Received $status when trying to call HIP FinancialTransactions - with body: ${response.body}")
          handleErrorResponse(response)

        case status =>
          logger.error(
            s"[FinancialTransactionsHIPReads][read] Received unexpected response from HIP FinancialTransactions," +
              s" status code: $status and body: ${response.body}")
          Left(Error(response.status, response.body))
      }

    private def handleSuccessResponse(json: JsValue): FinancialTransactionsHIPResponse = {
      logger.info(s"[FinancialTransactionsHIPReads][read] Success 201 response returned from API#5327")
      json.validate[FinancialTransactionsHIP] match {
        case JsSuccess(valid, _) =>
          logger.info(s"[FinancialTransactionsHIPReads][read] FinancialTransactions successfully validated from success response")
          Right(valid)
        case JsError(errors) =>
          logger.error(s"[FinancialTransactionsHIPReads][read] Json validation of 201 body failed with errors: $errors")
          Left(
            Error(
              INTERNAL_SERVER_ERROR,
              "UNEXPECTED_JSON_FORMAT - The downstream service responded with json which did not match the expected format."))
      }
    }

    private def extractErrorResponseBodyFrom422(response: HttpResponse): Left[Error, Nothing] =
      (response.json \ "errors").validate[BusinessError] match {
        case JsSuccess(error, _) if error.code == "016" && error.text == "Invalid ID Number" =>
          logger.error(s"[FinancialTransactionsHIPReads][read] - Error: ID number did not match any penalty data")
          Left(Error(NOT_FOUND, "ID number did not match any penalty data"))
        case JsSuccess(error, _) if error.code == "018" && error.text == "No Data Identified" =>
          logger.error(s"[FinancialTransactionsHIPReads][read] - Error: ID number did not match any financial data")
          Left(Error(NOT_FOUND, "ID number did not match any financial data"))
        case _ =>
          logger.error(s"[FinancialTransactionsHIPReads][read] - 422 error: ${response.body}")
          handleErrorResponse(response)
      }

    private def handleErrorResponse(response: HttpResponse): Left[Error, Nothing] = {
      val status        = response.status
      val error         = (response.json \ "response" \ "error").validate[TechnicalError]
      val errors        = (response.json \ "response" \ "failures").validate[Seq[HipWrappedError]]
      val businessError = (response.json \ "errors").validate[BusinessError]
      val errorMsg = (error, errors, businessError) match {
        case (JsSuccess(error, _), _, _)  => s"${error.code} - ${error.message}"
        case (_, JsSuccess(errors, _), _) => errors.map(err => s"${err.`type`} - ${err.reason}").mkString(",\n")
        case (_, _, JsSuccess(error, _))  => s"${error.code} - ${error.text}"
        case _                            => response.json.toString()
      }
      logger.error(s"[FinancialTransactionsHIPReads][read] $status error: $errorMsg")
      Left(Error(status, errorMsg))
    }
  }
}
