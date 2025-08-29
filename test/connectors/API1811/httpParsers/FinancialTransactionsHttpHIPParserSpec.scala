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
import connectors.API1811.httpParsers.FinancialTransactionsHttpHIPParser._
import models.API1811.Error
import play.api.http.Status
import play.api.http.Status._
import uk.gov.hmrc.http.HttpResponse
import utils.API1811.TestConstantsHIP._

class FinancialTransactionsHttpHIPParserSpec extends SpecBase {

  def financialTransactionsParserReads(httpResponse: HttpResponse): FinancialTransactionsHIPResponse =
    FinancialTransactionsHttpHIPParser.FinancialTransactionsHIPReads.read("GET", "/", httpResponse)

  "The FinancialTransactionsHttpParser" when {

    "the http response status is 201 and matches expected Schema" when {

      "charge types are returned" should {

        val httpResponse = HttpResponse(Status.CREATED, fullFinancialTransactionsHIPJson.toString)

        val expected = Right(fullFinancialTransactionsHIP)

        val result = FinancialTransactionsHttpHIPParser.FinancialTransactionsHIPReads.read("", "", httpResponse)

        "return a FinancialTransactions instance containing financialDetails items" in {
          result shouldEqual expected
        }
      }
    }

    "parsing an UNPROCESSABLE_ENTITY response" should {
      def errorResponse(responseBody: String): HttpResponse = HttpResponse.apply(status = UNPROCESSABLE_ENTITY, responseBody)

      "return a NOT_FOUND Error response" when {
        "able to validate a HIP BusinessError body with a '016' failure code and text" in {
          val noDataFailureResponseBody = """{"errors":{"processingDate":"2025-03-03", "code":"016", "text":"Invalid ID Number"}}"""
          val notFoundHttpResponse = errorResponse(noDataFailureResponseBody)

          val result = financialTransactionsParserReads(notFoundHttpResponse)
          result shouldBe Left(Error(NOT_FOUND, "ID number did not match any penalty data"))
        }
        "able to validate a HIP BusinessError body with a '018' failure code and text" in {
          val noDataFailureResponseBody = """{"errors":{"processingDate":"2025-03-03", "code":"018", "text":"No Data Identified"}}"""
          val notFoundHttpResponse = errorResponse(noDataFailureResponseBody)

          val result = financialTransactionsParserReads(notFoundHttpResponse)
          result shouldBe Left(Error(NOT_FOUND, "ID number did not match any financial data"))
        }
      }
      "return an UNPROCESSABLE_ENTITY Error response" when {
        "HIP BusinessError body does not have correct '016' failure code" in {
          val bodyWithInvalidCode = """{"errors":{"processingDate":"2025-03-03", "code":"16", "text":"Invalid ID Number"}}"""
          val notFoundNoBodyHttpResponse = errorResponse(bodyWithInvalidCode)

          val result = financialTransactionsParserReads(notFoundNoBodyHttpResponse)
          result shouldBe Left(Error(UNPROCESSABLE_ENTITY, "16 - Invalid ID Number"))
        }
      }

      "HIP BusinessError body does not have correct '016' failure text" in {
        val bodyWithInvalidText = """{"errors":{"processingDate":"2025-03-03", "code":"016", "text":"Invalid id num."}}"""
        val notFoundNoBodyHttpResponse = errorResponse(bodyWithInvalidText)

        val result = financialTransactionsParserReads(notFoundNoBodyHttpResponse)

        result shouldBe Left(Error(UNPROCESSABLE_ENTITY, "016 - Invalid id num."))
      }
    }
    "response body cannot be validated as a BusinessError" in {
      val invalidBody = """{"notGood":"isWrong"}"""
      val notFoundHttpResponse = HttpResponse.apply(status = UNPROCESSABLE_ENTITY, body = invalidBody)

      val result = financialTransactionsParserReads(notFoundHttpResponse)
      result shouldBe Left(Error(UNPROCESSABLE_ENTITY, invalidBody))
    }
  }

  "will return an INTERNAL_SERVER_ERROR response" when {
    "parsing an error with a TechnicalError response body" in {
      val technicalError = """{"response":{"error":{"code":"errorCode","message":"errorMessage","logId":"errorLogId"}}}"""
      val technicalErrorResponse = HttpResponse(status = INTERNAL_SERVER_ERROR, body = technicalError)

        val result = financialTransactionsParserReads(technicalErrorResponse)
        result shouldBe Left(Error(INTERNAL_SERVER_ERROR, "errorCode - errorMessage"))
      }
    }

    "parsing an error with an array of HipWrappedError response body" in {
      val hipWrappedError =
        """{"response":{"failures":[
          |{"type": "errorType", "reason": "errorReason"},
          |{"type": "errorType2", "reason": "errorReason2"}
          |]}}""".stripMargin
      val technicalErrorResponse = HttpResponse(status = BAD_REQUEST, body = hipWrappedError)

        val result = financialTransactionsParserReads(technicalErrorResponse)
        result shouldBe Left(Error(BAD_REQUEST, "errorType - errorReason,\nerrorType2 - errorReason2"))
    }

    "response body cannot be parsed as expected error format" in {
      val invalidBody = """{"notGood":"isWrong"}"""
      val notFoundHttpResponse = HttpResponse.apply(status = CONFLICT, body = invalidBody)

      val result = financialTransactionsParserReads(notFoundHttpResponse)
      result shouldBe Left(Error(CONFLICT, invalidBody))
    }

    "parsing an unknown error (e.g. IM A TEAPOT - 418) - and log a PagerDuty" in {
      val imATeapotHttpResponse = HttpResponse.apply(status = IM_A_TEAPOT, body = "I'm a teapot.")

      val result = financialTransactionsParserReads(imATeapotHttpResponse)
      result shouldBe Left(Error(IM_A_TEAPOT, "I'm a teapot."))
    }
}
