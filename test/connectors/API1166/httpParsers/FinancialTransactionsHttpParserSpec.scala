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

import base.SpecBase
import models.API1166._
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse
import utils.API1166.TestConstants.{fullFinancialTransactions, fullFinancialTransactionsJson, fullSubItemJson}

class FinancialTransactionsHttpParserSpec extends SpecBase {

  val httpParser = new FinancialTransactionsHttpParser()

  "The FinancialTransactionsHttpParser" when {

    "the http response status is 200 OK and matches expected Schema" when {

      "recognised charge types are returned" should {

        val httpResponse = HttpResponse(Status.OK, fullFinancialTransactionsJson.toString)

        val expected = Right(fullFinancialTransactions)

        val result = httpParser.FinancialTransactionsReads.read("", "", httpResponse)

        "return a FinancialTransactions instance containing financialTransactions items with valid charge types" in {
          result shouldEqual expected
        }
      }

      "unrecognised charge types are returned" should {

        val invalidChargeTypeJson: JsObject = Json.obj(
          "chargeType" -> "Made Up Charge Type",
          "mainType" -> "2100",
          "periodKey" -> "13RL",
          "periodKeyDescription" -> "abcde",
          "taxPeriodFrom" -> "2017-04-06",
          "taxPeriodTo" -> "2018-04-05",
          "businessPartner" -> "6622334455",
          "contractAccountCategory" -> "02",
          "contractAccount" -> "X",
          "contractObjectType" -> "ABCD",
          "contractObject" -> "00000003000000002757",
          "sapDocumentNumber" -> "1040000872",
          "sapDocumentNumberItem" -> "XM00",
          "chargeReference" -> "XM002610011594",
          "mainTransaction" -> "1234",
          "subTransaction" -> "5678",
          "originalAmount" -> 3400,
          "outstandingAmount" -> 1400,
          "clearedAmount" -> 2000,
          "accruedInterest" -> 0.23,
          "items" -> Json.arr(fullSubItemJson)
        )

        val filteredFinancialJson = Json.obj(
          "idType" -> "MTDBSA",
          "idNumber" -> "XQIT00000000001",
          "regimeType" -> "ITSA",
          "processingDate" -> "2017-03-07T22:55:56.987Z",
          "financialTransactions" -> Json.arr(invalidChargeTypeJson)
        )

        val httpResponse = HttpResponse(Status.OK, filteredFinancialJson.toString)

        val expected = Right(fullFinancialTransactions.copy(financialTransactions = Seq()))

        val result = httpParser.FinancialTransactionsReads.read("", "", httpResponse)

        "return a FinancialTransactions instance that has had the invalid financialTransactions items filtered out" in {
          result shouldEqual expected
        }
      }
    }

    "the http response status is 200 OK but the response is not as expected" should {

      val httpResponse = HttpResponse(Status.OK, Json.obj("foo" -> "bar").toString)

      val expected = Left(UnexpectedJsonFormat)

      val result = httpParser.FinancialTransactionsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (single error)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST, Json.obj(
        "code" -> "CODE",
        "reason" -> "ERROR MESSAGE"
      ).toString
      )

      val expected = Left(ErrorResponse(
        Status.BAD_REQUEST,
        Error(
          code = "CODE",
          reason = "ERROR MESSAGE"
        )
      ))

      val result = httpParser.FinancialTransactionsReads.read("", "", httpResponse)

      "return a Error instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (multiple errors)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST, Json.obj(
        "failures" -> Json.arr(
          Json.obj(
            "code" -> "ERROR CODE 1",
            "reason" -> "ERROR MESSAGE 1"
          ),
          Json.obj(
            "code" -> "ERROR CODE 2",
            "reason" -> "ERROR MESSAGE 2"
          )
        )
      ).toString
      )

      val expected = Left(ErrorResponse(
        Status.BAD_REQUEST,
        MultiError(
          failures = Seq(
            Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
            Error(code = "ERROR CODE 2", reason = "ERROR MESSAGE 2")
          )
        )
      ))

      val result = httpParser.FinancialTransactionsReads.read("", "", httpResponse)

      "return a MultiError" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (Unexpected Json Returned)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST, Json.obj("foo" -> "bar").toString)

      val expected = Left(UnexpectedJsonFormat)

      val result = httpParser.FinancialTransactionsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (Bad Json Returned)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST, "Banana")

      val expected = Left(InvalidJsonResponse)

      val result = httpParser.FinancialTransactionsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 500 ISE" should {

      val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, Json.obj(
        "code" -> "code",
        "reason" -> "message"
      ).toString
      )

      val expected = Left(ErrorResponse(
        Status.INTERNAL_SERVER_ERROR,
        Error(
          code = "code",
          reason = "message"
        )
      ))

      val result = httpParser.FinancialTransactionsReads.read("", "", httpResponse)

      "return an ISE" in {
        result shouldEqual expected
      }

    }

    "the http response status is unexpected" should {

      val httpResponse = HttpResponse(Status.SEE_OTHER,"")

      val expected = Left(UnexpectedResponse)

      val result = httpParser.FinancialTransactionsReads.read("", "", httpResponse)

      "return an ISE" in {
        result shouldEqual expected
      }
    }
  }
}