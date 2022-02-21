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
import connectors.API1811.httpParsers.FinancialTransactionsHttpParser.FinancialTransactionsReads
import models.API1811._
import play.api.http.Status
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import utils.TestConstantsAPI1811.{fullFinancialTransactions, fullFinancialTransactionsJsonEIS}

class FinancialTransactionsHttpParserSpec extends SpecBase {

  "The FinancialTransactionsHttpParser" when {

    "the http response status is 200 OK and matches expected Schema" should {

      val httpResponse = HttpResponse(Status.OK, fullFinancialTransactionsJsonEIS.toString)

      val expected = Right(fullFinancialTransactions)

      val result = FinancialTransactionsReads.read("", "", httpResponse)

      "return a FinancialTransactions instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 200 OK but the response is not as expected" should {

      val httpResponse = HttpResponse(Status.OK, Json.obj("foo" -> "bar").toString)

      val expected = Left(Error(BAD_REQUEST,
        "UNEXPECTED_JSON_FORMAT - The downstream service responded with json which did not match the expected format."))

      val result = FinancialTransactionsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (single error)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST, "ERROR MESSAGE")

      val expected = Left(Error(
        code = BAD_REQUEST,
        reason = "ERROR MESSAGE"
      ))

      val result = FinancialTransactionsReads.read("", "", httpResponse)

      "return a Error instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (Unexpected Json Returned)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST, Json.obj("foo" -> "bar").toString)

      val expected = Left(Error(BAD_REQUEST, """{"foo":"bar"}"""))

      val result = FinancialTransactionsReads.read("", "", httpResponse)

      "return a 400 BAD_REQUEST (Unexpected JSON returned)" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (Bad Json Returned)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST, "Banana")

      val expected = Left(Error(BAD_REQUEST,"Banana"))

      val result = FinancialTransactionsReads.read("", "", httpResponse)

      "return a 400 BAD_REQUEST (BAD Json returned)" in {
        result shouldEqual expected
      }

    }

    "the http response status is 500 ISE" should {

      val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, "message")

      val expected = Left(Error( code = INTERNAL_SERVER_ERROR, reason = "message"))

      val result = FinancialTransactionsReads.read("", "", httpResponse)

      "return an ISE" in {
        result shouldEqual expected
      }

    }

    "the http response status is NOT_FOUND" should {

      val httpResponse = HttpResponse(Status.NOT_FOUND,"")

      val expected = Left(Error(Status.NOT_FOUND,""))

      val result = FinancialTransactionsReads.read("", "", httpResponse)

      "return an NOT_FOUND " in {
        result shouldEqual expected
      }
    }
  }
}
