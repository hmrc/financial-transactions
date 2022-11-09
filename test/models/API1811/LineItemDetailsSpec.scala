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

  "LineItemDetails" when {

    "maximum fields are present" should {

      "deserialise from JSON correctly" in {
        lineItemDetailsFullJson.as[LineItemDetails] shouldBe lineItemDetailsFull
      }
    }

    "minimum fields are present" should {

      "deserialise from JSON correctly" in {
        val emptyModel = LineItemDetails(None, None, None, None, None, None, None, None, None, None, None, None)
        Json.obj("" -> "").as[LineItemDetails] shouldBe emptyModel
      }
    }

    "some correct fields are present but some are unrecognised" should {

      "read the recognised fields correctly" in {
        lineItemDetailsFullIncorrectFieldsJson.as[LineItemDetails] shouldBe lineItemDetailsFullIncorrectFields
      }
    }

  }

  "LineItemDetails json writes" should {

    "write a LineItemDetails model to maximum JSON successfully" when {

      "all fields are present" in {
        Json.toJson(lineItemDetailsFull) shouldBe Json.obj("chargeType" -> "VAT Return Debit Charge")
      }
    }

    "not include a chargeType field in the output JSON" should {

      "main transaction is not present" in {
        Json.toJson(lineItemDetailsFull.copy(mainTransaction = None)) shouldBe Json.obj()
      }

      "sub transaction is not present" in {
        Json.toJson(lineItemDetailsFull.copy(subTransaction = None)) shouldBe Json.obj()
      }

      "main transaction and sub transaction are not present" in {
        Json.toJson(lineItemDetailsFull.copy(mainTransaction = None, subTransaction = None)) shouldBe Json.obj()
      }
    }
  }
}
