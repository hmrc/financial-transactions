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

package controllers

import utils.TestConstants.{fullFinancialTransactions1166 => fullFinancialTransactions, multipleDirectDebits}
import base.SpecBase
import controllers.actions.AuthActionImpl
import mocks.auth.MockMicroserviceAuthorisedFunctions
import mocks.services.MockFinancialTransactionsService
import models.API1166.FinancialDataQueryParameters
import models.API1166._
import models._
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}

class FinancialTransactionsControllerSpec extends SpecBase with MockFinancialTransactionsService with
  MockMicroserviceAuthorisedFunctions {

  val singleError: Error = Error(code = "CODE", reason = "ERROR MESSAGE")
  val multiError: MultiError = MultiError(
    failures = Seq(
      Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
      Error(code = "ERROR CODE 2", reason = "ERROR MESSAGE 2")
    )
  )

  val authActionImpl = new AuthActionImpl(mockAuth, controllerComponents)

  object TestFinancialTransactionController extends FinancialTransactionsController(
    authActionImpl, mockFinancialTransactionsService, controllerComponents
  )

  "The GET FinancialTransactionsController.financialTransactions method" when {

    val successResponse = Right(fullFinancialTransactions)
    val badRequestSingleError = Left(ErrorResponse(Status.BAD_REQUEST, singleError))
    val badRequestMultiError = Left(ErrorResponse(Status.BAD_REQUEST, multiError))

    "called by an authenticated user" which {

      "is requesting VAT details" should {

        val regimeType = "VAT"
        val vrn = "123456"
        val vatRegime = VatRegime(vrn)

        "for a successful response from the FinancialTransactionsService" should {

          lazy val result = TestFinancialTransactionController.getFinancialTransactions(
            regimeType, vrn, FinancialDataQueryParameters()
          )(fakeRequest)

          "return a status of 200 (OK)" in {
            setupMockGetFinancialTransactions(vatRegime, FinancialDataQueryParameters())(successResponse)
            status(result) shouldBe Status.OK
          }

          "return a json body with the financial transaction information" in {
            contentAsJson(result) shouldBe Json.toJson(fullFinancialTransactions)
          }

        }

        "for a bad request with single error from the FinancialTransactionsService" should {

          lazy val result = TestFinancialTransactionController.getFinancialTransactions(
            regimeType, vrn, FinancialDataQueryParameters()
          )(fakeRequest)

          "return a status of 400 (BAD_REQUEST)" in {
            setupMockGetFinancialTransactions(vatRegime, FinancialDataQueryParameters())(badRequestSingleError)
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the single error message" in {

            contentAsJson(result) shouldBe Json.toJson(singleError)
          }
        }

        "for a bad request with multiple errors from the FinancialTransactionsService" should {

          lazy val result = TestFinancialTransactionController.getFinancialTransactions(
            regimeType, vrn, FinancialDataQueryParameters()
          )(fakeRequest)

          "return a status of 400 (BAD_REQUEST)" in {
            setupMockGetFinancialTransactions(vatRegime, FinancialDataQueryParameters())(badRequestMultiError)
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the multiple error messages" in {
            contentAsJson(result) shouldBe Json.toJson(multiError)
          }
        }

      }

      "is requesting IT details" should {

        val regimeType = "IT"
        val mtditid = "XAIT0000123456"
        val incomeTaxRegime = IncomeTaxRegime(mtditid)

        "for a successful response from the FinancialTransactionsService" should {

          lazy val result = TestFinancialTransactionController.getFinancialTransactions(
            regimeType, mtditid, FinancialDataQueryParameters()
          )(fakeRequest)

          "return a status of 200 (OK)" in {
            setupMockGetFinancialTransactions(incomeTaxRegime, FinancialDataQueryParameters())(successResponse)
            status(result) shouldBe Status.OK
          }

          "return a json body with the financial transaction information" in {
            contentAsJson(result) shouldBe Json.toJson(fullFinancialTransactions)
          }

        }

        "for a bad request with single error from the FinancialTransactionsService" should {

          lazy val result = TestFinancialTransactionController.getFinancialTransactions(
            regimeType, mtditid, FinancialDataQueryParameters()
          )(fakeRequest)

          "return a status of 400 (BAD_REQUEST)" in {
            setupMockGetFinancialTransactions(incomeTaxRegime, FinancialDataQueryParameters())(badRequestSingleError)
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the single error message" in {

            contentAsJson(result) shouldBe Json.toJson(singleError)
          }
        }

        "for a bad request with multiple errors from the FinancialTransactionsService" should {

          lazy val result = TestFinancialTransactionController.getFinancialTransactions(
            regimeType, mtditid, FinancialDataQueryParameters()
          )(fakeRequest)

          "return a status of 400 (BAD_REQUEST)" in {
            setupMockGetFinancialTransactions(incomeTaxRegime, FinancialDataQueryParameters())(badRequestMultiError)
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the multiple error messages" in {

            contentAsJson(result) shouldBe Json.toJson(multiError)
          }
        }

      }

      "is requesting details for an Invalid Tax Regime" should {

        val regimeType = "BANANA"
        val id = "123456"

        lazy val result = TestFinancialTransactionController.getFinancialTransactions(
          regimeType, id, FinancialDataQueryParameters()
        )(fakeRequest)

        "return a status of 400 (BAD_REQUEST)" in {
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return a json body with an Invalid Tax Regime message" in {
          contentAsJson(result) shouldBe Json.toJson(InvalidTaxRegime)
        }
      }
    }

    "called by an unauthenticated user" should {

      val regimeType = "VAT"
      val id = "123456"

      "Return an UNAUTHORISED response" which {

        lazy val result = TestFinancialTransactionController.getFinancialTransactions(
          regimeType, id, FinancialDataQueryParameters()
        )(fakeRequest)

        "has status UNAUTHORISED (401)" in {
          setupMockAuthorisationException()
          status(result) shouldBe Status.UNAUTHORIZED
        }
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
          regimeType, id, FinancialDataQueryParameters()
        )(fakeRequest)

        "has status UNAUTHORISED (401)" in {
          setupMockAuthorisationException()
          status(result) shouldBe Status.UNAUTHORIZED
        }
      }
    }
  }
}
