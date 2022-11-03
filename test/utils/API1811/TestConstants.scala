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

package utils.API1811

import models.API1811.{DocumentDetails, DocumentDetailsNew, FinancialTransactions, LineItemDetails, SubItem, Transaction}
import play.api.libs.json.{JsObject, Json}
import utils.ImplicitDateFormatter._

object TestConstants {

  val fullSubItem: SubItem = SubItem(
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
    DDcollectionInProgress = Some(true),
    returnReason = Some("J"),
    promiseToPay = Some("K")
  )

  val fullSubItemJsonEIS: JsObject = Json.obj(
    "subItem" -> "000",
    "dueDate" -> "2018-02-14",
    "amount" -> 3400.0,
    "clearingDate" -> "2018-02-17",
    "clearingReason" -> "A",
    "outgoingPaymentMethod" -> "B",
    "paymentLock" -> "C",
    "clearingLock" -> "D",
    "interestLock" -> "E",
    "dunningLock" -> "1",
    "returnFlag" -> false,
    "paymentReference" -> "F",
    "paymentAmount" -> 2000.0,
    "paymentMethod" -> "G",
    "paymentLot" -> "H",
    "paymentLotItem" -> "112",
    "clearingSAPDocument" -> "3350000253",
    "statisticalDocument" -> "I",
    "DDCollectionInProgress" -> true,
    "returnReason" -> "J",
    "promisetoPay" -> "K"
  )

  val fullSubItemJsonEISOutput: JsObject = Json.obj(
    "subItem" -> "000",
    "dueDate" -> "2018-02-14",
    "amount" -> 3400.0,
    "clearingDate" -> "2018-02-17",
    "clearingReason" -> "A",
    "outgoingPaymentMethod" -> "B",
    "paymentLock" -> "C",
    "clearingLock" -> "D",
    "interestLock" -> "E",
    "dunningLock" -> "1",
    "returnFlag" -> false,
    "paymentReference" -> "F",
    "paymentAmount" -> 2000.0,
    "paymentMethod" -> "G",
    "paymentLot" -> "H",
    "paymentLotItem" -> "112",
    "clearingSAPDocument" -> "3350000253",
    "statisticalDocument" -> "I",
    "DDcollectionInProgress" -> true,
    "returnReason" -> "J",
    "promiseToPay" -> "K"
  )

  val fullTransaction: Transaction = Transaction(
    documentId = "012345678901234567890123456789",
    chargeType = Some("VAT Return Debit Charge"),
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
    mainTransaction = Some("4700"),
    subTransaction = Some("1174"),
    originalAmount = Some(3400),
    outstandingAmount = Some(1400),
    clearedAmount = Some(2000),
    accruedInterest = Some(0.23),
    items = Seq(fullSubItem)
  )

  val fullTransactionJsonEIS: JsObject = Json.obj(
    "documentId" -> "012345678901234567890123456789",
    "chargeType" -> "VAT Return Debit Charge",
    "mainType" -> "2100",
    "periodKey" -> "13RL",
    "periodKeyDescription" -> "abcde",
    "taxPeriodFrom" -> "2017-04-06",
    "taxPeriodTo" -> "2018-04-05",
    "businessPartner" -> "6622334455",
    "contractAccountCategory" -> "02",
    "contractAccount" -> "X",
    "contractObjectType" -> "ABCD",
    "contractObject" -> "00000003000000002757",
    "sapDocumentNumber" -> "1040000872",
    "sapDocumentNumberItem" -> "XM00",
    "chargeReference" -> "XM002610011594",
    "mainTransaction" -> "4700",
    "subTransaction" -> "1174",
    "originalAmount" -> 3400,
    "outstandingAmount" -> 1400,
    "clearedAmount" -> 2000,
    "accruedInterest" -> 0.23,
    "items" -> Json.arr(fullSubItemJsonEIS)
  )

  val fullTransactionJsonEISOutput: JsObject = Json.obj(
    "chargeType" -> "VAT Return Debit Charge"
  )

  val fullDocumentDetails: DocumentDetails = DocumentDetails(
    taxYear = "2017",
    documentId = "1455",
    documentDate = "2018-03-29",
    documentText = "VAT-VC",
    documentDueDate = "2020-04-15",
    totalAmount = 45552768.79,
    documentOutstandingAmount = 297873.46,
    statisticalFlag = false,
    accruingPenaltyLPP1 = Some("1000.34"),
    accruingPenaltyLPP2 = Some("accrlpp2")
  )

  val fullDocumentDetailsJson: JsObject = Json.obj(
    "taxYear" -> "2017",
    "documentId" -> "1455",
    "documentDate" -> "2018-03-29",
    "documentText" -> "VAT-VC",
    "documentDueDate" -> "2020-04-15",
    "totalAmount" -> 45552768.79,
    "documentOutstandingAmount" -> 297873.46,
    "statisticalFlag" -> false,
    "accruingPenaltyLPP1" -> "1000.34",
    "accruingPenaltyLPP2" -> "accrlpp2",
  )

  val fullFinancialTransactionsJsonEIS: JsObject = Json.obj(
    "documentDetails" -> Json.arr(fullDocumentDetailsJson),
    "financialDetails" -> Json.arr(fullTransactionJsonEIS)
  )

  val fullFinancialTransactionsJsonOutput: JsObject = Json.obj(
    "documentDetails" -> Json.arr(fullDocumentDetailsJson),
    "financialTransactions" -> Json.arr(fullTransactionJsonEISOutput)
  )

  val fullFinancialTransactions: FinancialTransactions = FinancialTransactions(
    documentDetails = Seq(fullDocumentDetails),
    financialDetails = Seq(fullTransaction)
  )

  val lineItemDetailsFull: LineItemDetails = LineItemDetails(
    mainTransaction = Some("4700"),
    subTransaction = Some("1174"),
    periodKey = Some("13RL"),
    periodFromDate = Some("2017-4-6"),
    periodToDate = Some("2018-4-5"),
    netDueDate = Some("2018-2-14"),
    amount = Some(3400),
    ddCollectionInProgress = Some(true),
    interestRate = Some(3.00)
  )

  val lineItemDetailsFullJson: JsObject = Json.obj(
    "mainTransaction" -> "4700",
    "subTransaction" -> "1174",
    "periodKey" -> "13RL",
    "periodFromDate" -> "2017-04-06",
    "periodToDate" -> "2018-04-05",
    "netDueDate" -> "2018-02-14",
    "amount" -> 3400,
    "ddCollectionInProgress" -> true,
    "lineItemInterestDetails" -> Json.obj(
      "currentInterestRate" -> 3.00
    )
  )

  val lineItemDetailsFullIncorrectFields: LineItemDetails = lineItemDetailsFull.copy(
    mainTransaction = None,
    subTransaction = None
  )

  val lineItemDetailsFullIncorrectFieldsJson: JsObject = Json.obj(
    "mainTraction" -> "4700",
    "subTraction" -> "1174",
    "periodKey" -> "13RL",
    "periodFromDate" -> "2017-04-06",
    "periodToDate" -> "2018-04-05",
    "netDueDate" -> "2018-02-14",
    "amount" -> 3400,
    "ddCollectionInProgress" -> true,
    "lineItemInterestDetails" -> Json.obj(
      "currentInterestRate" -> 3.00
    )
  )

  val fullDocumentDetailsNew: DocumentDetailsNew = DocumentDetailsNew(
    chargeReferenceNumber = Some("XM002610011594"),
    documentTotalAmount = Some(45552768.79),
    documentOutstandingAmount = Some(297873.46),
    lineItemDetails = Some(Seq(lineItemDetailsFull)),
    interestAccruingAmount = Some(0.23),
    penaltyType = Some("LPP1"),
    penaltyStatus = Some("POSTED"),
    penaltyAmount = Some(10.01)
  )

  val fullDocumentDetailsNewJson: JsObject = Json.obj(
    "chargeReferenceNumber" -> "XM002610011594",
    "documentTotalAmount" -> 45552768.79,
    "documentOutstandingAmount" -> 297873.46,
    "lineItemDetails" -> Json.arr(lineItemDetailsFullJson),
    "documentInterestTotals" -> Json.obj(
      "interestAccruingAmount" -> 0.23
    ),
    "documentPenaltyTotals" -> Json.obj(
      "penaltyType" -> "LPP1",
      "penaltyStatus" -> "POSTED",
      "penaltyAmount" -> 10.01
    )
  )

  val documentDetailsNewIncorrectFieldsJson: JsObject = Json.obj(
    "chargeRefNumber" -> "XM002610011594",
    "documentAmount" -> 45552768.79,
    "documentOutstandingAmount" -> 297873.46,
    "lineItemDetails" -> Json.arr(lineItemDetailsFullJson),
    "documentInterestTotals" -> Json.obj(
      "interestAccruingAmount" -> 0.23
    ),
    "documentPenaltyTotals" -> Json.obj(
      "penaltyType" -> "LPP1",
      "penaltyStatus" -> "POSTED",
      "penaltyAmount" -> 10.01
    )
  )

  val fullDocumentDetailsNewIncorrectFields: DocumentDetailsNew = fullDocumentDetailsNew.copy(
    chargeReferenceNumber = None,
    documentTotalAmount = None
  )

}
