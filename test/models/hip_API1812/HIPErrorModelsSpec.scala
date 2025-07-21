/*
 * Copyright 2023 HM Revenue & Customs
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

package models.hip_API1812

import base.SpecBase
import play.api.libs.json.Json

class HIPErrorModelsSpec extends SpecBase {

  "HIPErrorResponse" should {
    "read from JSON with business error" in {
      val json = Json.obj(
        "errors" -> Json.obj(
          "processingDate" -> "2023-11-28T10:15:10Z",
          "code" -> "016",
          "text" -> "Invalid ID Number"
        )
      )

      val result = json.as[HIPErrorResponse]
      result.errors.processingDate shouldBe "2023-11-28T10:15:10Z"
      result.errors.code shouldBe "016"
      result.errors.text shouldBe "Invalid ID Number"
    }

    "read from JSON without processing date" in {
      val json = Json.obj(
        "errors" -> Json.obj(
          "processingDate" -> "2023-11-28T10:15:10Z",
          "code" -> "003",
          "text" -> "Request could not be processed"
        )
      )

      val result = json.as[HIPErrorResponse]
      result.errors.processingDate shouldBe "2023-11-28T10:15:10Z"
      result.errors.code shouldBe "003"
      result.errors.text shouldBe "Request could not be processed"
    }
  }

  "HIPBusinessError" should {
    "read from JSON with all fields" in {
      val json = Json.obj(
        "processingDate" -> "2023-11-28T10:15:10Z",
        "code" -> "002",
        "text" -> "Invalid Tax Regime"
      )

      val result = json.as[HIPBusinessError]
      result.processingDate shouldBe "2023-11-28T10:15:10Z"
      result.code shouldBe "002"
      result.text shouldBe "Invalid Tax Regime"
    }

    "read from JSON without processing date" in {
      val json = Json.obj(
        "processingDate" -> "2023-11-28T10:15:10Z",
        "code" -> "015",
        "text" -> "Invalid ID Type"
      )

      val result = json.as[HIPBusinessError]
      result.processingDate shouldBe "2023-11-28T10:15:10Z"
      result.code shouldBe "015"
      result.text shouldBe "Invalid ID Type"
    }
  }

  "HIPTechnicalErrorResponse" should {
    "read from JSON with technical error" in {
      val json = Json.obj(
        "error" -> Json.obj(
          "code" -> "500",
          "message" -> "Internal server error",
          "logID" -> "C0000AB8190C333200000002000007A6"
        )
      )

      val result = json.as[HIPTechnicalErrorResponse]
      result.error.code shouldBe "500"
      result.error.message shouldBe "Internal server error"
      result.error.logID shouldBe "C0000AB8190C333200000002000007A6"
    }

    "read from JSON without logID" in {
      val json = Json.obj(
        "error" -> Json.obj(
          "code" -> "400",
          "message" -> "Bad request",
          "logID" -> "log-123"
        )
      )

      val result = json.as[HIPTechnicalErrorResponse]
      result.error.code shouldBe "400"
      result.error.message shouldBe "Bad request"
      result.error.logID shouldBe "log-123"
    }
  }

  "HIPTechnicalError" should {
    "read from JSON with all fields" in {
      val json = Json.obj(
        "code" -> "500",
        "message" -> "Internal server error",
        "logID" -> "C0000AB8190C333200000002000007A6"
      )

      val result = json.as[HIPTechnicalError]
      result.code shouldBe "500"
      result.message shouldBe "Internal server error"
      result.logID shouldBe "C0000AB8190C333200000002000007A6"
    }

    "read from JSON without logID" in {
      val json = Json.obj(
        "code" -> "400",
        "message" -> "Bad request",
        "logID" -> "log-123"
      )

      val result = json.as[HIPTechnicalError]
      result.code shouldBe "400"
      result.message shouldBe "Bad request"
      result.logID shouldBe "log-123"
    }
  }

  "HIPWrappedErrorResponse" should {
    "read from JSON with wrapped errors" in {
      val json = Json.obj(
        "response" -> Json.arr(
          Json.obj(
            "type" -> "TechnicalError",
            "reason" -> "Service unavailable"
          ),
          Json.obj(
            "type" -> "ValidationError", 
            "reason" -> "Invalid parameters"
          )
        )
      )

      val result = json.as[HIPWrappedErrorResponse]
      result.response should have size 2
      result.response.head.`type` shouldBe "TechnicalError"
      result.response.head.reason shouldBe "Service unavailable"
      result.response(1).`type` shouldBe "ValidationError"
      result.response(1).reason shouldBe "Invalid parameters"
    }

    "read from JSON with single error" in {
      val json = Json.obj(
        "response" -> Json.arr(
          Json.obj(
            "type" -> "BusinessError",
            "reason" -> "No data found"
          )
        )
      )

      val result = json.as[HIPWrappedErrorResponse]
      result.response should have size 1
      result.response.head.`type` shouldBe "BusinessError"
      result.response.head.reason shouldBe "No data found"
    }
  }

  "HIPWrappedError" should {
    "read from JSON" in {
      val json = Json.obj(
        "type" -> "TechnicalError",
        "reason" -> "Service unavailable"
      )

      val result = json.as[HIPWrappedError]
      result.`type` shouldBe "TechnicalError"
      result.reason shouldBe "Service unavailable"
    }
  }
}

