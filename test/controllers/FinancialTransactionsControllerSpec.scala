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

package controllers

import utils.API1396.TestConstants.multipleDirectDebits
import utils.API1811.TestConstants.{fullFinancialTransactionsOutputJson, fullFinancialTransactions => fullFinancialTransactions1811}
import base.SpecBase
import controllers.actions.AuthActionImpl
import mocks.auth.MockMicroserviceAuthorisedFunctions
import mocks.services.MockDirectDebitService
import mocks.services.Mock1811FinancialTransactionsService
import models.API1811.{Error => Error1811}
import models._
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}

import scala.concurrent.Future

class FinancialTransactionsControllerSpec extends SpecBase
  with MockDirectDebitService
  with Mock1811FinancialTransactionsService
  with MockMicroserviceAuthorisedFunctions {

  val singleError: Error = Error(code = "CODE", reason = "ERROR MESSAGE")
  val multiError: MultiError = MultiError(
    failures = Seq(
      Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
      Error(code = "ERROR CODE 2", reason = "ERROR MESSAGE 2")
    )
  )

  val authActionImpl = new AuthActionImpl(mockAuth, controllerComponents)

  object TestFinancialTransactionController extends FinancialTransactionsController(
    authActionImpl,
    mockDirectDebitService,
    mock1811FinancialTransactionsService,
    controllerComponents
  )

  "The GET FinancialTransactionsController.getFinancialTransactions method" when {

    val badRequestError = Error1811(Status.BAD_REQUEST, "error")
    val successResponse = Future.successful(Right(fullFinancialTransactions1811))
    val errorResponse = Future.successful(Left(badRequestError))

    "an authenticated user requests VAT details" when {

      val id = "123456"
      val regimeType = "VAT"
      val vatRegime = VatRegime(id)

      "the service returns a success response" should {

        lazy val result = {
          setupMock1811GetFinancialTransactions(vatRegime, FinancialRequestQueryParameters())(successResponse)
          TestFinancialTransactionController.getFinancialTransactions(
            regimeType, id, FinancialRequestQueryParameters()
          )(fakeRequest)
        }

        "return a status of 200 (OK)" in {
          status(result) shouldBe Status.OK
        }

        "return a json body with the financial transaction information" in {
          contentAsJson(result) shouldBe fullFinancialTransactionsOutputJson
        }
      }

      "the service returns a failure response" should {

        lazy val result = {
          setupMock1811GetFinancialTransactions(vatRegime, FinancialRequestQueryParameters())(errorResponse)
          TestFinancialTransactionController.getFinancialTransactions(
            regimeType, id, FinancialRequestQueryParameters()
          )(fakeRequest)
        }

        "return the same status as the response" in {
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return the correct error JSON" in {
          contentAsJson(result) shouldBe Json.toJson(badRequestError)
        }
      }
    }

    "an authenticated user requests details for an invalid tax regime" should {

      val regimeType = "BANANA"
      val id = "123456"
      val vatRegime = VatRegime(id)

      lazy val result = {
        setupMock1811GetFinancialTransactions(vatRegime, FinancialRequestQueryParameters())(errorResponse)
        TestFinancialTransactionController.getFinancialTransactions(
          regimeType, id, FinancialRequestQueryParameters()
        )(fakeRequest)
      }

      "return a status of 400 (BAD_REQUEST)" in {
        status(result) shouldBe Status.BAD_REQUEST
      }

      "return a json body with an Invalid Tax Regime message" in {
        contentAsJson(result) shouldBe Json.toJson(InvalidTaxRegime)
      }
    }
  }

  "The GET FinancialTransactionsController.checkDirectDebitExists method" when {

    val successResponse = Right(multipleDirectDebits)
    val badRequestSingleError = Left(ErrorResponse(Status.BAD_REQUEST, singleError))
    val badRequestMultiError = Left(ErrorResponse(Status.BAD_REQUEST, multiError))

    "called by an authenticated user" which {

      "is requesting check direct debit" should {
        val vrn = "123456"

        "for a successful response from the FinancialTransactionsService" should {

          lazy val result = TestFinancialTransactionController.checkDirectDebitExists(vrn)(fakeRequest)

          "return a status of 200 (OK)" in {
            setupMockCheckDirectDebitExists(vrn)(successResponse)
            status(result) shouldBe Status.OK
          }

          "return a json body with the financial transaction information" in {
            contentAsJson(result) shouldBe Json.toJson(multipleDirectDebits)
          }
        }

        "checkDirectDebitExists for a bad request with single error from the FinancialTransactionsService" should {

          lazy val result = TestFinancialTransactionController.checkDirectDebitExists(vrn)(fakeRequest)

          "return a status of 400 (BAD_REQUEST)" in {
            setupMockCheckDirectDebitExists(vrn)(badRequestSingleError)
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the single error message" in {

            contentAsJson(result) shouldBe Json.toJson(singleError)
          }
        }

        "checkDirectDebitExists for a bad request with multiple errors from the FinancialTransactionsService" should {

          lazy val result = TestFinancialTransactionController.checkDirectDebitExists(vrn)(fakeRequest)

          "return a status of 400 (BAD_REQUEST)" in {
            setupMockCheckDirectDebitExists(vrn)(badRequestMultiError)
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the multiple error messages" in {
            contentAsJson(result) shouldBe Json.toJson(multiError)
          }
        }
      }
    }

    "checkDirectDebitExists called by an unauthenticated user" should {

      val regimeType = "VAT"
      val id = "123456"

      "Return an UNAUTHORISED response" which {

        lazy val result = TestFinancialTransactionController.getFinancialTransactions(
          regimeType, id, FinancialRequestQueryParameters()
        )(fakeRequest)

        "has status UNAUTHORISED (401)" in {
          setupMockAuthorisationException()
          status(result) shouldBe Status.UNAUTHORIZED
        }
      }
    }
  }
}
