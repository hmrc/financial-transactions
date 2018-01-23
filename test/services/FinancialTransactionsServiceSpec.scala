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

package services

import base.SpecBase
import mocks.connectors.MockFinancialDataConnector
import models._
import utils.ImplicitDateFormatter._

class FinancialTransactionsServiceSpec extends SpecBase with MockFinancialDataConnector {


  object TestFinancialTransactionService extends FinancialTransactionsService(mockFinancialDataConnector)

  lazy val regime = VatRegime("123456")

  "The FinancialTransactionService.getFinancialData method" should {

    "Return FiniancialTransactions when a DesError is returned from the Connector" in {

      val successResponse = Right(FinancialTransactions(
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
      ))

      setupMockGetFinancialData(regime, FinancialDataQueryParameters(
        fromDate = Some("2017-04-06"),
        toDate = Some("2018-04-05"),
        onlyOpenItems = Some(false),
        includeLocks = Some(true),
        calculateAccruedInterest = Some(false),
        customerPaymentInformation = Some(false)
      ))(successResponse)

      val actual = await(TestFinancialTransactionService.getFinancialTransactions(
        regime,
        fromDate = Some("2017-04-06"),
        toDate = Some("2018-04-05"),
        onlyOpenItems = Some(false),
        includeLocks = Some(true),
        calculateAccruedInterest = Some(false),
        customerPaymentInformation = Some(false)
      ))

      actual shouldBe successResponse

    }

    "Return an error when a DesError is returned from the Connector" in {

      val singleErrorResponse = Left(DesError("CODE","REASON"))

      setupMockGetFinancialData(regime, FinancialDataQueryParameters(
        fromDate = Some("2017-04-06"),
        toDate = Some("2018-04-05"),
        onlyOpenItems = Some(false),
        includeLocks = Some(true),
        calculateAccruedInterest = Some(false),
        customerPaymentInformation = Some(false)
      ))(singleErrorResponse)

      val actual = await(TestFinancialTransactionService.getFinancialTransactions(
        regime,
        fromDate = Some("2017-04-06"),
        toDate = Some("2018-04-05"),
        onlyOpenItems = Some(false),
        includeLocks = Some(true),
        calculateAccruedInterest = Some(false),
        customerPaymentInformation = Some(false)
      ))

      actual shouldBe singleErrorResponse

    }

    "Return a DesMultiError when a multiple error response is returned from the Connector" in {

      val multiErrorResponse = Left(DesMultiError(Seq(
        DesError("CODE 1","REASON 1"),
        DesError("CODE 2","REASON 2")
      )))

      setupMockGetFinancialData(regime, FinancialDataQueryParameters(
        fromDate = Some("2017-04-06"),
        toDate = Some("2018-04-05"),
        onlyOpenItems = Some(false),
        includeLocks = Some(true),
        calculateAccruedInterest = Some(false),
        customerPaymentInformation = Some(false)
      ))(multiErrorResponse)

      val actual = await(TestFinancialTransactionService.getFinancialTransactions(
        regime,
        fromDate = Some("2017-04-06"),
        toDate = Some("2018-04-05"),
        onlyOpenItems = Some(false),
        includeLocks = Some(true),
        calculateAccruedInterest = Some(false),
        customerPaymentInformation = Some(false)
      ))

      actual shouldBe multiErrorResponse

    }
  }
}
