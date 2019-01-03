/*
 * Copyright 2019 HM Revenue & Customs
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

class FinancialTransactionsServiceSpec extends SpecBase with MockFinancialDataConnector with MockAuditingService {


  object TestFinancialTransactionService extends FinancialTransactionsService(mockFinancialDataConnector, mockAuditingService)

  lazy val regime: TaxRegime = VatRegime("123456")

  "The FinancialTransactionService.getFinancialData method" should {

    "Return FinancialTransactions when a success response is returned from the Connector" in {

      val financialTransactions: FinancialTransactions = FinancialTransactions(
        idType = Some("MTDBSA"),
        idNumber = Some("XQIT00000000001"),
        regimeType = Some("ITSA"),
        processingDate = "2017-03-07T22:55:56.987Z",
        financialTransactions = Some(Seq(Transaction(
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
          items = Some(Seq(SubItem(
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
          )))
        )))
      )
      val successResponse: Either[Nothing, FinancialTransactions] = Right(financialTransactions)
      val queryParams: FinancialDataQueryParameters = FinancialDataQueryParameters(
        fromDate = Some("2017-04-06"),
        toDate = Some("2018-04-05"),
        onlyOpenItems = Some(false),
        includeLocks = Some(true),
        calculateAccruedInterest = Some(false),
        customerPaymentInformation = Some(false)
      )

      setupMockGetFinancialData(regime, queryParams)(successResponse)
      setupMockAuditEventResponse(FinancialTransactionsResponseAuditModel(regime, financialTransactions))
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
      verifyAuditEvent(FinancialTransactionsResponseAuditModel(regime, financialTransactions))

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

    "CheckDirectDebitExists Return true when a success response is returned from the Connector" in {

      val successResponse: Either[Nothing, Boolean] = Right(true)
      setupMockCheckDirectDebitExists(vrn)(successResponse)

      setupMockAuditEventResponse(DirectDebitsCheckResponseAuditModel(vrn, hasDirectDebit = true))
      setupMockAuditEventResponse(DirectDebitCheckRequestAuditModel(vrn))

      val actual: Either[ErrorResponse, Boolean] = await(TestFinancialTransactionService.checkDirectDebitExists(vrn))

      actual shouldBe successResponse

      verifyAuditEvent(DirectDebitCheckRequestAuditModel(vrn))
      verifyAuditEvent(DirectDebitsCheckResponseAuditModel(vrn, hasDirectDebit = true))

    }

    "CheckDirectDebitExists Return Error when a single error is returned from the Connector" in {

      val singleErrorResponse: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, Error("CODE", "REASON")))

      setupMockCheckDirectDebitExists(vrn)(singleErrorResponse)

      val actual: Either[ErrorResponse, Boolean] = await(TestFinancialTransactionService.checkDirectDebitExists(vrn))

      actual shouldBe singleErrorResponse

    }

    "CheckDirectDebitExists Return a MultiError when multiple error responses are returned from the Connector" in {

      val multiErrorResponse: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, MultiError(Seq(
        Error("CODE 1", "REASON 1"),
        Error("CODE 2", "REASON 2")
      ))))

      setupMockCheckDirectDebitExists(vrn)(multiErrorResponse)

      val actual: Either[ErrorResponse, Boolean] = await(TestFinancialTransactionService.checkDirectDebitExists(vrn))

      actual shouldBe multiErrorResponse

    }
  }
}
