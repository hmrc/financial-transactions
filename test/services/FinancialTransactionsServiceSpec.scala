/*
 * Copyright 2021 HM Revenue & Customs
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

package services

import audit.models.{DirectDebitCheckRequestAuditModel, DirectDebitsCheckResponseAuditModel,
  FinancialTransactionsRequestAuditModel, FinancialTransactionsResponseAuditModel}
import base.SpecBase
import mocks.audit.MockAuditingService
import mocks.connectors.MockFinancialDataConnector
import models._
import play.api.http.Status
import utils.ImplicitDateFormatter._
import utils.TestConstants.{fullFinancialTransactions,multipleDirectDebits}

class FinancialTransactionsServiceSpec extends SpecBase with MockFinancialDataConnector with MockAuditingService {

  object TestFinancialTransactionService extends FinancialTransactionsService(mockFinancialDataConnector, mockAuditingService)

  lazy val regime: TaxRegime = VatRegime("123456")

  "The FinancialTransactionService.getFinancialData method" should {

    "Return FinancialTransactions when a success response is returned from the Connector" in {

      val successResponse: Either[Nothing, FinancialTransactions] = Right(fullFinancialTransactions)
      val queryParams: FinancialDataQueryParameters = FinancialDataQueryParameters(
        fromDate = Some("2017-04-06"),
        toDate = Some("2018-04-05"),
        onlyOpenItems = Some(false),
        includeLocks = Some(true),
        calculateAccruedInterest = Some(false),
        customerPaymentInformation = Some(false)
      )

      setupMockGetFinancialData(regime, queryParams)(successResponse)
      setupMockAuditEventResponse(FinancialTransactionsResponseAuditModel(regime, fullFinancialTransactions))
      setupMockAuditEventResponse(FinancialTransactionsRequestAuditModel(regime, queryParams))

      val actual: Either[ErrorResponse, FinancialTransactions] = await(TestFinancialTransactionService.getFinancialTransactions(
        regime,
        FinancialDataQueryParameters(
          fromDate = Some("2017-04-06"),
          toDate = Some("2018-04-05"),
          onlyOpenItems = Some(false),
          includeLocks = Some(true),
          calculateAccruedInterest = Some(false),
          customerPaymentInformation = Some(false)
        )
      ))

      actual shouldBe successResponse

      verifyAuditEvent(FinancialTransactionsRequestAuditModel(regime, queryParams))
      verifyAuditEvent(FinancialTransactionsResponseAuditModel(regime, fullFinancialTransactions))

    }

    "Return Error when a single error is returned from the Connector" in {

      val singleErrorResponse: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, Error("CODE", "REASON")))

      setupMockGetFinancialData(regime, FinancialDataQueryParameters(
        fromDate = Some("2017-04-06"),
        toDate = Some("2018-04-05"),
        onlyOpenItems = Some(false),
        includeLocks = Some(true),
        calculateAccruedInterest = Some(false),
        customerPaymentInformation = Some(false)
      ))(singleErrorResponse)

      val actual: Either[ErrorResponse, FinancialTransactions] = await(TestFinancialTransactionService.getFinancialTransactions(
        regime,
        FinancialDataQueryParameters(
          fromDate = Some("2017-04-06"),
          toDate = Some("2018-04-05"),
          onlyOpenItems = Some(false),
          includeLocks = Some(true),
          calculateAccruedInterest = Some(false),
          customerPaymentInformation = Some(false)
        )
      ))

      actual shouldBe singleErrorResponse

    }

    "Return a MultiError when multiple error responses are returned from the Connector" in {

      val multiErrorResponse: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, MultiError(Seq(
        Error("CODE 1", "REASON 1"),
        Error("CODE 2", "REASON 2")
      ))))

      setupMockGetFinancialData(regime, FinancialDataQueryParameters(
        fromDate = Some("2017-04-06"),
        toDate = Some("2018-04-05"),
        onlyOpenItems = Some(false),
        includeLocks = Some(true),
        calculateAccruedInterest = Some(false),
        customerPaymentInformation = Some(false)
      ))(multiErrorResponse)

      val actual: Either[ErrorResponse, FinancialTransactions] = await(TestFinancialTransactionService.getFinancialTransactions(
        regime,
        FinancialDataQueryParameters(
          fromDate = Some("2017-04-06"),
          toDate = Some("2018-04-05"),
          onlyOpenItems = Some(false),
          includeLocks = Some(true),
          calculateAccruedInterest = Some(false),
          customerPaymentInformation = Some(false)
        )
      ))

      actual shouldBe multiErrorResponse

    }
  }

  "The FinancialTransactionService.checkDirectDebitExists method" should {

    val vrn = "123456"

    "CheckDirectDebitExists Returns direct debit details when a success response is returned from the Connector" in {

      val successResponse: Either[Nothing, DirectDebits] = Right(multipleDirectDebits)
      setupMockCheckDirectDebitExists(vrn)(successResponse)

      setupMockAuditEventResponse(DirectDebitsCheckResponseAuditModel(vrn, hasDirectDebit = true))
      setupMockAuditEventResponse(DirectDebitCheckRequestAuditModel(vrn))

      val actual: Either[ErrorResponse, DirectDebits] = await(TestFinancialTransactionService.checkDirectDebitExists(vrn))

      actual shouldBe successResponse

      verifyAuditEvent(DirectDebitCheckRequestAuditModel(vrn))
      verifyAuditEvent(DirectDebitsCheckResponseAuditModel(vrn, hasDirectDebit = true))

    }

    "CheckDirectDebitExists Return Error when a single error is returned from the Connector" in {

      val singleErrorResponse: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, Error("CODE", "REASON")))

      setupMockCheckDirectDebitExists(vrn)(singleErrorResponse)

      val actual: Either[ErrorResponse, DirectDebits] = await(TestFinancialTransactionService.checkDirectDebitExists(vrn))

      actual shouldBe singleErrorResponse

    }

    "CheckDirectDebitExists Return a MultiError when multiple error responses are returned from the Connector" in {

      val multiErrorResponse: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, MultiError(Seq(
        Error("CODE 1", "REASON 1"),
        Error("CODE 2", "REASON 2")
      ))))

      setupMockCheckDirectDebitExists(vrn)(multiErrorResponse)

      val actual: Either[ErrorResponse, DirectDebits] = await(TestFinancialTransactionService.checkDirectDebitExists(vrn))

      actual shouldBe multiErrorResponse

    }
  }
}
