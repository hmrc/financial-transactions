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

class HttpErrorSpec extends SpecBase {

  "The HttpErrorModel" should {

    val httpErrorModel = HttpErrorModel("CODE","ERROR MESSAGE")
    val httpErrorJson: JsValue = Json.obj("code"->"CODE","message"->"ERROR MESSAGE")

    "Serialize to Json as expected" in {
      Json.toJson(httpErrorModel) shouldBe httpErrorJson
    }

    "Deserialize to a HttpErrorModel as expected" in {
      httpErrorJson.as[HttpErrorModel] shouldBe httpErrorModel
    }
  }

  "The UnexpectedStatusError" should {

    val unexpectedStatusError = UnexpectedStatusError(505)

    "have the correct code" in {
      unexpectedStatusError.status shouldBe 505
    }

    "have the error message 'Received an unexpected status code: 505.'" in {
      unexpectedStatusError.message shouldBe "Received an unexpected status code: 505."
    }

  }

  "The UnknownHttpError" should {

    "have the message 'Received an unknown error.'" in {
      UnknownHttpError.message shouldBe "Received an unknown error."
    }
  }

  "The ServerSideError" should {

    "have the message 'The server you connecting to returned an error.'" in {
      ServerSideError.message shouldBe "The server you connecting to returned an error."
    }
  }
}
