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
import play.api.libs.json.{JsError, Json}
import utils.TestConstantsAPI1811.fullFinancialTransactions
import utils.TestConstantsAPI1812._

class GetPenaltyDetailsSpec extends SpecBase {

  "Get Penalty Details" should {
    "return empty json when no LSP/LPP details exist" in {
      val result = Json.fromJson(Json.parse("{}"))(GetPenaltyDetails.format)
      result.isSuccess shouldBe true
      result.get.latePaymentPenalty.isEmpty shouldBe true
      result.get.lateSubmissionPenalty.isEmpty shouldBe true
    }

    "deserialize successfully where only latePaymentPenalty is populated" in {
      getPenaltyDetailsLPPJson.as[GetPenaltyDetails] shouldBe getPenaltyDetailsLPPModel
    }

    "deserialize successfully to a GetPenaltyDetailsModel where only lateSubmissionPenalty is populated" in {
      getPenaltyDetailsLSPJson.as[GetPenaltyDetails] shouldBe getPenaltyDetailsLSPModel
    }

    "deserialize successfully to a GetPenaltyDetailsModel where both latePaymentPenalty and lateSubmissionPenalty are populated" in {
      getPenaltyDetailsJAllson.as[GetPenaltyDetails] shouldBe getPenaltyDetailsAllModel
    }

    "be writable to JSON" when {
      "no LSP/LPP details exist - return empty JSON" in {
        Json.toJson(getPenaltyDetailsNone) shouldBe Json.obj()
      }
    }
    "serialize successfully to a GetPenaltyDetailsJson where only lateSubmissionPenalty is populated" in {
      Json.toJson(getPenaltyDetailsLSPModel) shouldBe getPenaltyDetailsLSPJson
    }

    "serialize successfully to a GetPenaltyDetailsJson where only latePaymentPenalty is populated" in {
      Json.toJson(getPenaltyDetailsLPPModel) shouldBe getPenaltyDetailsLPPJson
    }
    "serialize successfully to a GetPenaltyDetailsJson where both latePaymentPenalty and lateSubmissionPenalty are populated" in {
      Json.toJson(getPenaltyDetailsAllModel) shouldBe getPenaltyDetailsJAllson
    }
  }
}
