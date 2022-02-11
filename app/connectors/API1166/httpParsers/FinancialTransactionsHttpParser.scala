/*
 * Copyright 2022 HM Revenue & Customs
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

package connectors.API1166.httpParsers

import connectors.httpParsers.ResponseHttpParsers
import models.API1166.FinancialTransactions
import models.API1166.{UnexpectedJsonFormat, UnexpectedResponse}
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.LoggerUtil

object FinancialTransactionsHttpParser extends ResponseHttpParsers with LoggerUtil {

  implicit object FinancialTransactionsReads extends HttpReads[HttpGetResult[FinancialTransactions]] {
    override def read(method: String, url: String, response: HttpResponse): HttpGetResult[FinancialTransactions] = {
      response.status match {
        case OK =>
          response.json.validate[FinancialTransactions].fold(
            invalid => {
              logger.warn("[FinancialTransactionsReads][read] Json Error Parsing Successful DES Response")
              logger.debug(s"[FinancialTransactionsReads][read] DES Response: ${response.json}\nJson Errors: $invalid")
              Left(UnexpectedJsonFormat)
            },
            valid => {
              logger.debug(s"[FinancialTransactionsReads][read] DES Response: \n\n${response.json}")
              logger.debug(s"[FinancialTransactionsReads][read] Financial Transactions Model: \n\n$valid")
              Right(valid)
            }
          )
        case status if status >= 400 && status < 600 =>
          logger.debug(s"[FinancialTransactionsReads][read] $status returned from DES")
          handleErrorResponse(response)
        case _ =>
          logger.debug(s"[FinancialTransactionsReads][read] Unexpected Response")
          Left(UnexpectedResponse)
      }
    }
  }
}
