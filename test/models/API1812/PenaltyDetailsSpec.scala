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

import base.SpecBase
import play.api.libs.json.Json
import utils.TestConstantsAPI1812._

class PenaltyDetailsSpec extends SpecBase {

  "PenaltyDetails" should {

    "parse JSON to an empty sequence when no LPP details exist" in {
      val result = Json.obj().as[PenaltyDetails]
      result.LPPDetails.isEmpty shouldBe true
    }

    "parse JSON to an empty array and breathing space details when BS json and TTP json are in the response" in {
      val result = apiLPPJsonNoPen.as[PenaltyDetails]
      result shouldBe penaltyDetailsModelNoPen
    }

    "parse a JSON array of LPP details to a sequence correctly" when {

      "optional fields are present" in {
        apiLPPJson(LPPJsonMax, breathingSpaceJSONAfterBS, timeToPayJsonAfterTTP).as[PenaltyDetails] shouldBe penaltyDetailsModelMax
      }

      "optional fields are missing" in {
        apiLPPJson(LPPJsonMin, breathingSpaceJSONAfterBS, timeToPayJsonAfterTTP).as[PenaltyDetails] shouldBe penaltyDetailsModelMin
      }
    }

    "serialize to JSON" when {

      "optional fields are present" in {
        Json.toJson(penaltyDetailsModelMax) shouldBe writtenPenDetailsMaxJson(breathingSpace = false, timeToPay = false)
      }

      "optional fields are missing" in {
        Json.toJson(penaltyDetailsModelMinNoBS) shouldBe writtenPenDetailsMinJson(breathingSpace = false, timeToPay = false)
      }

      "user has breathing space" in {
        Json.toJson(penaltyDetailsModelInBS) shouldBe writtenPenDetailsMinJson(breathingSpace = true, timeToPay = false)
      }

      "user has time to pay" in {
        Json.toJson(penaltyDetailsModelInTTP) shouldBe writtenPenDetailsMinJson(breathingSpace = false, timeToPay = true)
      }

      "the user has both breathing space and time to pay" in {
        Json.toJson(penaltyDetailsModelInBSAndTTP) shouldBe writtenPenDetailsMinJson(breathingSpace = true, timeToPay = true)
      }
    }

  }

  "hasBreathingSpace" should {

    "return true" when {

      "user is well within breathing space" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(inBS)), None).hasBreathingSpace shouldBe true
      }

      "user is on the first day of breathing space" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(firstDayBS)), None).hasBreathingSpace shouldBe true
      }

      "user is on the last day of breathing space" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(lastDayBS)), None).hasBreathingSpace shouldBe true
      }

      "user is in the first of 2 breathing space periods" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(inBS, outOfBS)), None).hasBreathingSpace shouldBe true
      }

      "the user is in both breathing space and time to pay" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(inBS)), Some(Seq(inTTP))).hasBreathingSpace shouldBe true
      }
    }

    "return false" when {

      "user's breathing space ended yesterday" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(BSEndYesterday)), None).hasBreathingSpace shouldBe false
      }

      "user's breathing space will begin tomorrow" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(BSBeginTomorrow)), None).hasBreathingSpace shouldBe false
      }

      "user has been out of breathing space for some time" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(outOfBS)), None).hasBreathingSpace shouldBe false
      }

      "user is due to go into breathing space in the future" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(futureBS)), None).hasBreathingSpace shouldBe false
      }

      "user has no breathing space data" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), None, None).hasBreathingSpace shouldBe false
      }

      "user is between 2 breathing space periods" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(outOfBS, futureBS)), None).hasBreathingSpace shouldBe false
      }

      "the user is in time to pay instead of breathing space" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), None, Some(Seq(inTTP))).hasBreathingSpace shouldBe false
      }

      "the user is between breathing space and time to pay" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(outOfBS)), Some(Seq(futureTTP))).hasBreathingSpace shouldBe false
      }

      "the user is between time time to pay and breathing space" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(futureBS)), Some(Seq(outOfTTP))).hasBreathingSpace shouldBe false
      }
    }
  }

  "hasTimeToPay" should {

    "return true" when {

      "user is well within time to pay" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), None, Some(Seq(inTTP))).hasTimeToPay shouldBe true
      }

      "user is on the first day of time to pay" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), None, Some(Seq(firstDayTTP))).hasTimeToPay shouldBe true
      }

      "user is on the last day of time to pay" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), None, Some(Seq(lastDayTTP))).hasTimeToPay shouldBe true
      }

      "user is in the first of 2 time to pay space periods" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), None, Some(Seq(inTTP, outOfTTP))).hasTimeToPay shouldBe true
      }

      "the user is in both breathing space and time to pay" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(inBS)), Some(Seq(inTTP))).hasTimeToPay shouldBe true
      }
    }

    "return false" when {

      "user's time to pay ended yesterday" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), None, Some(Seq(TTPEndYesterday))).hasTimeToPay shouldBe false
      }

      "user's time to pay will begin tomorrow" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), None, Some(Seq(TTPBeginTomorrow))).hasTimeToPay shouldBe false
      }

      "user has been out of time to pay for some time" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), None, Some(Seq(outOfTTP))).hasTimeToPay shouldBe false
      }

      "user is due to go into time to pay in the future" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), None, Some(Seq(futureTTP))).hasTimeToPay shouldBe false
      }

      "user has no time to pay data" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), None, None).hasTimeToPay shouldBe false
      }

      "user is between 2 time to pay periods" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), None, Some(Seq(outOfTTP, futureTTP))).hasTimeToPay shouldBe false
      }

      "the user is in breathing space instead of time to pay" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(inBS)), None).hasTimeToPay shouldBe false
      }

      "the user is between breathing space and time to pay" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(outOfBS)), Some(Seq(futureTTP))).hasTimeToPay shouldBe false
      }

      "the user is between time time to pay and breathing space" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(futureBS)), Some(Seq(outOfTTP))).hasTimeToPay shouldBe false
      }
    }
  }
}
