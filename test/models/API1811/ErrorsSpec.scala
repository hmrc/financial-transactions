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
import play.api.libs.json.{JsValue, Json}

class ErrorsSpec extends SpecBase {

  "The Error model" should {

    val desErrorModel = Error("CODE", "ERROR MESSAGE")
    val desErrorJson: JsValue = Json.obj("code"->"CODE", "reason"->"ERROR MESSAGE")

    "Serialize to Json as expected" in {
      Json.toJson(desErrorModel) shouldBe desErrorJson
    }

    "Deserialize to a Error as expected" in {
      desErrorJson.as[Error] shouldBe desErrorModel
    }
  }
}
