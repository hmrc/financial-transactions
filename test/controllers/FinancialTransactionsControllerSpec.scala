/*
 * Copyright 2017 HM Revenue & Customs
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

import base.SpecBase
import controllers.actions.AuthActionImpl
import mocks.auth.MockMicroserviceAuthorisedFunctions
import mocks.services.MockFinancialTransactionsService
import models._
import play.api.http.Status
import play.api.libs.json.Json
import utils.ImplicitDateFormatter._

class FinancialTransactionsControllerSpec extends SpecBase with MockFinancialTransactionsService with MockMicroserviceAuthorisedFunctions {

  val success = FinancialTransactions(
    idType = "MTDBSA",
    idNumber = "XQIT00000000001",
    regimeType = "ITSA",
    processingDate = "2017-03-07T22:55:56.987Z",
    financialTransactions = Seq(Transaction(
      chargeType = Some("PAYE"),
      mainType = Some("2100"),
      periodKey = Some("13RL"),
      periodKeyDescription = Some("abcde"),
      taxPeriodFrom = Some("2017-4-6"),
      taxPeriodTo = Some("2018-4-5"),
      businessPartner = Some("6622334455"),
      contractAccountCategory = Some("02"),
      contractAccount = Some("X"),
      contractObjectType = Some("ABCD"),
      contractObject = Some("00000003000000002757"),
      sapDocumentNumber = Some("1040000872"),
      sapDocumentNumberItem = Some("XM00"),
      chargeReference = Some("XM002610011594"),
      mainTransaction = Some("1234"),
      subTransaction = Some("5678"),
      originalAmount = Some(3400.0),
      outstandingAmount = Some(1400.0),
      clearedAmount = Some(2000.0),
      accruedInterest = Some(0.23),
      items = Seq(SubItem(
        subItem = Some("000"),
        dueDate = Some("2018-2-14"),
        amount = Some(3400.00),
        clearingDate = Some("2018-2-17"),
        clearingReason = Some("A"),
        outgoingPaymentMethod = Some("B"),
        paymentLock = Some("C"),
        clearingLock = Some("D"),
        interestLock = Some("E"),
        dunningLock = Some("1"),
        returnFlag = Some(false),
        paymentReference = Some("F"),
        paymentAmount = Some(2000.00),
        paymentMethod = Some("G"),
        paymentLot = Some("H"),
        paymentLotItem = Some("112"),
        clearingSAPDocument = Some("3350000253"),
        statisticalDocument = Some("I"),
        returnReason = Some("J"),
        promiseToPay = Some("K")
      ))
    ))
  )
  val singleError = Error(code = "CODE", reason = "ERROR MESSAGE")
  val multiError = MultiError(
    failures = Seq(
      Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
      Error(code = "ERROR CODE 2", reason = "ERROR MESSAGE 2")
    )
  )

  val successResponse = Right(success)
  val badRequestSingleError = Left(ErrorResponse(Status.BAD_REQUEST, singleError))
  val badRequestMultiError = Left(ErrorResponse(Status.BAD_REQUEST, multiError))

  "The GET FinancialTransactionsController.financialTransactions method" when {

    "called by an authenticated user" which {

      object TestFinancialTransactionController extends FinancialTransactionsController(new AuthActionImpl(mockAuth), mockFinancialTransactionsService)

      "is requesting VAT details" should {

        val regimeType = "VAT"
        val vrn = "123456"
        val vatRegime = VatRegime(vrn)

        "for a successful response from the FinancialTransactionsService" should {

          lazy val result = await(TestFinancialTransactionController.getFinancialTransactions(regimeType, vrn, FinancialDataQueryParameters())(fakeRequest))

          "return a status of 200 (OK)" in {
            setupMockGetFinancialTransactions(vatRegime, FinancialDataQueryParameters())(successResponse)
            status(result) shouldBe Status.OK
          }

          "return a json body with the financial transaction information" in {
            jsonBodyOf(result) shouldBe Json.toJson(success)
          }

        }

        "for a bad request with single error from the FinancialTransactionsService" should {

          lazy val result = await(TestFinancialTransactionController.getFinancialTransactions(regimeType, vrn, FinancialDataQueryParameters())(fakeRequest))

          "return a status of 400 (BAD_REQUEST)" in {
            setupMockGetFinancialTransactions(vatRegime, FinancialDataQueryParameters())(badRequestSingleError)
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the single error message" in {

            jsonBodyOf(result) shouldBe Json.toJson(singleError)
          }
        }

        "for a bad request with multiple errors from the FinancialTransactionsService" should {

          lazy val result = await(TestFinancialTransactionController.getFinancialTransactions(regimeType, vrn, FinancialDataQueryParameters())(fakeRequest))

          "return a status of 400 (BAD_REQUEST)" in {
            setupMockGetFinancialTransactions(vatRegime, FinancialDataQueryParameters())(badRequestMultiError)
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the multiple error messages" in {
            jsonBodyOf(result) shouldBe Json.toJson(multiError)
          }
        }

      }

      "is requesting IT details" should {

        val regimeType = "IT"
        val mtditid = "XAIT0000123456"
        val incomeTaxRegime = IncomeTaxRegime(mtditid)

        "for a successful response from the FinancialTransactionsService" should {

          lazy val result = await(TestFinancialTransactionController.getFinancialTransactions(regimeType, mtditid, FinancialDataQueryParameters())(fakeRequest))

          "return a status of 200 (OK)" in {
            setupMockGetFinancialTransactions(incomeTaxRegime, FinancialDataQueryParameters())(successResponse)
            status(result) shouldBe Status.OK
          }

          "return a json body with the financial transaction information" in {
            jsonBodyOf(result) shouldBe Json.toJson(success)
          }

        }

        "for a bad request with single error from the FinancialTransactionsService" should {

          lazy val result = await(TestFinancialTransactionController.getFinancialTransactions(regimeType, mtditid, FinancialDataQueryParameters())(fakeRequest))

          "return a status of 400 (BAD_REQUEST)" in {
            setupMockGetFinancialTransactions(incomeTaxRegime, FinancialDataQueryParameters())(badRequestSingleError)
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the single error message" in {

            jsonBodyOf(result) shouldBe Json.toJson(singleError)
          }
        }

        "for a bad request with multiple errors from the FinancialTransactionsService" should {

          lazy val result = await(TestFinancialTransactionController.getFinancialTransactions(regimeType, mtditid, FinancialDataQueryParameters())(fakeRequest))

          "return a status of 400 (BAD_REQUEST)" in {
            setupMockGetFinancialTransactions(incomeTaxRegime, FinancialDataQueryParameters())(badRequestMultiError)
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the multiple error messages" in {

            jsonBodyOf(result) shouldBe Json.toJson(multiError)
          }
        }

      }

      "is requesting details for an Invalid Tax Regime" should {

        val regimeType = "BANANA"
        val id = "123456"

        "for a successful response from the FinancialTransactionsService" should {

          lazy val result = await(TestFinancialTransactionController.getFinancialTransactions(regimeType, id, FinancialDataQueryParameters())(fakeRequest))

          "return a status of 400 (BAD_REQUEST)" in {
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with an Invalid Tax Regime message" in {
            jsonBodyOf(result) shouldBe Json.obj(
              "code" -> Status.BAD_REQUEST.toString,
              "reason" -> "The supplied Tax Regime is invalid."
            )
          }
        }
      }
    }

    "called by an unauthenticated user" should {

      val regimeType = "VAT"
      val id = "123456"
      object TestFinancialTransactionController extends FinancialTransactionsController(new AuthActionImpl(mockAuth), mockFinancialTransactionsService)

      "Return an UNAUTHORISED response" which {

        lazy val result = await(TestFinancialTransactionController.getFinancialTransactions(regimeType, id, FinancialDataQueryParameters())(fakeRequest))

        "has status UNAUTHORISED (401)" in {
          setupMockAuthorisationException()
          status(result) shouldBe Status.UNAUTHORIZED
        }
      }
    }
  }
}
