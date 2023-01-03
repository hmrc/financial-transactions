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

import models.API1812.latePaymentPenalty.LatePaymentPenalty
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{Reads, Writes, __}

case class PenaltyDetails(LPPDetails: Option[Seq[LatePaymentPenalty]])

object PenaltyDetails {

  implicit val reads: Reads[PenaltyDetails] =
    (__ \ "latePaymentPenalty" \ "details").readNullable[Seq[LatePaymentPenalty]].map(PenaltyDetails.apply)

  implicit val writes: Writes[PenaltyDetails] =
    (__ \ "LPPDetails").writeNullable[Seq[LatePaymentPenalty]].contramap(unlift(PenaltyDetails.unapply))
}
