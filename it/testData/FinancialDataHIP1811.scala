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
package testData

import models.API1811.{DocumentDetails, DocumentPenaltyTotals, Error, FinancialTransactions, LineItemDetails, LineItemLockDetails}
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}

import java.time.LocalDate

object FinancialDataHIP1811 {

  val fullFinancialTransactionsJsonHIP: JsObject = Json.obj(
    "success" -> Json.obj(
    "processingDate" -> "2024-12-31T13:34:56Z",
    "FinancialData" -> Json.obj(
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
            "penaltyAmount" -> "10.01"
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
              "lockType" -> "Some payment lock",
              "lockStartDate" -> "2022-01-01",
              "lockEndDate" -> "2022-01-01"
            ))
          ))
        ))
      )
    )
  )
  val singleErrorHIP: JsObject = Json.obj(
    "error" -> Json.obj(
      "code" -> "TECH_ERROR",
      "message" -> "Internal server error occurred",
      "logId" -> "log-12345-abc"
    )
  )

  val singleErrorHIPModel: Error = Error(Status.BAD_REQUEST, singleErrorHIP.toString())

  val multipleErrorsHIP: JsObject = Json.obj(
    "processingDate" -> "2024-12-31T13:34:56Z",
    "errors" -> Json.arr(
      Json.obj(
        "code" -> "BUSINESS_ERR_001",
        "text" -> "Invalid VAT registration number"
      ),
      Json.obj(
        "code" -> "BUSINESS_ERR_002",
        "text" -> "Missing tax period"
      )
    )
  )

  val multipleErrorsHIPModel: Error = Error(Status.BAD_REQUEST, multipleErrorsHIP.toString())
}