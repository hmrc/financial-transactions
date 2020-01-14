/*
 * Copyright 2020 HM Revenue & Customs
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

package testData

import models._
import utils.ImplicitDateFormatter._

object FinancialData {

  val successResponse: FinancialTransactions = FinancialTransactions(
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

  val singleErrorResponse: Error = Error("CODE","ERROR MESSAGE")

  val multiErrorModel: MultiError = MultiError(
    failures = Seq(
      Error("CODE 1","ERROR MESSAGE 1"),
      Error("CODE 2","ERROR MESSAGE 2")
    )
  )
}
