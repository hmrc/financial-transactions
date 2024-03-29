/*
 * Copyright 2023 HM Revenue & Customs
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

import models.API1811.{Error, FinancialTransactions}
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND, OK}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.LoggerUtil

object FinancialTransactionsHttpParser extends LoggerUtil {

  type FinancialTransactionsResponse = Either[Error, FinancialTransactions]

  implicit object FinancialTransactionsReads extends HttpReads[FinancialTransactionsResponse] {
    override def read(method: String, url: String, response: HttpResponse): FinancialTransactionsResponse =
      response.status match {
        case OK =>
          response.json.validate[FinancialTransactions].fold(
            invalid => {
              logger.warn("[FinancialTransactionsReads][read] Json Error Parsing Successful EIS Response")
              logger.debug(s"[FinancialTransactionsReads][read] EIS Response: ${response.json}\nJson Errors: $invalid")
              Left(Error(BAD_REQUEST, "UNEXPECTED_JSON_FORMAT - " +
                "The downstream service responded with json which did not match the expected format."))
            },
            valid => {
              logger.debug(s"[FinancialTransactionsReads][read] EIS Response: \n\n${response.json}")
              logger.debug(s"[FinancialTransactionsReads][read] Financial Transactions Model: \n\n$valid")
              Right(valid)
            }
          )
        case NOT_FOUND =>
          logger.debug("[FinancialTransactionsReads][read] Error received: " + response)
          Left(Error(response.status,response.body))
        case _ =>
          Left(Error(response.status, response.body))
      }
  }
}
