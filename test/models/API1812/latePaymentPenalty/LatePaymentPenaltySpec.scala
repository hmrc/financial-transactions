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

class LatePaymentPenaltySpec extends SpecBase{

  "serialize to JSON with no appeal status and appeal level" in {
    lppWithoutAppealStatusAndLevel.as[LatePaymentPenalty] shouldBe modelWithoutAppealStatusAndLevel
  }

  "serialize to JSON with an appeal status and appeal level" in {
   lppWithAppealStatusAndLevel.as[LatePaymentPenalty] shouldBe modelWithAppealStatusAndLevel
  }

  "deserialize to a penalty model with no appeal status and no appeal level" in {
    Json.toJson(modelWithoutAppealStatusAndLevel) shouldBe lppWithoutAppealStatusAndLevel
  }

  "deserialize to a penalty model with appeal status and appeal level" in {
    Json.toJson(modelWithAppealStatusAndLevel) shouldBe lppWithAppealStatusAndLevel
  }

}
