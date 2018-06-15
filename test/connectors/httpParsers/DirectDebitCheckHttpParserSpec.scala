/*
 * Copyright 2018 HM Revenue & Customs
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

import base.SpecBase
import connectors.httpParsers.DirectDebitCheckHttpParser.DirectDebitCheckReads
import models._
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse

class DirectDebitCheckHttpParserSpec extends SpecBase {

  "The FinancialTransactionsHttpParser" when {

    "the http response status is 200 OK and matches expected Schema" should {

      val httpResponse = HttpResponse(Status.OK, responseJson = Some(
        Json.obj(
          "directDebitMandateFound" -> true,
          "directDebitDetails" -> Json.arr(
            Json.obj(
              "directDebitInstructionNumber" -> "000000001234567898",
              "directDebitPlanType" -> "VPP",
              "dateCreated" -> "2018-04-08",
              "accountHolderName" -> "A PERSON",
              "sortCode" -> "0000000",
              "accountNumber" -> "000000001"
            ),
            Json.obj(
              "directDebitInstructionNumber" -> "000000001234567898",
              "directDebitPlanType" -> "VPP",
              "dateCreated" -> "2018-04-08",
              "accountHolderName" -> "ANOTHER PERSON",
              "sortCode" -> "000000",
              "accountNumber" -> "000000002"
            )
          )
        )
      ))

      val expected: Either[Nothing, Boolean] = Right(true)

      val result = DirectDebitCheckReads.read("", "", httpResponse)

      "return a DirectDebits instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 200 OK but the response is not as expected" should {

      val httpResponse = HttpResponse(Status.OK, responseJson = Some(Json.obj("foo" -> "bar")))

      val expected = Left(UnexpectedJsonFormat)

      val result = DirectDebitCheckReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (single error)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "code" -> "CODE",
          "reason" -> "ERROR MESSAGE"
        ))
      )

      val expected = Left(ErrorResponse(
        Status.BAD_REQUEST,
        Error(
          code = "CODE",
          reason = "ERROR MESSAGE"
        )
      ))

      val result = DirectDebitCheckReads.read("", "", httpResponse)

      "return a Error instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (multiple errors)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
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
        ))
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

      val result = DirectDebitCheckReads.read("", "", httpResponse)

      "return a MultiError" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (Unexpected Json Returned)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST, responseJson = Some(Json.obj("foo" -> "bar")))

      val expected = Left(UnexpectedJsonFormat)

      val result = DirectDebitCheckReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (Bad Json Returned)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST, responseString = Some("Banana"))

      val expected = Left(InvalidJsonResponse)

      val result = DirectDebitCheckReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 500 Internal Server Error" should {

      val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR,
        responseJson = Some(Json.obj(
          "code" -> "code",
          "reason" -> "message"
        ))
      )

      val expected = Left(ErrorResponse(
        Status.INTERNAL_SERVER_ERROR,
        Error(
          code = "code",
          reason = "message"
        )
      ))

      val result = DirectDebitCheckReads.read("", "", httpResponse)

      "return an Internal Server Error" in {
        result shouldEqual expected
      }

    }

    "the http response status is unexpected" should {

      val httpResponse = HttpResponse(Status.SEE_OTHER)

      val expected = Left(UnexpectedResponse)

      val result = DirectDebitCheckReads.read("", "", httpResponse)

      "return an unexpected error" in {
        result shouldEqual expected
      }

    }

  }

}
