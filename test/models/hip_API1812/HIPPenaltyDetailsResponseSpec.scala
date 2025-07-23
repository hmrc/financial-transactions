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

package models.hip_API1812

import base.SpecBase
import models.API1812.latePaymentPenalty.LatePaymentPenalty
import models.API1812.BreathingSpace
import play.api.libs.json.Json

import java.time.LocalDate

class HIPPenaltyDetailsResponseSpec extends SpecBase {

  "HIPSuccessResponse" should {
    "read from JSON with full penalty data" in {
      val json = Json.obj(
        "success" -> Json.obj(
          "processingDate" -> "2023-11-28T10:15:10Z",
          "penaltyData" -> Json.obj(
            "lpp" -> Json.obj(
              "lppDetails" -> Json.arr(
                Json.obj(
                  "principalChargeReference" -> "ABCDEFGHIJKLMNOP",
                  "penaltyCategory" -> "LPP1"
                )
              ),
              "manualLPPIndicator" -> true
            ),
            "breathingSpace" -> Json.arr(
              Json.obj(
                "BSStartDate" -> "2018-04-01",
                "BSEndDate" -> "2018-06-30"
              )
            )
          )
        )
      )

      val result = json.as[HIPSuccessResponse]
      result.success.processingDate shouldBe "2023-11-28T10:15:10Z"
      result.success.penaltyData shouldBe defined
      result.success.penaltyData.get.lpp shouldBe defined
      result.success.penaltyData.get.breathingSpace shouldBe defined
    }

    "read from JSON with no penalty data" in {
      val json = Json.obj(
        "success" -> Json.obj(
          "processingDate" -> "2023-11-28T10:15:10Z"
        )
      )

      val result = json.as[HIPSuccessResponse]
      result.success.processingDate shouldBe "2023-11-28T10:15:10Z"
      result.success.penaltyData shouldBe None
    }
  }

  "HIPSuccess" should {
    "read from JSON with all fields" in {
      val json = Json.obj(
        "processingDate" -> "2023-11-28T10:15:10Z",
        "penaltyData" -> Json.obj(
          "lpp" -> Json.obj(
            "lppDetails" -> Json.arr(),
            "manualLPPIndicator" -> false
          ),
          "breathingSpace" -> Json.arr()
        )
      )

      val result = json.as[HIPSuccess]
      result.processingDate shouldBe "2023-11-28T10:15:10Z"
      result.penaltyData shouldBe defined
    }

    "read from JSON with only processing date" in {
      val json = Json.obj(
        "processingDate" -> "2023-11-28T10:15:10Z"
      )

      val result = json.as[HIPSuccess]
      result.processingDate shouldBe "2023-11-28T10:15:10Z"
      result.penaltyData shouldBe None
    }
  }

  "HIPPenaltyData" should {
    "read from JSON with all fields" in {
      val json = Json.obj(
        "lpp" -> Json.obj(
          "lppDetails" -> Json.arr(),
          "manualLPPIndicator" -> true
        ),
        "breathingSpace" -> Json.arr()
      )

      val result = json.as[HIPPenaltyData]
      result.lpp shouldBe defined
      result.breathingSpace shouldBe defined
    }

    "read from JSON with only lpp" in {
      val json = Json.obj(
        "lpp" -> Json.obj(
          "lppDetails" -> Json.arr(),
          "manualLPPIndicator" -> false
        )
      )

      val result = json.as[HIPPenaltyData]
      result.lpp shouldBe defined
      result.breathingSpace shouldBe None
    }

    "read from JSON with only breathing space" in {
      val json = Json.obj(
        "breathingSpace" -> Json.arr()
      )

      val result = json.as[HIPPenaltyData]
      result.lpp shouldBe None
      result.breathingSpace shouldBe defined
    }
  }

  "HIPLpp" should {
    "read from JSON with all fields" in {
      val json = Json.obj(
        "lppDetails" -> Json.arr(),
        "manualLPPIndicator" -> true
      )

      val result = json.as[HIPLpp]
      result.lppDetails shouldBe defined
      result.manualLPPIndicator shouldBe true
    }

    "read from JSON with only manualLPPIndicator" in {
      val json = Json.obj(
        "manualLPPIndicator" -> false
      )

      val result = json.as[HIPLpp]
      result.lppDetails shouldBe None
      result.manualLPPIndicator shouldBe false
    }
  }
}

