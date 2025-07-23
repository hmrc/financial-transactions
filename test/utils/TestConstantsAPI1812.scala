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
import play.api.libs.json.{JsObject, JsValue, Json}
import models.API1812.latePaymentPenalty.{LPPPenaltyCategoryEnum, LatePaymentPenalty}
import models.API1812.{PenaltyDetails, BreathingSpace, TimeToPay}

object TestConstantsAPI1812 {

  val timeToPayJson: JsObject = Json.obj(
    "TTPStartDate" -> "2018-04-05",
    "TTPEndDate" -> "2018-08-31"
  )

  val timeToPayJsonOptionalEndDate: JsObject = Json.obj(
    "TTPStartDate" -> "2018-04-05"
  )

  val timeToPayJsonOptionalStartDate: JsObject = Json.obj(
    "TTPEndDate" -> "2018-08-31"
  )

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
    "penaltyChargeReference" -> "BCDEFGHIJKLMNOPQ",
    "timeToPay" -> Json.arr(timeToPayJson)
  )

  val LPPJsonMaxWritten: JsObject = Json.obj(
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
    "penaltyChargeReference" -> "BCDEFGHIJKLMNOPQ",
    "timeToPay" -> true
  )

  val LPPJsonMin: JsObject = Json.obj(
    "principalChargeReference" -> "ABCDEFGHIJKLMNOP",
    "penaltyCategory" -> "LPP1"
  )

  val LPPJsonMinWritten: JsObject = Json.obj(
    "principalChargeReference" -> "ABCDEFGHIJKLMNOP",
    "penaltyCategory" -> "LPP1",
    "timeToPay" -> false
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
    penaltyChargeReference = Some("BCDEFGHIJKLMNOPQ"),
    Some(Seq(TimeToPay(Some(LocalDate.parse("2018-04-05")), Some(LocalDate.parse("2018-08-31")))))
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
    None,
    None
  )

  def writtenPenDetailsMaxJson(breathingSpace: Boolean): JsObject = Json.obj(
    "LPPDetails" -> Json.arr(LPPJsonMaxWritten),
    "breathingSpace" -> breathingSpace
  )

  def writtenPenDetailsMinJson(breathingSpace: Boolean): JsObject = Json.obj(
    "LPPDetails" -> Json.arr(LPPJsonMinWritten),
    "breathingSpace" -> breathingSpace
  )

  val inBS           : BreathingSpace = BreathingSpace(LocalDate.parse("2018-04-01"), LocalDate.parse("2018-06-30"))
  val firstDayBS     : BreathingSpace = BreathingSpace(LocalDate.parse("2018-05-01"), LocalDate.parse("2018-05-30"))
  val lastDayBS      : BreathingSpace = BreathingSpace(LocalDate.parse("2018-04-01"), LocalDate.parse("2018-05-01"))
  val BSEndYesterday : BreathingSpace = BreathingSpace(LocalDate.parse("2018-04-01"), LocalDate.parse("2018-04-30"))
  val BSBeginTomorrow: BreathingSpace = BreathingSpace(LocalDate.parse("2018-05-02"), LocalDate.parse("2018-06-02"))
  val outOfBS        : BreathingSpace = BreathingSpace(LocalDate.parse("2018-03-01"), LocalDate.parse("2018-04-01"))
  val futureBS       : BreathingSpace = BreathingSpace(LocalDate.parse("2018-07-01"), LocalDate.parse("2018-08-01"))

  val outOfTTP        : Option[Seq[TimeToPay]] = Some(Seq(TimeToPay(Some(LocalDate.parse("2018-01-07")), Some(LocalDate.parse("2018-02-07")))))
  val inTTP           : Option[Seq[TimeToPay]] = Some(Seq(TimeToPay(Some(LocalDate.parse("2018-04-07")), Some(LocalDate.parse("2018-07-07")))))
  val firstDayTTP     : Option[Seq[TimeToPay]] = Some(Seq(TimeToPay(Some(LocalDate.parse("2018-05-01")), Some(LocalDate.parse("2018-07-01")))))
  val lastDayTTP      : Option[Seq[TimeToPay]] = Some(Seq(TimeToPay(Some(LocalDate.parse("2018-03-16")), Some(LocalDate.parse("2018-05-01")))))
  val TTPEndYesterday : Option[Seq[TimeToPay]] = Some(Seq(TimeToPay(Some(LocalDate.parse("2018-03-07")), Some(LocalDate.parse("2018-04-30")))))
  val TTPBeginTomorrow: Option[Seq[TimeToPay]] = Some(Seq(TimeToPay(Some(LocalDate.parse("2018-05-02")), Some(LocalDate.parse("2018-07-04")))))
  val futureTTP       : Option[Seq[TimeToPay]] = Some(Seq(TimeToPay(Some(LocalDate.parse("2018-06-18")), Some(LocalDate.parse("2018-09-05")))))
  val betweenTTPS     : Option[Seq[TimeToPay]] = Some(Seq(
      TimeToPay(Some(LocalDate.parse("2018-01-07")), Some(LocalDate.parse("2018-02-07"))),
      TimeToPay(Some(LocalDate.parse("2018-06-18")), Some(LocalDate.parse("2018-08-07")))
  ))
  val firstOfTwoTTPS  : Option[Seq[TimeToPay]] = Some(Seq(
    TimeToPay(Some(LocalDate.parse("2018-04-05")), Some(LocalDate.parse("2018-07-07"))),
    TimeToPay(Some(LocalDate.parse("2018-09-05")), Some(LocalDate.parse("2018-11-07")))
  ))
  val penaltyDetailsModelMax: PenaltyDetails = PenaltyDetails(Some(Seq(LPPModelMax)), Some(Seq(outOfBS)))
  val penaltyDetailsModelMinNoBS: PenaltyDetails = PenaltyDetails(Some(Seq(LPPModelMin)), None)
  val penaltyDetailsModelMin: PenaltyDetails = PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(outOfBS)))
  val penaltyDetailsModelNoPen: PenaltyDetails = PenaltyDetails(None, Some(Seq(outOfBS)))
  val penaltyDetailsModelEmptyLPP: PenaltyDetails = PenaltyDetails(Some(Seq()), Some(Seq(outOfBS))) // EIS returns Some(List()) for empty array
  val penaltyDetailsModelInBS: PenaltyDetails = PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(inBS)))
  val penaltyDetailsModelNone: PenaltyDetails = PenaltyDetails(None, None)

  def apiLPPJsonEIS(LPPJson: JsObject, bsJson: JsObject): JsValue = Json.obj(
    "latePaymentPenalty" -> Json.obj(
      "details" -> Json.arr(LPPJson)
    ),
    "breathingSpace" -> Json.arr(bsJson)
  )

  def apiLPPJsonEISNoLPP(bsJson: JsObject): JsValue = Json.obj(
    "latePaymentPenalty" -> Json.obj(
      "details" -> Json.arr()
    ),
    "breathingSpace" -> Json.arr(bsJson)
  )

  def apiLPPJsonHIP(LPPJson: JsObject, bsJson: JsObject): JsValue = Json.obj(
    "success" -> Json.obj(
      "processingDate" -> "2023-11-28T10:15:10Z",
      "penaltyData" -> Json.obj(
        "lpp" -> Json.obj(
          "lppDetails" -> Json.arr(LPPJson),
          "manualLPPIndicator" -> true
        ),
        "breathingSpace" -> Json.arr(bsJson)
      )
    )
  )

  val breathingSpaceJSON: JsObject = Json.obj(
    "BSStartDate" -> "2017-04-06",
    "BSEndDate" -> "2017-06-30"
  )

  val breathingSpaceJSONAfterBS: JsObject = Json.obj(
    "BSStartDate" -> "2018-03-01",
    "BSEndDate" -> "2018-04-01"
  )

  val apiLPPJsonNoPen: JsValue = Json.obj(
    "success" -> Json.obj(
      "processingDate" -> "2023-11-28T10:15:10Z",
      "penaltyData" -> Json.obj(
        "breathingSpace" -> Json.arr(breathingSpaceJSONAfterBS)
      )
    )
  )

}
