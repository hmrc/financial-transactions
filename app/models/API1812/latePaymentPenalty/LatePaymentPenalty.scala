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

package models.API1812.latePaymentPenalty

import config.AppConfig
import models.API1812.TimeToPay
import play.api.libs.json.{JsNull, JsObject, Json, Reads, Writes}
import services.DateService

case class LatePaymentPenalty(principalChargeReference: String,
                              penaltyCategory: LPPPenaltyCategoryEnum.Value,
                              LPP1LRCalculationAmount: Option[BigDecimal],
                              LPP1LRDays: Option[String],
                              LPP1LRPercentage: Option[Double],
                              LPP1HRCalculationAmount: Option[BigDecimal],
                              LPP1HRDays: Option[String],
                              LPP1HRPercentage: Option[Double],
                              LPP2Days: Option[String],
                              LPP2Percentage: Option[Double],
                              penaltyChargeReference: Option[String],
                              timeToPay: Option[Seq[TimeToPay]]) {

  def hasTimeToPay(implicit appConfig: AppConfig): Boolean = {
    timeToPay.fold(false)(_.exists(ttp => ttp.TTPStartDate.exists(!_.isAfter(DateService.now))
      && ttp.TTPEndDate.exists(!_.isBefore(DateService.now))))
  }
}

object LatePaymentPenalty {

  implicit val reads: Reads[LatePaymentPenalty] = Json.reads[LatePaymentPenalty]

  implicit def writes(implicit appConfig: AppConfig): Writes[LatePaymentPenalty] = { model =>
    JsObject(Json.obj(
      "principalChargeReference" -> model.principalChargeReference,
      "penaltyCategory" -> model.penaltyCategory,
      "LPP1LRCalculationAmount" -> model.LPP1LRCalculationAmount,
      "LPP1LRDays" -> model.LPP1LRDays,
      "LPP1LRPercentage" -> model.LPP1LRPercentage,
      "LPP1HRCalculationAmount" -> model.LPP1HRCalculationAmount,
      "LPP1HRDays" -> model.LPP1HRDays,
      "LPP1HRPercentage" -> model.LPP1HRPercentage,
      "LPP2Days" -> model.LPP2Days,
      "LPP2Percentage" -> model.LPP2Percentage,
      "penaltyChargeReference" -> model.penaltyChargeReference,
      "timeToPay" -> model.hasTimeToPay
    ).fields.filterNot(_._2 == JsNull))
  }
}
