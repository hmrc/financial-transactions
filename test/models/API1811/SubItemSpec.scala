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

package models.API1811

import base.SpecBase
import play.api.libs.json.Json
import utils.TestConstantsAPI1811.{fullSubItem, fullSubItemJsonEIS, fullSubItemJsonEISOutput}

class SubItemSpec extends SpecBase {

  "SubItem" should {

    "serialize to Json successfully" in {
      Json.toJson(fullSubItem) shouldBe fullSubItemJsonEISOutput
    }

    "deserialize to a SubItem model successfully" in {
      fullSubItemJsonEIS.as[SubItem] shouldBe fullSubItem
    }
  }
}
