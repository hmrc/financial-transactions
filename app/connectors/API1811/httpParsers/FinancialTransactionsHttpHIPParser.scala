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

import models.API1811.{BusinessError, Error, FinancialTransactionsHIP, TechnicalError}
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND, OK}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.LoggerUtil

object FinancialTransactionsHttpHIPParser extends LoggerUtil {

  type FinancialTransactionsHIPResponse = Either[Error, FinancialTransactionsHIP]

  implicit object FinancialTransactionsHIPReads extends HttpReads[FinancialTransactionsHIPResponse] {

    override def read(method: String, url: String, response: HttpResponse): FinancialTransactionsHIPResponse = {

      response.status match {
        case OK =>
          response.json.validate[FinancialTransactionsHIP].fold(
            invalid => {
              logger.warn("[FinancialTransactionsHIPReads][read] - Invalid JSON when parsing HIP Success Response")
              logger.debug(s"[FinancialTransactionsHIPReads][read] - Response Body:\n${response.body}\nJSON Errors: $invalid")
              Left(Error(BAD_REQUEST, "Unexpected JSON format for HIP Success response."))
            },
            valid => {
              logger.debug(s"[FinancialTransactionsHIPReads][read] - Successfully parsed FinancialTransactionsHIP model")
              Right(valid)
            }
          )

        case BAD_REQUEST | NOT_FOUND =>
          val technicalError = response.json.validate[TechnicalError]
          val businessError = response.json.validate[BusinessError]

          if (technicalError.isSuccess) {
            logger.warn("[FinancialTransactionsHIPReads][read] - HIP TechnicalError received")
            Left(Error(response.status, response.body))
          } else if (businessError.isSuccess) {
            logger.warn("[FinancialTransactionsHIPReads][read] - HIP BusinessError received")
            Left(Error(response.status, response.body))
          } else {
            logger.warn("[FinancialTransactionsHIPReads][read] - Unknown HIP error format")
            Left(Error(BAD_REQUEST, "Unexpected error format for HIP Error response."))
          }

        case _ =>
          logger.warn(s"[FinancialTransactionsHIPReads][read] - Unexpected status ${response.status} received")
          Left(Error(response.status, response.body))
      }
    }
  }
}
