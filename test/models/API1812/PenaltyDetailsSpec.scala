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
      val result = Json.obj(
        "success" -> Json.obj(
          "processingDate" -> "2023-11-28T10:15:10Z",
          "penaltyData" -> Json.obj()
        )
      ).as[PenaltyDetails]
      result.LPPDetails.isEmpty shouldBe true
    }

    "parse JSON to an empty array and breathing space details when BS json is in the response" in {
      apiLPPJsonEISNoLPP(breathingSpaceJSONAfterBS).as[PenaltyDetails] shouldBe penaltyDetailsModelEmptyLPP
    }

    "parse a JSON array of LPP details to a sequence correctly" when {

      "optional fields are present" in {
        apiLPPJsonEIS(LPPJsonMax, breathingSpaceJSONAfterBS).as[PenaltyDetails] shouldBe penaltyDetailsModelMax
      }

      "optional fields are missing" in {
        apiLPPJsonEIS(LPPJsonMin, breathingSpaceJSONAfterBS).as[PenaltyDetails] shouldBe penaltyDetailsModelMin
      }
    }

    "serialize to JSON" when {

      "optional fields are present" in {
        Json.toJson(penaltyDetailsModelMax) shouldBe writtenPenDetailsMaxJson(breathingSpace = false)
      }

      "optional fields are missing" in {
        Json.toJson(penaltyDetailsModelMinNoBS) shouldBe writtenPenDetailsMinJson(breathingSpace = false)
      }

      "user has breathing space" in {
        Json.toJson(penaltyDetailsModelInBS) shouldBe writtenPenDetailsMinJson(breathingSpace = true)
      }
    }

  }

  "hasBreathingSpace" should {

    "return true" when {

      "user is well within breathing space" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(inBS))).hasBreathingSpace shouldBe true
      }

      "user is on the first day of breathing space" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(firstDayBS))).hasBreathingSpace shouldBe true
      }

      "user is on the last day of breathing space" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(lastDayBS))).hasBreathingSpace shouldBe true
      }

      "user is in the first of 2 breathing space periods" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(inBS, outOfBS))).hasBreathingSpace shouldBe true
      }

      "the user is in both breathing space and time to pay" in {
        PenaltyDetails(Some(Seq(LPPModelMin.copy(timeToPay = inTTP))), Some(Seq(inBS))).hasBreathingSpace shouldBe true
      }
    }

    "return false" when {

      "user's breathing space ended yesterday" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(BSEndYesterday))).hasBreathingSpace shouldBe false
      }

      "user's breathing space will begin tomorrow" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(BSBeginTomorrow))).hasBreathingSpace shouldBe false
      }

      "user has been out of breathing space for some time" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(outOfBS))).hasBreathingSpace shouldBe false
      }

      "user is due to go into breathing space in the future" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(futureBS))).hasBreathingSpace shouldBe false
      }

      "user has no breathing space data" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), None).hasBreathingSpace shouldBe false
      }

      "user is between 2 breathing space periods" in {
        PenaltyDetails(Some(Seq(LPPModelMin)), Some(Seq(outOfBS, futureBS))).hasBreathingSpace shouldBe false
      }

      "the user is in time to pay instead of breathing space" in {
        PenaltyDetails(Some(Seq(LPPModelMin.copy(timeToPay = inTTP))), None).hasBreathingSpace shouldBe false
      }

      "the user is between breathing space and time to pay" in {
        PenaltyDetails(Some(Seq(LPPModelMin.copy(timeToPay = futureTTP))), Some(Seq(outOfBS))).hasBreathingSpace shouldBe false
      }

      "the user is between time time to pay and breathing space" in {
        PenaltyDetails(Some(Seq(LPPModelMin.copy(timeToPay = outOfTTP))), Some(Seq(futureBS))).hasBreathingSpace shouldBe false
      }
    }
  }
}
