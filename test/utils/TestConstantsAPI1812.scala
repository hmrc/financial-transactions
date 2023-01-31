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

import java.time.LocalDate

import models.API1812.{BreathingSpace, PenaltyDetails}
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

  def writtenPenDetailsMaxJson(breathingSpace: Boolean, timeToPay: Boolean): JsObject = Json.obj(
    "LPPDetails" -> Json.arr(LPPJsonMax),
    "breathingSpace" -> breathingSpace,
    "timeToPay" -> timeToPay
  )

  def writtenPenDetailsMinJson(breathingSpace: Boolean, timeToPay: Boolean): JsObject = Json.obj(
    "LPPDetails" -> Json.arr(LPPJsonMin),
    "breathingSpace" -> breathingSpace,
    "timeToPay" -> timeToPay
  )

  val inBS           : BreathingSpace = BreathingSpace(LocalDate.parse("2018-04-01"), LocalDate.parse("2018-06-30"))
  val firstDayBS     : BreathingSpace = BreathingSpace(LocalDate.parse("2018-05-01"), LocalDate.parse("2018-05-30"))
  val lastDayBS      : BreathingSpace = BreathingSpace(LocalDate.parse("2018-04-01"), LocalDate.parse("2018-05-01"))
  val BSEndYesterday : BreathingSpace = BreathingSpace(LocalDate.parse("2018-04-01"), LocalDate.parse("2018-04-30"))
  val BSBeginTomorrow: BreathingSpace = BreathingSpace(LocalDate.parse("2018-05-02"), LocalDate.parse("2018-06-02"))
  val outOfBS        : BreathingSpace = BreathingSpace(LocalDate.parse("2018-03-01"), LocalDate.parse("2018-04-01"))
  val futureBS       : BreathingSpace = BreathingSpace(LocalDate.parse("2018-07-01"), LocalDate.parse("2018-08-01"))

  val penaltyDetailsModelMax: PenaltyDetails = PenaltyDetails(Some(Seq(LPPModelMax)), Some(Seq(outOfBS)), None)
  val penaltyDetailsModelMinNoBS: PenaltyDetails = PenaltyDetails(Some(Seq(LPPModelMin)), None, None)
  val penaltyDetailsModelMin: PenaltyDetails = PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(outOfBS)), None)
  val penaltyDetailsModelNoPen: PenaltyDetails = PenaltyDetails(None, Some(Seq(outOfBS)), None)
  val penaltyDetailsModelInBS: PenaltyDetails = PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(inBS)), None)
  val penaltyDetailsModelNone: PenaltyDetails = PenaltyDetails(None, None, None)

  def apiLPPJson(LPPJson: JsObject, bsJson: JsObject): JsValue = Json.obj(
    "latePaymentPenalty" -> Json.obj(
      "details" -> Json.arr(LPPJson)
    ),
    "breathingSpace" -> Json.arr(bsJson)
  )

  val breathingSpaceJSON: JsObject = Json.obj(
    "BSStartDate" -> "2017-04-06",
    "BSEndDate" -> "2017-06-30"
  )

  def writtenLPPJson(LPPJson: JsObject, timeToPay: Boolean): JsValue = Json.obj(
    "LPPDetails" -> Json.arr(LPPJson),
    "timeToPay" -> timeToPay
  )

  val breathingSpaceJSONNoBS: JsObject = Json.obj(
    "BSStartDate" -> "2018-03-01",
    "BSEndDate" -> "2018-04-01"
  )

  val apiLPPJsonNoPen: JsValue = Json.obj(
    "breathingSpace" -> Json.arr(breathingSpaceJSONNoBS)
  )

}
