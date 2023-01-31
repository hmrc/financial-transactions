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

package models.API1812

import config.AppConfig
import models.API1812.latePaymentPenalty.LatePaymentPenalty
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{Json, Reads, Writes, __}
import services.DateService

case class PenaltyDetails(LPPDetails: Option[Seq[LatePaymentPenalty]],
                          breathingSpace: Option[Seq[BreathingSpace]],
                          timeToPay: Option[Seq[TimeToPay]]) {

  def hasBreathingSpace(implicit appConfig: AppConfig): Boolean =
    breathingSpace.fold(false)(_.exists { bs =>
      (bs.BSStartDate.isBefore(DateService.now) || bs.BSStartDate.isEqual(DateService.now)) &&
      (bs.BSEndDate.isAfter(DateService.now) || bs.BSEndDate.isEqual(DateService.now))
    })

  def hasTimeToPay(implicit appConfig: AppConfig): Boolean = timeToPay.fold(false)(_.exists { ttp =>
    (ttp.TTPStartDate.isBefore(DateService.now) || ttp.TTPStartDate.isEqual(DateService.now)) &&
      (ttp.TTPEndDate.isAfter(DateService.now) || ttp.TTPEndDate.isEqual(DateService.now))
  })
}

object PenaltyDetails {

  implicit val reads: Reads[PenaltyDetails] = (
    (__ \ "latePaymentPenalty" \ "details").readNullable[Seq[LatePaymentPenalty]] and
    (__ \ "breathingSpace").readNullable[Seq[BreathingSpace]] and
    (__ \ "timeToPay").readNullable[Seq[TimeToPay]]
  )(PenaltyDetails.apply _)

  implicit def writes(implicit appConfig: AppConfig): Writes[PenaltyDetails] = { model =>
    Json.obj(
      "LPPDetails" -> Json.toJsFieldJsValueWrapper(model.LPPDetails.getOrElse(Seq())),
      "breathingSpace" -> model.hasBreathingSpace,
      "timeToPay" -> model.hasTimeToPay
    )
  }
}
