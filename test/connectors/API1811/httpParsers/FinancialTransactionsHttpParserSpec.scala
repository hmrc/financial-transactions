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

package connectors.API1811.httpParsers

import base.SpecBase
import models.API1811._
import play.api.http.Status
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import utils.API1811.TestConstants._

class FinancialTransactionsHttpParserSpec extends SpecBase {

  val httpParser = new FinancialTransactionsHttpParser()

  "The FinancialTransactionsHttpParser" when {

    "the http response status is 200 OK and matches expected Schema" when {

      "recognised charge types are returned" should {

        val httpResponse = HttpResponse(Status.OK, fullFinancialTransactionsJsonEIS.toString)

        val expected = Right(fullFinancialTransactions)

        val result = httpParser.FinancialTransactionsReads.read("", "", httpResponse)

        "return a FinancialTransactions instance containing financialDetails items with valid charge types" in {
          result shouldEqual expected
        }
      }

      "unrecognised charge types are returned" should {

        val httpResponse = HttpResponse(Status.OK, filteredFinancialJson.toString)

        val expected = Right(fullFinancialTransactions.copy(documentDetails = Seq()))

        val result = httpParser.FinancialTransactionsReads.read("", "", httpResponse)

        "return a FinancialTransactions instance that has had the invalid financialDetails items filtered out" in {
          result shouldEqual expected
        }
      }
    }

    "the http response status is 200 OK but the response is not as expected" should {

      val httpResponse = HttpResponse(Status.OK, Json.obj("foo" -> "bar").toString)

      val expected = Left(Error(BAD_REQUEST,
        "UNEXPECTED_JSON_FORMAT - The downstream service responded with json which did not match the expected format."))

      val result = httpParser.FinancialTransactionsReads.read("", "", httpResponse)

      "return an Error model with an UNEXPECTED_JSON_FORMAT error message" in {
        result shouldEqual expected
      }
    }

    "the http response status is NOT_FOUND" should {

      val httpResponse = HttpResponse(Status.NOT_FOUND,"")

      val expected = Left(Error(Status.NOT_FOUND,""))

      val result = httpParser.FinancialTransactionsReads.read("", "", httpResponse)

      "return a NOT_FOUND status in an Error model" in {
        result shouldEqual expected
      }
    }

    "the http response status is something unexpected" should {

      val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, "message")

      val expected = Left(Error( code = INTERNAL_SERVER_ERROR, reason = "message"))

      val result = httpParser.FinancialTransactionsReads.read("", "", httpResponse)

      "return the status in an Error model" in {
        result shouldEqual expected
      }
    }
  }
}
