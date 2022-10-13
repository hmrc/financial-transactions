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

package models

import base.SpecBase
import play.api.libs.json.Json
import utils.API1166.TestConstants.{singleDirectDebits, singleDirectDebitsJson, multipleDirectDebits, multipleDirectDebitsJson
  , noDirectDebits, noDirectDebitsJson}

class DirectDebitsSpec extends SpecBase {

  "DirectDebits" should {

    "serialize to Json successfully for more than one direct debit" in {
      Json.toJson(multipleDirectDebits) shouldBe multipleDirectDebitsJson
    }

    "deserialize to a Direct debit model successfully for more than one direct debit" in {
      multipleDirectDebitsJson.as[DirectDebits] shouldBe multipleDirectDebits
    }

    "serialize to Json successfully for one direct debit" in {
      Json.toJson(singleDirectDebits) shouldBe singleDirectDebitsJson
    }

    "deserialize to a Direct debit model successfully one direct debit" in {
      singleDirectDebitsJson.as[DirectDebits] shouldBe singleDirectDebits
    }

    "serialize to Json successfully when there are no direct debits" in {
      Json.toJson(noDirectDebits) shouldBe noDirectDebitsJson
    }

    "deserialize to a Direct debit model successfully there are no direct debits" in {
      noDirectDebitsJson.as[DirectDebits] shouldBe noDirectDebits
    }

  }

}
