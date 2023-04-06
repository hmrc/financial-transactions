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

package connectors.API1396

import base.SpecBase
import connectors.API1396.httpParsers.DirectDebitCheckHttpParser.HttpGetResult
import mocks.MockHttp
import models.API1396._
import models._
import play.api.http.Status
import play.api.http.Status.BAD_GATEWAY
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.RequestTimeoutException
import utils.API1396.TestConstants.singleDirectDebits

import scala.concurrent.Future

class DirectDebitDataConnectorSpec extends SpecBase with MockHttp {

  val vrn: String = "5555555555"

  val connector = new DirectDebitDataConnector(mockHttpGet)

  "The DirectDebitDataConnector" should {

    "format the request Url correctly for check direct debit requests" in {

      val actualUrl: String = connector.directDebitUrl(vrn = vrn)
      val expectedUrl: String = s"${mockAppConfig.desUrl}/cross-regime/direct-debits/vatc/vrn/$vrn"

      actualUrl shouldBe expectedUrl
    }

    "return a success response" when {

      "a direct debit was found" in {
        val successResponse: HttpGetResult[DirectDebits] = Right(singleDirectDebits)

        setupMockHttpGet(connector.directDebitUrl(vrn = vrn))(Future.successful(successResponse))
        val result: Future[HttpGetResult[DirectDebits]] =
          connector.checkDirectDebitExists(vrn = vrn)
        await(result) shouldBe successResponse
      }
    }

    "return an Error model" when {

      "there is a bad request, single error" in {
        val badRequestSingleError: HttpGetResult[DirectDebits] =
          Left(ErrorResponse(Status.BAD_REQUEST, Error(code = "CODE", reason = "ERROR MESSAGE")))

        setupMockHttpGet(connector.directDebitUrl(vrn = vrn))(Future.successful(badRequestSingleError))
        val result: Future[HttpGetResult[DirectDebits]] =
          connector.checkDirectDebitExists(vrn = vrn)
        await(result) shouldBe badRequestSingleError
      }

      "there is a bad request, multi error" in {
        val badRequestMultiError: HttpGetResult[DirectDebits] =
          Left(ErrorResponse(Status.BAD_REQUEST, MultiError(
            failures = Seq(
              Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
              Error(code = "ERROR CODE 2", reason = "ERROR MESSAGE 2")
            )
          )))

        setupMockHttpGet(connector.directDebitUrl(vrn = vrn))(Future.successful(badRequestMultiError))
        val result: Future[HttpGetResult[DirectDebits]] =
          connector.checkDirectDebitExists(vrn = vrn)
        await(result) shouldBe badRequestMultiError
      }

      "there is a HTTP exception" in {
        val exception = new RequestTimeoutException("Request timed out!!!")

        setupMockHttpGet(connector.directDebitUrl(vrn = vrn))(Future.failed(exception))
        val result: Future[HttpGetResult[DirectDebits]] =
          connector.checkDirectDebitExists(vrn = vrn)
        await(result) shouldBe Left(ErrorResponse(BAD_GATEWAY, Error("BAD_GATEWAY", exception.message)))
      }
    }
  }
}