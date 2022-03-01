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

package models.API1812.lateSubmissionPenalty

import base.SpecBase
import play.api.libs.json.Json
import utils.TestConstantsAPI1812._

class LSPDetailsSpec extends SpecBase {

  "deserialize to JSON with all optional fields present" in {
    lspDetailsAllOptions.as[LSPDetails] shouldBe lspDetailsModelWithAllOptions
  }

  "deserialize to JSON with no optional fields present" in {
    lspDetailsWithNoOptions.as[LSPDetails] shouldBe lspDetailsModelWithNoOptions
  }

  "serialize to an lsp details model with all optional fields present" in {
    Json.toJson(lspDetailsModelWithAllOptions) shouldBe lspDetailsAllOptions
  }

  "serialize to a penalty model with all optional fields present" in {
    Json.toJson(lspDetailsModelWithNoOptions) shouldBe lspDetailsWithNoOptions
  }

}
