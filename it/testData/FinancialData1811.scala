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
package testData

import models.API1811.{DocumentDetails, Error, FinancialTransactions, LineItemDetails}
import play.api.libs.json.{JsObject, Json}
import play.api.http.Status
import utils.ImplicitDateFormatter._

object FinancialData1811 {

  val fullFinancialTransactionsJsonEIS: JsObject = Json.obj(
    "getFinancialData" -> Json.obj(
      "financialDetails" -> Json.obj(
        "totalisation" -> Json.obj(
          "regimeTotalisation" -> Json.obj(
            "totalAccountOverdue" -> 1000.0,
            "totalAccountNotYetDue" -> 250.0,
            "totalAccountCredit" -> 40.0,
            "totalAccountBalance" -> 1210.0
          ),
          "targetedSearch_SelectionCriteriaTotalisation" -> Json.obj(
            "totalOverdue" -> 100.0,
            "totalNotYetDue" -> 0.0,
            "totalBalance" -> 100.0,
            "totalCredit" -> 10.0,
            "totalCleared" -> 50.0
          ),
          "additionalReceivableTotalisations" -> Json.obj(
            "totalAccountPostedInterest" -> -99999999999.99,
            "totalAccountAccruingInterest" -> -99999999999.99
          ),
        ),
        "documentDetails" -> Json.arr(Json.obj(
          "documentNumber" -> "187346702498",
          "documentType" -> "TRM New Charge",
          "chargeReferenceNumber" -> "XP001286394838",
          "businessPartnerNumber" -> "100893731",
          "contractAccountNumber" -> "900726630",
          "contractAccountCategory" -> "VAT",
          "contractObjectNumber" -> "104920928302302",
          "contractObjectType" -> "ZVAT",
          "postingDate" -> "2022-01-01",
          "issueDate" -> "2022-01-01",
          "documentTotalAmount" -> 100.00,
          "documentClearedAmount" -> 100.00,
          "documentOutstandingAmount" -> 0.00,
          "documentLockDetails" -> Json.obj(
            "lockType" -> "Payment",
            "lockStartDate" -> "2022-01-01",
            "lockEndDate" -> "2022-01-01"
          ),
          "documentInterestTotals" -> Json.obj(
            "interestPostedAmount" -> "13.12",
            "interestPostedChargeRef" -> "XB001286323438",
            "interestAccruingAmount" -> 12.10
          ),
          "documentPenaltyTotals" -> Json.arr(Json.obj(
            "penaltyType" -> "LPP1",
            "penaltyStatus" -> "ACCRUING",
            "penaltyAmount" -> "10.01",
            "postedChargeReference" -> "XR00123933492"
          )),
          "lineItemDetails" -> Json.arr(Json.obj(
            "itemNumber" -> "0001",
            "subItemNumber" -> "003",
            "mainTransaction" -> "4700",
            "subTransaction" -> "1174",
            "chargeDescription" -> "VAT Return",
            "periodFromDate" -> "2022-01-01",
            "periodToDate" -> "2022-01-31",
            "periodKey" -> "22A1",
            "netDueDate" -> "2022-02-08",
            "formBundleNumber" -> "125435934761",
            "statisticalKey" -> "1",
            "amount" -> 3420.00,
            "clearingDate" -> "2022-02-09",
            "clearingReason" -> "Payment at External Payment Collector Reported",
            "clearingDocument" -> "719283701921",
            "outgoingPaymentMethod" -> "B",
            "ddCollectionInProgress" -> true,
            "lineItemLockDetails" -> Json.arr(Json.obj(
              "lockType" -> "Payment",
              "lockStartDate" -> "2022-01-01",
              "lockEndDate" -> "2022-01-01"
            )),
            "lineItemInterestDetails" -> Json.obj(
            "interestKey" -> "String",
              "currentInterestRate" -> -999.99,
              "interestStartDate" -> "1920-02-29",
              "interestPostedAmount" -> -99999999999.99,
              "interestAccruingAmount" -> -99999999999.99
            )
          ))
        ))
      )
    )
  )

  val lineItems: LineItemDetails = LineItemDetails(
    mainTransaction = Some("4700"),
    subTransaction = Some("1174"),
    periodFromDate = Some("2022-01-01"),
    periodToDate = Some("2022-01-31"),
    periodKey = Some("22A1"),
    netDueDate = Some("2022-02-08"),
    amount = Some(3420.0),
    ddCollectionInProgress = Some(true),
    clearingDate = Some("2022-02-09"),
    clearingReason = Some("Payment at External Payment Collector Reported"),
    clearingDocument = Some("719283701921"),
    interestRate = Some(-999.99)
  )

  val fullFinancialTransactions: FinancialTransactions = FinancialTransactions(Seq(
    DocumentDetails(
      chargeReferenceNumber = Some("XP001286394838"),
      documentTotalAmount = Some(100.00),
      documentOutstandingAmount = Some(0.0),
      documentClearedAmount = Some(100.0),
      lineItemDetails = Seq(lineItems),
      interestAccruingAmount = Some(12.10),
      penaltyType = Some("LPP1"),
      penaltyStatus = Some("ACCRUING"),
      penaltyAmount = Some(10.01)
    ))
  )

  val errorJson: JsObject = Json.obj(
    "failures" -> Json.arr(Json.obj(
      "code" -> "INVALID_CORRELATIONID",
      "reason" -> "Submission has not passed validation. Invalid header CorrelationId"
    ))
  )

  val errorModel: Error = Error(Status.BAD_REQUEST, errorJson.toString())

  val fullFinancialTransactionsOutputJson: JsObject = Json.obj(
    "financialTransactions" -> Json.arr(Json.obj(
      "chargeType" -> "VAT Return Debit Charge",
      "periodKey" -> "22A1",
      "taxPeriodFrom" -> "2022-01-01",
      "taxPeriodTo" -> "2022-01-31",
      "chargeReference" -> "XP001286394838",
      "mainTransaction" -> "4700",
      "subTransaction" -> "1174",
      "originalAmount" -> 100.00,
      "outstandingAmount" -> 0.00,
      "clearedAmount" -> 100.00,
      "items" -> Json.arr(Json.obj(
        "dueDate" -> "2022-02-08",
        "amount" -> 3420.00,
        "clearingDate" -> "2022-02-09",
        "clearingReason" -> "Payment at External Payment Collector Reported",
        "clearingSAPDocument" -> "719283701921",
        "DDcollectionInProgress" -> true
      )),
      "accruingInterestAmount" -> 12.10,
      "interestRate" -> -999.99,
      "accruingPenaltyAmount" -> 10.01,
      "penaltyType" -> "LPP1"
    ))
  )
}
