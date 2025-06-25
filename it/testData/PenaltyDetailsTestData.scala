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

package testData

import java.time.LocalDate

import models.API1812.{BreathingSpace, Error, PenaltyDetails, TimeToPay}
import models.API1812.latePaymentPenalty.{LPPPenaltyCategoryEnum, LatePaymentPenalty}
import play.api.libs.json.{JsObject, Json}
import play.api.http.Status

object PenaltyDetailsTestData {

  val timeToPayJson: JsObject = Json.obj(
    "TTPStartDate" -> "2018-04-07",
    "TTPEndDate" -> "2018-08-31"
  )

  val timeToPayJson2: JsObject = Json.obj(
    "TTPStartDate" -> "2018-04-08",
  )

  val LPPJson: JsObject = Json.obj(
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
    "timeToPay" -> Json.arr(timeToPayJson, timeToPayJson2)
  )

  val LPPJsonWritten: JsObject = Json.obj(
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

  val breathingSpaceJson: JsObject = Json.obj(
    "BSStartDate" -> "2017-04-06",
    "BSEndDate" -> "2017-06-30"
  )

  val penaltyDetailsAPIJson: JsObject = Json.obj(
    "success" -> Json.obj(
      "processingDate" -> "2023-11-28T10:15:10Z",
      "penaltyData" -> Json.obj(
        "lpp" -> Json.obj(
          "lppDetails" -> Json.arr(LPPJson)
        ),
        "breathingSpace" -> Json.arr(breathingSpaceJson)
      )
    )
  )

  val penaltyDetailsAPIJsonBSOnly: JsObject = Json.obj(
    "success" -> Json.obj(
      "processingDate" -> "2023-11-28T10:15:10Z",
      "penaltyData" -> Json.obj(
        "breathingSpace" -> Json.arr(breathingSpaceJson)
      )
    )
  )

  val penaltyDetailsWrittenJson: JsObject = Json.obj(
    "LPPDetails" -> Json.arr(LPPJsonWritten), "breathingSpace" -> false
  )

  val penaltyDetailsWrittenJsonBSOnly: JsObject = Json.obj(
    "LPPDetails" -> Json.arr(),
    "breathingSpace" -> false
  )

  val LPPModel: LatePaymentPenalty = LatePaymentPenalty(
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
    Some(Seq(TimeToPay(Some(LocalDate.parse("2018-04-07")), Some(LocalDate.parse("2018-08-31"))),
             TimeToPay(Some(LocalDate.parse("2018-04-08")), None)))
  )

  val penaltyDetailsModel: PenaltyDetails = PenaltyDetails(
    Some(Seq(LPPModel)),
    Some(Seq(BreathingSpace(LocalDate.parse("2017-04-06"), LocalDate.parse("2017-06-30"))))
  )

  val errorJson: JsObject = Json.obj(
    "failures" -> Json.arr(Json.obj(
      "code" -> "INVALID_REGIME",
      "reason" -> "Submission has not passed validation. Invalid parameter regime."
    ))
  )

  val errorModel: Error = Error(Status.BAD_REQUEST, errorJson.toString())
}
