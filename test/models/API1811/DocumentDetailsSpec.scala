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

import play.api.libs.json.Json
import utils.TestConstantsAPI1811._
import base.SpecBase

class DocumentDetailsSpec  extends SpecBase{

  "DocumentDetails" should {
    "serialize to Json successfully" in {
      Json.toJson(fullDocumentDetails) shouldBe fullDocumentDetailsJson
    }

    "deserialize to a Transaction model successfully" in {
      fullDocumentDetailsJson.as[DocumentDetails] shouldBe fullDocumentDetails
    }
  }

}