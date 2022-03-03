/*
 * Copyright 2022 HM Revenue & Customs
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

  "Get Penalty Details" should {
    "return empty json when no LSP/LPP details exist" in {
      val result = Json.fromJson(Json.parse("{}"))(PenaltyDetails.format)
      result.isSuccess shouldBe true
      result.get.latePaymentPenalty.isEmpty shouldBe true
      result.get.lateSubmissionPenalty.isEmpty shouldBe true
    }

    "deserialize successfully where only latePaymentPenalty is populated" in {
      penaltyDetailsLPPJson.as[PenaltyDetails] shouldBe penaltyDetailsLPPModel
    }

    "deserialize successfully to a GetPenaltyDetailsModel where only lateSubmissionPenalty is populated" in {
      penaltyDetailsLSPJson.as[PenaltyDetails] shouldBe penaltyDetailsLSPModel
    }

    "deserialize successfully to a GetPenaltyDetailsModel where both latePaymentPenalty and lateSubmissionPenalty are populated" in {
      penaltyDetailsAllJson.as[PenaltyDetails] shouldBe penaltyDetailsAllModel
    }

    "be writable to JSON" when {
      "no LSP/LPP details exist - return empty JSON" in {
        Json.toJson(penaltyDetailsNone) shouldBe Json.obj()
      }
    }
    "serialize successfully to a GetPenaltyDetailsJson where only lateSubmissionPenalty is populated" in {
      Json.toJson(penaltyDetailsLSPModel) shouldBe penaltyDetailsLSPJson
    }

    "serialize successfully to a GetPenaltyDetailsJson where only latePaymentPenalty is populated" in {
      Json.toJson(penaltyDetailsLPPModel) shouldBe penaltyDetailsLPPJson
    }
    "serialize successfully to a GetPenaltyDetailsJson where both latePaymentPenalty and lateSubmissionPenalty are populated" in {
      Json.toJson(penaltyDetailsAllModel) shouldBe penaltyDetailsAllJson
    }
  }
}
