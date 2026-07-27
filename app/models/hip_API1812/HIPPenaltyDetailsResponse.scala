/*
 * Copyright 2025 HM Revenue & Customs
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

package models.hip_API1812

import models.API1812.latePaymentPenalty.{HipLatePaymentPenalty, LatePaymentPenalty}
import models.API1812.{BreathingSpace, PenaltyDetails}
import play.api.libs.json.{Json, Reads}

case class PenaltyDetailsSuccessResponse(success: PenaltyDetailsSuccess)

case class PenaltyDetailsSuccess(processingDate: String, penaltyData: Option[PenaltyData]) {
  def toPenaltyDetailsModel: PenaltyDetails = {
    val lppDetails: Option[Seq[LatePaymentPenalty]] = penaltyData.flatMap(_.lpp.flatMap(_.lppDetails.map(_.map(_.toLatePaymentPenalty))))
    PenaltyDetails(LPPDetails = lppDetails, breathingSpace = penaltyData.flatMap(_.breathingSpace))
  }
}

case class PenaltyData(lpp: Option[Lpp], breathingSpace: Option[Seq[BreathingSpace]])

case class Lpp(
    lppDetails: Option[Seq[HipLatePaymentPenalty]],
    manualLPPIndicator: Boolean
)

object PenaltyDetailsSuccessResponse {
  implicit val reads: Reads[PenaltyDetailsSuccessResponse] = Json.reads[PenaltyDetailsSuccessResponse]
}

object PenaltyDetailsSuccess {
  implicit val reads: Reads[PenaltyDetailsSuccess] = Json.reads[PenaltyDetailsSuccess]
}

object PenaltyData {
  implicit val reads: Reads[PenaltyData] = Json.reads[PenaltyData]
}

object Lpp {
  implicit val reads: Reads[Lpp] = Json.reads[Lpp]
}
