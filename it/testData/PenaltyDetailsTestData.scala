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

import models.API1812.PenaltyDetails
import models.API1812.latePaymentPenalty.{LPPPenaltyCategoryEnum, LPPPenaltyStatusEnum, LatePaymentPenalty}
import models.API1812.lateSubmissionPenalty.{LSPDetails, LSPPenaltyCategoryEnum, LSPPenaltyStatusEnum, LSPSummary, LateSubmission, LateSubmissionPenalty}
import play.api.libs.json.{JsObject, Json}

import java.time.LocalDate

object PenaltyDetailsTestData {

  val fullLPPJson: JsObject = Json.obj(
    "details" -> Json.arr(
      Json.obj(
        "penaltyNumber" -> "5678ABCD",
      "penaltyCategory" -> "LPP1",
      "penaltyStatus" -> "P",
      "penaltyAmountAccruing" -> 123.45,
      "penaltyAmountPosted" -> 123.45,
      "penaltyChargeCreationDate" -> "2022-01-01",
      "communicationsDate" -> "2022-01-01",
      "penaltyChargeReference" -> "CHARGE123456",
      "penaltyChargeDueDate" -> "2022-02-01",
      "appealStatus" -> "1",
      "appealLevel" -> "1",
      "principalChargeDueDate" -> "2022-03-01",
      "principalChargeReference" -> "CHARGING12345"
      )
    )
  )

  val fullLateSubmissionJson: JsObject = Json.obj(
    "lateSubmissionID" -> "ID123",
    "taxPeriod" -> "1",
    "taxReturnStatus" -> "2",
    "taxPeriodStartDate" -> "2022-01-01",
    "taxPeriodEndDate" -> "2022-03-31",
    "taxPeriodDueDate" -> "2022-05-07",
    "returnReceiptDate" -> "2022-04-01"
  )

  val fullLSPDetailsJson: JsObject = Json.obj(
    "penaltyNumber" ->"1234ABCD",
    "penaltyOrder" -> "1",
    "penaltyCategory" ->"P",
    "penaltyStatus" ->"ACTIVE",
    "penaltyCreationDate" ->"2022-01-01",
    "penaltyExpiryDate" ->"2024-01-01",
    "communicationsDate" -> "2022-01-01",
    "lateSubmissions" -> Json.arr(fullLateSubmissionJson),
    "appealStatus" ->"1",
    "appealLevel" ->"1",
    "chargeReference" ->"foobar",
    "chargeAmount" -> 123.45,
    "chargeOutstandingAmount" -> 123.45,
    "chargeDueDate" -> "2022-01-01"
  )

  val lspSummaryJson: JsObject = Json.obj(
    "activePenaltyPoints" -> 1,
    "inactivePenaltyPoints" -> 2,
    "POCAchievementDate" -> "2022-01-01",
    "regimeThreshold" -> 3,
    "penaltyChargeAmount" -> 123.45
  )

  val fullLSPJson: JsObject = Json.obj(
    "summary" -> lspSummaryJson,
    "details" -> Json.arr(fullLSPDetailsJson)
  )

  val fullPenaltyDetailsJson: JsObject = Json.obj(
    "lateSubmissionPenalty" -> fullLSPJson,
    "latePaymentPenalty" -> fullLPPJson
  )

  val fullLPPModel: LatePaymentPenalty = LatePaymentPenalty(
    penaltyNumber = "5678ABCD",
    penaltyCategory = LPPPenaltyCategoryEnum.firstPenalty,
    penaltyStatus = LPPPenaltyStatusEnum.Posted,
    penaltyAmountAccruing = 123.45,
    penaltyAmountPosted = 123.45,
    penaltyChargeCreationDate = LocalDate.of(2022, 1, 1),
    communicationsDate = LocalDate.of(2022, 1, 1),
    penaltyChargeReference = "CHARGE123456",
    penaltyChargeDueDate = LocalDate.of(2022, 2, 1),
    appealStatus = Some("1"),
    appealLevel = Some("1"),
    principalChargeReference = "CHARGING12345",
    principalChargeDueDate = LocalDate.of(2022, 3, 1)
  )

  val lateSubmissionResponseModel: LateSubmission = LateSubmission(
    lateSubmissionID = "ID123",
    taxPeriod = Some("1"),
    taxReturnStatus = "2",
    taxPeriodStartDate = Some(LocalDate.of(2022, 1, 1)),
    taxPeriodEndDate = Some(LocalDate.of(2022, 3, 31)),
    taxPeriodDueDate = Some(LocalDate.of(2022, 5, 7)),
    returnReceiptDate = Some(LocalDate.of(2022, 4, 1))
  )

  val fullLSPDetailsModel: LSPDetails = LSPDetails(
    penaltyCategory = LSPPenaltyCategoryEnum.Point,
    penaltyOrder = "1",
    penaltyNumber = "1234ABCD",
    penaltyCreationDate = LocalDate.of(2022, 1, 1),
    penaltyExpiryDate = LocalDate.of(2024, 1, 1),
    penaltyStatus = LSPPenaltyStatusEnum.Active,
    appealStatus = Some("1"),
    communicationsDate = LocalDate.of(2022, 1, 1),
    lateSubmissions = Some(Seq(lateSubmissionResponseModel)),
    appealLevel = Some("1"),
    chargeReference = Some("foobar"),
    chargeAmount = Some(123.45),
    chargeOutstandingAmount = Some(123.45),
    chargeDueDate = Some(LocalDate.of(2022, 1, 1))
  )

  val lspSummaryModel: LSPSummary = LSPSummary(
    activePenaltyPoints = 1,
    inactivePenaltyPoints = 2,
    POCAchievementDate = LocalDate.of(2022, 1, 1),
    regimeThreshold = 3,
    penaltyChargeAmount = 123.45
  )

  val fullLSPModel: LateSubmissionPenalty = LateSubmissionPenalty(
    summary = lspSummaryModel,
    details = Seq(fullLSPDetailsModel)
  )

  val fullPenaltyDetailsModel: PenaltyDetails = PenaltyDetails(
    lateSubmissionPenalty = Some(fullLSPModel),
    latePaymentPenalty = Some(Seq(fullLPPModel))
  )

}
