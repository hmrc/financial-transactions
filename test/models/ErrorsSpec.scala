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

class ErrorsSpec extends SpecBase {

  "The Error model" should {

    val desErrorModel = Error("CODE","ERROR MESSAGE")
    val desErrorJson: JsValue = Json.obj("code"->"CODE","reason"->"ERROR MESSAGE")

    "Serialize to Json as expected" in {
      Json.toJson(desErrorModel) shouldBe desErrorJson
    }

    "Deserialize to a Error as expected" in {
      desErrorJson.as[Error] shouldBe desErrorModel
    }
  }

  "The MultiError model" should {

    val desMultiErrorModel = MultiError(failures = Seq(
      Error("CODE 1","ERROR MESSAGE 1"),
      Error("CODE 2","ERROR MESSAGE 2")
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

    "Deserialize to a MultiError as expected" in {
      desMultiErrorJson.as[MultiError] shouldBe desMultiErrorModel
    }

  }

  "The UnexpectedResponse object" should {

//    "Have the error code 'UNEXPECTED_DES_RESPONSE'" in {
//      UnexpectedResponse.code shouldBe "UNEXPECTED_DES_RESPONSE"
//    }
//
//    "Have the error reason 'The DES response did not match the expected format'" in {
//      UnexpectedResponse.reason shouldBe "The DES response did not match the expected format"
//    }

  }
}
