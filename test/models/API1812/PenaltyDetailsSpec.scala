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

  "PenaltyDetails" should {

    "parse JSON to an empty sequence when no LPP details exist" in {
      val result = Json.obj().as[PenaltyDetails]
      result.LPPDetails.isEmpty shouldBe true
    }

    "parse a JSON array of LPP details to a sequence correctly" when {

      "optional fields are present" in {
        apiLPPJson(LPPJsonMax).as[PenaltyDetails] shouldBe penaltyDetailsModelMax
      }

      "optional fields are missing" in {
        apiLPPJson(LPPJsonMin).as[PenaltyDetails] shouldBe penaltyDetailsModelMin
      }
    }

    "serialize to JSON" when {

      "optional fields are present" in {
        Json.toJson(penaltyDetailsModelMax) shouldBe writtenLPPJson(LPPJsonMax)
      }

      "optional fields are missing" in {
        Json.toJson(penaltyDetailsModelMin) shouldBe writtenLPPJson(LPPJsonMin)
      }
    }
  }
}
