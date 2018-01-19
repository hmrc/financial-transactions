/*
 * Copyright 2017 HM Revenue & Customs
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
import play.api.libs.json.{JsValue, Json}

class DesErrorsSpec extends SpecBase {

  "The DesError model" should {

    val desErrorModel = DesError("CODE","ERROR MESSAGE")
    val desErrorJson: JsValue = Json.obj("code"->"CODE","reason"->"ERROR MESSAGE")

    "Serialize to Json as expected" in {
      Json.toJson(desErrorModel) shouldBe desErrorJson
    }

    "Deserialize to a DesError as expected" in {
      desErrorJson.as[DesError] shouldBe desErrorModel
    }
  }

  "The DesMultiError model" should {

    val desMultiErrorModel = DesMultiError(failures = Seq(
      DesError("CODE 1","ERROR MESSAGE 1"),
      DesError("CODE 2","ERROR MESSAGE 2")
    ))
    val desMultiErrorJson: JsValue =
      Json.obj("failures" ->
        Json.arr(
          Json.obj(
            "code" -> "CODE 1",
            "reason"->"ERROR MESSAGE 1"
          ),
          Json.obj(
            "code" -> "CODE 2",
            "reason"->"ERROR MESSAGE 2"
          )
        )
      )

    "Serialize to Json as expected" in {
      Json.toJson(desMultiErrorModel) shouldBe desMultiErrorJson
    }

    "Deserialize to a DesMultiError as expected" in {
      desMultiErrorJson.as[DesMultiError] shouldBe desMultiErrorModel
    }

  }

  "The UnexpectedDesResponse object" should {

    "Have the error code 'UNEXPECTED_DES_RESPONSE'" in {
      UnexpectedDesResponse.code shouldBe "UNEXPECTED_DES_RESPONSE"
    }

    "Have the error reason 'The DES response did not match the expected format'" in {
      UnexpectedDesResponse.reason shouldBe "The DES response did not match the expected format"
    }

  }
}
