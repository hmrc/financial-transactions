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

package models.API1812.latePaymentPenalty

import base.SpecBase
import play.api.libs.json.Json
import utils.TestConstantsAPI1812._

class LatePaymentPenaltySpec extends SpecBase {

  "LatePaymentPenalty" should {

    "parse from JSON" when {

      "optional fields are present" in {
        LPPJsonMax.as[LatePaymentPenalty] shouldBe LPPModelMax
      }

      "optional fields are missing" in {
        LPPJsonMin.as[LatePaymentPenalty] shouldBe LPPModelMin
      }
    }

    "serialize to JSON" when {

      "optional fields are present" in {
        Json.toJson(LPPModelMax) shouldBe LPPJsonMax
      }

      "optional fields are missing" in {
        Json.toJson(LPPJsonMin) shouldBe LPPJsonMin
      }
    }
  }
}
