/*
 * Copyright 2019 HM Revenue & Customs
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

package connectors.httpParsers

import models.{DirectDebits, UnexpectedJsonFormat, UnexpectedResponse}
import play.api.Logger
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object DirectDebitCheckHttpParser extends ResponseHttpParsers {

  implicit object DirectDebitCheckReads extends HttpReads[HttpGetResult[Boolean]] {
    override def read(method: String, url: String, response: HttpResponse): HttpGetResult[Boolean] = {
      response.status match {
        case OK =>
          response.json.validate[DirectDebits].fold(
            invalid => {
              Logger.warn("[DirectDebitCheckReads][read] Json Error Parsing Successful DES Response")
              Logger.debug(s"[DirectDebitCheckReads][read] DES Response: ${response.json}\nJson Errors: $invalid")
              Left(UnexpectedJsonFormat)
            },
            valid => {
              Logger.debug(s"[DirectDebitCheckReads][read] DES Response: \n\n${response.json}")
              Logger.debug(s"[DirectDebitCheckReads][read] Direct Debits Model: \n\n$valid")
              Right(valid.directDebitMandateFound)
            }
          )
        case status if status >= 400 && status < 600 =>
          Logger.debug(s"[DirectDebitCheckReads][read] $status returned from DES")
          handleErrorResponse(response)
        case _ =>
          Logger.debug(s"[DirectDebitCheckReads][read] Unexpected Response")
          Left(UnexpectedResponse)
      }
    }
  }

}
