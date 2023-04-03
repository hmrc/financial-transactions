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
import play.api.test.Helpers.{await, defaultAwaitTimeout}

import scala.concurrent.Future

class DirectDebitDataConnectorSpec extends SpecBase with MockHttp {

  val badRequestSingleError: Either[ErrorResponse, Nothing] =
    Left(ErrorResponse(Status.BAD_REQUEST, Error(code = "CODE", reason = "ERROR MESSAGE")))

  val badRequestMultiError = Left(ErrorResponse(Status.BAD_REQUEST, MultiError(
    failures = Seq(
      Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
      Error(code = "ERROR CODE 2", reason = "ERROR MESSAGE 2")
    )
  )))

  val vatRegime: TaxRegime = VatRegime(id = "12345678")

  object TestDirectdebitDataConnector extends DirectDebitDataConnector(mockHttpGet)

  "The DirectDebitDataConnector" should {

    "format the request Url correctly for check direct debit requests" in {

      val vrn:String = "555555555"
      val actualUrl: String = TestDirectdebitDataConnector.directDebitUrl(vrn = vrn)
      val expectedUrl: String = s"${mockAppConfig.desUrl}/cross-regime/direct-debits/vatc/vrn/$vrn"

      actualUrl shouldBe expectedUrl
    }

    "when calling the checkDirectDebitExists" when {

      val successResponse: Either[Nothing, Boolean] = Right(true)
      val vrn: String = "5555555555"
      setupMockHttpGet(TestDirectdebitDataConnector.directDebitUrl(vrn = vrn))(successResponse)

      "calling check direct debit exists with success response received" should {

        "return a that a direct debit was found" in {
          setupMockHttpGet(TestDirectdebitDataConnector.directDebitUrl(vrn = vrn))(successResponse)
          val result: Future[HttpGetResult[DirectDebits]] =
            TestDirectdebitDataConnector.checkDirectDebitExists(vrn = vrn)
          await(result) shouldBe successResponse
        }
      }

      "calling the checkDirectDebitExist, single error" should {

        "return a Error model" in {
          setupMockHttpGet(TestDirectdebitDataConnector.directDebitUrl(vrn = vrn))(badRequestSingleError)
          val result: Future[HttpGetResult[DirectDebits]] =
            TestDirectdebitDataConnector.checkDirectDebitExists(vrn = vrn)
          await(result) shouldBe badRequestSingleError
        }
      }

      "calling the checkDirectDebitExist, multi error" should {

        "return a MultiError model" in {
          setupMockHttpGet(TestDirectdebitDataConnector.directDebitUrl(vrn = vrn))(badRequestMultiError)
          val result: Future[HttpGetResult[DirectDebits]] =
            TestDirectdebitDataConnector.checkDirectDebitExists(vrn = vrn)
          await(result) shouldBe badRequestMultiError
        }
      }
    }
  }
}