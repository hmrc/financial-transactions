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
import utils.API1811.TestConstants._

class LineItemDetailsSpec extends SpecBase {

  "LineItemDetails" should {

    "read from JSON" when {

      "maximum fields are present" in {
        lineItemDetailsFullJson.as[LineItemDetails] shouldBe lineItemDetailsFull
      }

      "minimum fields are present" in {
        Json.obj("" -> "").as[LineItemDetails] shouldBe emptyLineItem
      }

      "some correct fields are present but some are unrecognised" in {
        lineItemDetailsFullIncorrectFieldsJson.as[LineItemDetails] shouldBe lineItemDetailsFullIncorrectFields
      }
    }

    "write to JSON" when {

      "maximum fields are present" in {
        Json.toJson(lineItemDetailsFull) shouldBe fullLineItemDetailsOutputJson
      }

      "minimum fields are present" in {
        Json.toJson(emptyLineItem) shouldBe Json.obj()
      }
    }
  }
}
