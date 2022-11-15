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

import models.API1811.{DocumentDetails, FinancialTransactions, LineItemDetails}
import play.api.libs.json.{JsObject, Json}
import utils.ImplicitDateFormatter._

object TestConstants {

  val lineItemDetailsFull: LineItemDetails = LineItemDetails(
    mainTransaction = Some("4700"),
    subTransaction = Some("1174"),
    periodKey = Some("13RL"),
    periodFromDate = Some("2017-04-06"),
    periodToDate = Some("2018-04-05"),
    netDueDate = Some("2018-02-14"),
    amount = Some(3400),
    ddCollectionInProgress = Some(true),
    clearingDate = Some("2017-08-06"),
    clearingReason = Some("Payment at External Payment Collector Reported"),
    clearingDocument = Some("719283701921"),
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
    ),
    "clearingDate" -> "2017-08-06",
    "clearingReason" -> "Payment at External Payment Collector Reported",
    "clearingDocument" -> "719283701921"
  )

  val lineItemDetailsJsonBadCharge: JsObject = Json.obj(
    "mainTransaction" -> "470055555",
    "subTransaction" -> "117455555",
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
    ),
    "clearingDate" -> "2017-08-06",
    "clearingReason" -> "Payment at External Payment Collector Reported",
    "clearingDocument" -> "719283701921"
  )

  val fullDocumentDetails: DocumentDetails = DocumentDetails(
    chargeReferenceNumber = Some("XM002610011594"),
    documentTotalAmount = Some(45552768.79),
    documentOutstandingAmount = Some(297873.46),
    documentClearedAmount = Some(45254895.33),
    lineItemDetails = Seq(lineItemDetailsFull),
    interestAccruingAmount = Some(0.23),
    penaltyType = Some("LPP1"),
    penaltyStatus = Some("ACCRUING"),
    penaltyAmount = Some(10.01)
  )

  val fullDocumentDetailsJson: JsObject = Json.obj(
    "chargeReferenceNumber" -> "XM002610011594",
    "documentTotalAmount" -> 45552768.79,
    "documentOutstandingAmount" -> 297873.46,
    "documentClearedAmount" -> 45254895.33,
    "lineItemDetails" -> Json.arr(lineItemDetailsFullJson),
    "documentInterestTotals" -> Json.obj(
      "interestAccruingAmount" -> 0.23
    ),
    "documentPenaltyTotals" -> Json.arr(Json.obj(
      "penaltyType" -> "LPP1",
      "penaltyStatus" -> "ACCRUING",
      "penaltyAmount" -> 10.01
    ))
  )

  val fullDocumentDetailsJsonBadCharge: JsObject = Json.obj(
    "chargeReferenceNumber" -> "XM002610011594",
    "documentTotalAmount" -> 45552768.79,
    "documentOutstandingAmount" -> 297873.46,
    "documentClearedAmount" -> 45254895.33,
    "lineItemDetails" -> Json.arr(lineItemDetailsJsonBadCharge),
    "documentInterestTotals" -> Json.obj(
      "interestAccruingAmount" -> 0.23
    ),
    "documentPenaltyTotals" -> Json.arr(Json.obj(
      "penaltyType" -> "LPP1",
      "penaltyStatus" -> "ACCRUING",
      "penaltyAmount" -> 10.01
    ))
  )

  val documentDetailsIncorrectFieldsJson: JsObject = Json.obj(
    "chargeRefNumber" -> "XM002610011594",
    "documentAmount" -> 45552768.79,
    "documentOutstandingAmount" -> 297873.46,
    "documentClearedAmount" -> 45254895.33,
    "lineItemDetails" -> Json.arr(lineItemDetailsFullJson),
    "documentInterestTotals" -> Json.obj(
      "interestAccruingAmount" -> 0.23
    ),
    "documentPenaltyTotals" -> Json.arr(Json.obj(
      "penaltyType" -> "LPP1",
      "penaltyStatus" -> "ACCRUING",
      "penaltyAmount" -> 10.01
    ))
  )

  val fullDocumentDetailsIncorrectFields: DocumentDetails = fullDocumentDetails.copy(
    chargeReferenceNumber = None,
    documentTotalAmount = None
  )

  val emptyLineItem: LineItemDetails = LineItemDetails(None, None, None, None, None, None, None, None, None, None, None, None)
  val emptyDocumentDetails: DocumentDetails = DocumentDetails(None, None, None, None, Seq(emptyLineItem), None, None, None, None)

  def fullFTJson(documentDetails: JsObject): JsObject = Json.obj(
    "getFinancialData" -> Json.obj(
      "financialDetails" -> Json.obj(
        "documentDetails" -> Json.arr(documentDetails)
      )
    )
  )
  val fullFinancialTransactionsJsonEIS: JsObject = fullFTJson(fullDocumentDetailsJson)
  val filteredFinancialJson: JsObject = fullFTJson(fullDocumentDetailsJsonBadCharge)

  val fullFinancialTransactions: FinancialTransactions = FinancialTransactions(
    documentDetails = Seq(fullDocumentDetails)
  )

  val fullLineItemDetailsOutputJson: JsObject = Json.obj(
    "dueDate" -> "2018-02-14",
    "amount" -> 3400,
    "clearingDate" -> "2017-08-06",
    "clearingReason" -> "Payment at External Payment Collector Reported",
    "clearingSAPDocument" -> "719283701921",
    "DDcollectionInProgress" -> true
  )

  val fullDocumentDetailsOutputJson: JsObject = Json.obj(
    "chargeType" -> "VAT Return Debit Charge",
    "periodKey" -> "13RL",
    "taxPeriodFrom" -> "2017-04-06",
    "taxPeriodTo" -> "2018-04-05",
    "chargeReference" -> "XM002610011594",
    "mainTransaction" -> "4700",
    "subTransaction" -> "1174",
    "originalAmount" -> 45552768.79,
    "outstandingAmount" -> 297873.46,
    "clearedAmount" -> 45254895.33,
    "items" -> Json.arr(fullLineItemDetailsOutputJson),
    "accruingInterestAmount" -> 0.23,
    "interestRate" -> 3,
    "accruingPenaltyAmount" -> 10.01,
    "penaltyType" -> "LPP1"
  )

  val fullFinancialTransactionsOutputJson: JsObject = Json.obj(
    "financialTransactions" -> Json.arr(fullDocumentDetailsOutputJson)
  )
}
