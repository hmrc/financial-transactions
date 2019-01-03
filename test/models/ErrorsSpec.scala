/*
 * Copyright 2019 HM Revenue & Customs
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
import play.api.http.Status
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

    "Have the error status ISE (500)'" in {
      UnexpectedResponse.status shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "Have the error reason 'The downstream service responded with an unexpected response.'" in {
      UnexpectedResponse.error shouldBe Error(
        code = "UNEXPECTED_DOWNSTREAM_ERROR",
        reason = "The downstream service responded with an unexpected response.")
    }

  }

  "The InvalidJsonResponse object" should {

    "Have the error status ISE (500)'" in {
      InvalidJsonResponse.status shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "Have the error reason 'The response did not contain valid json.'" in {
      InvalidJsonResponse.error shouldBe Error(
        code = "INVALID_JSON",
        reason = "The downstream service responded with invalid json.")
    }

  }

  "The UnexpectedJsonFormat object" should {

    "Have the error status ISE (500)'" in {
      UnexpectedJsonFormat.status shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "Have the error reason 'The DES response did not match the expected format'" in {
      UnexpectedJsonFormat.error shouldBe Error(
        code = "UNEXPECTED_JSON_FORMAT",
        reason = "The downstream service responded with json which did not match the expected format.")
    }

  }

  "The InvalidTaxRegime object" should {

    "Have the error status BAD_REQUEST (400)'" in {
      InvalidTaxRegime.code shouldBe "INVALID_TAX_REGIME"
    }

    "Have the error reason 'The supplied Tax Regime is invalid.'" in {
      InvalidTaxRegime.reason shouldBe "The supplied Tax Regime is invalid."
    }
  }

  "The UnauthenticatedError object" should {

    "Have the error status UNAUTHORIZED (401)'" in {
      UnauthenticatedError.code shouldBe "UNAUTHENTICATED"
    }

    "Have the error reason 'Not authenticated'" in {
      UnauthenticatedError.reason shouldBe "Not authenticated"
    }
  }

  "The ForbiddenError object" should {

    "Have the error status FORBIDDEN (403)'" in {
      ForbiddenError.code shouldBe "UNAUTHORISED"
    }

    "Have the error reason 'Not authorised'" in {
      ForbiddenError.reason shouldBe "Not authorised"
    }
  }
}
