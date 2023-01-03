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

package utils

import models.API1812.PenaltyDetails
import models.API1812.latePaymentPenalty._
import play.api.libs.json.{JsObject, JsValue, Json}

object TestConstantsAPI1812 {

  val LPPJsonMax: JsObject = Json.obj(
    "principalChargeReference" -> "ABCDEFGHIJKLMNOP",
    "penaltyCategory" -> "LPP1",
    "LPP1LRCalculationAmount" -> 100.11,
    "LPP1LRDays" -> "15",
    "LPP1LRPercentage" -> 2.4,
    "LPP1HRCalculationAmount" -> 200.22,
    "LPP1HRDays" -> "30",
    "LPP1HRPercentage" -> 4.2,
    "LPP2Days" -> "31",
    "LPP2Percentage" -> 5.5,
    "penaltyChargeReference" -> "BCDEFGHIJKLMNOPQ"
  )

  val LPPJsonMin: JsObject = Json.obj(
    "principalChargeReference" -> "ABCDEFGHIJKLMNOP",
    "penaltyCategory" -> "LPP1"
  )

  val LPPModelMax: LatePaymentPenalty = LatePaymentPenalty(
    principalChargeReference = "ABCDEFGHIJKLMNOP",
    penaltyCategory = LPPPenaltyCategoryEnum.firstPenalty,
    Some(100.11),
    Some("15"),
    Some(2.4),
    Some(200.22),
    Some("30"),
    Some(4.2),
    Some("31"),
    Some(5.5),
    penaltyChargeReference = Some("BCDEFGHIJKLMNOPQ")
  )

  val LPPModelMin: LatePaymentPenalty = LatePaymentPenalty(
    principalChargeReference = "ABCDEFGHIJKLMNOP",
    penaltyCategory = LPPPenaltyCategoryEnum.firstPenalty,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None
  )

  val penaltyDetailsModelMax: PenaltyDetails = PenaltyDetails(Some(Seq(LPPModelMax)))
  val penaltyDetailsModelMin: PenaltyDetails = PenaltyDetails(Some(Seq(LPPModelMin)))
  val penaltyDetailsModelNone: PenaltyDetails = PenaltyDetails(None)

  def apiLPPJson(LPPJson: JsObject): JsValue = Json.obj(
    "latePaymentPenalty" -> Json.obj(
      "details" -> Json.arr(LPPJson)
    )
  )

  def writtenLPPJson(LPPJson: JsObject): JsValue = Json.obj(
    "LPPDetails" -> Json.arr(LPPJson)
  )
}
