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

package models.API1812.latePaymentPenalty

import java.time.LocalDate

import play.api.libs.json.{Format, Json}

case class LatePaymentPenalty(penaltyNumber: String,
                              penaltyCategory: LPPPenaltyCategoryEnum.Value,
                              penaltyStatus: LPPPenaltyStatusEnum.Value,
                              penaltyAmountAccruing: BigDecimal,
                              penaltyAmountPosted: BigDecimal,
                              penaltyChargeCreationDate: LocalDate,
                              communicationsDate: LocalDate,
                              penaltyChargeReference: String,
                              penaltyChargeDueDate: LocalDate,
                              appealStatus: Option[String],
                              appealLevel: Option[String],
                              principalChargeReference: String,
                              principalChargeDueDate: LocalDate
                              )

object LatePaymentPenalty {
  implicit val format: Format[LatePaymentPenalty] = Json.format[LatePaymentPenalty]
}