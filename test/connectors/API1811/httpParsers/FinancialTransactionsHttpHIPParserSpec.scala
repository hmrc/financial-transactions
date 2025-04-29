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

import base.SpecBase
import play.api.http.Status
import uk.gov.hmrc.http.HttpResponse
import utils.API1811.TestConstantsHIP._

class FinancialTransactionsHttpHIPParserSpec extends SpecBase {

  "The FinancialTransactionsHttpParser" when {

    "the http response status is 200 OK and matches expected Schema" when {

      "charge types are returned" should {

        val httpResponse = HttpResponse(Status.OK, fullFinancialTransactionsHIPJson.toString)

        val expected = Right(fullFinancialTransactionsHIP)

        val result = FinancialTransactionsHttpHIPParser.FinancialTransactionsHIPReads.read("", "", httpResponse)

        "return a FinancialTransactions instance containing financialDetails items" in {
          result shouldEqual expected
        }
      }
    }

    "return BusinessError when status is 400 or 404" in {
      val httpResponse = HttpResponse(Status.BAD_REQUEST, businessErrorJson.toString)
      val expected = Left(Left(businessErrorModel))
      val result = FinancialTransactionsHttpHIPParser.FinancialTransactionsHIPReads.read("", "", httpResponse)

      result shouldEqual expected
    }

    "return TechnicalError when status is 500 or 503" in {
      val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, technicalErrorJson.toString)
      val expected = Left(Right(technicalErrorModel))
      val result = FinancialTransactionsHttpHIPParser.FinancialTransactionsHIPReads.read("", "", httpResponse)

      result shouldEqual expected
    }
  }
}