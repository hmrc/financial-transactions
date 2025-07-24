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

import models.API1812.latePaymentPenalty.LatePaymentPenalty
import models.API1812.BreathingSpace
import play.api.libs.json.{Json, Reads}

case class HIPSuccessResponse(success: HIPSuccess)

case class HIPSuccess(processingDate: String, penaltyData: Option[HIPPenaltyData])

case class HIPPenaltyData(lpp: Option[HIPLpp], breathingSpace: Option[Seq[BreathingSpace]])

case class HIPLpp(
  lppDetails: Option[Seq[LatePaymentPenalty]],
  manualLPPIndicator: Boolean
)

object HIPSuccessResponse {
  implicit val reads: Reads[HIPSuccessResponse] = Json.reads[HIPSuccessResponse]
}

object HIPSuccess {
  implicit val reads: Reads[HIPSuccess] = Json.reads[HIPSuccess]
}

object HIPPenaltyData {
  implicit val reads: Reads[HIPPenaltyData] = Json.reads[HIPPenaltyData]
}

object HIPLpp {
  implicit val reads: Reads[HIPLpp] = Json.reads[HIPLpp]
}