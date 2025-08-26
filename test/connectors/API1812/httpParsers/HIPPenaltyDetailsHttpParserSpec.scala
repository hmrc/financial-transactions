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

package connectors.API1812.httpParsers

import base.SpecBase
import connectors.API1812.httpParsers.HIPPenaltyDetailsHttpParser.HIPPenaltyDetailsReads
import models.API1812.{Error, PenaltyDetails}
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import utils.TestConstantsAPI1812.{apiLPPJsonHIP, breathingSpaceJSONAfterBS, hipLppJsonMax, penaltyDetailsModelMax}

class HIPPenaltyDetailsHttpParserSpec extends SpecBase {

  "The HIPPenaltyDetailsHttpParser" when {

    "the http response status is 200 OK and contains relevant LPP and breathing space JSON" should {

      val httpResponse = HttpResponse(OK, apiLPPJsonHIP(hipLppJsonMax, breathingSpaceJSONAfterBS).toString)
      val expected = Right(penaltyDetailsModelMax)
      val result = HIPPenaltyDetailsReads.read("", "", httpResponse)

      "return a PenaltyDetails instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 200 OK with no penalty data" should {

      val httpResponse = HttpResponse(OK, Json.obj(
        "success" -> Json.obj(
          "processingDate" -> "2023-11-28T10:15:10Z"
        )
      ).toString)
      val expected = Right(PenaltyDetails(None, None))
      val result = HIPPenaltyDetailsReads.read("", "", httpResponse)

      "return an empty PenaltyDetails instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 200 OK but the response is in an invalid format" should {

      val httpResponse = HttpResponse(OK, Json.obj(
        "success" -> Json.obj(
          "processingDate" -> "2023-11-28T10:15:10Z",
          "penaltyData" -> Json.obj(
            "lpp" -> Json.obj(
              "lppDetails" -> "invalid content"
            )
          )
        )
      ).toString)
      val expected = Left(Error(INTERNAL_SERVER_ERROR,
        "UNEXPECTED_JSON_FORMAT - The downstream service responded with json which did not match the expected format."))
      val result = HIPPenaltyDetailsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat error" in {
        result shouldEqual expected
      }
    }

    "the http response status is 404 NOT_FOUND with empty body" should {

      val httpResponse = HttpResponse(NOT_FOUND, "")
      val expected = Left(Error(NOT_FOUND, ""))
      val result = HIPPenaltyDetailsReads.read("", "", httpResponse)

      "should return a NOT_FOUND error with no data found message" in {
        result shouldEqual expected
      }
    }

    "the http response status is 404 NOT_FOUND with any body" should {

      val httpResponse = HttpResponse(NOT_FOUND, Json.obj(
        "errors" -> Json.obj(
          "code" -> "016",
          "text" -> "Invalid ID Number"
        )
      ).toString)
      val expected = Left(Error(NOT_FOUND, httpResponse.body))
      val result = HIPPenaltyDetailsReads.read("", "", httpResponse)

      "should return a NOT_FOUND error with URL not found message (404 means URL is wrong)" in {
        result shouldEqual expected
      }
    }

    "the http response status is 204 NO_CONTENT" should {

      val httpResponse = HttpResponse(NO_CONTENT, "")
      val expected = Left(Error(NOT_FOUND, "No penalty details found"))
      val result = HIPPenaltyDetailsReads.read("", "", httpResponse)

      "return a NOT_FOUND error with no data found message" in {
        result shouldEqual expected
      }
    }

    "the http response status is 400 BAD_REQUEST with technical error" should {

      val httpResponse = HttpResponse(BAD_REQUEST, Json.obj(
        "error" -> Json.obj(
          "code" -> "400",
          "message" -> "Bad request error",
          "logID" -> "log-123"
        )
      ).toString)
      val expected = Left(Error(BAD_REQUEST, "Bad request error"))
      val result = HIPPenaltyDetailsReads.read("", "", httpResponse)

      "return a BAD_REQUEST error with the technical error message" in {
        result shouldEqual expected
      }
    }

    "the http response status is 422 UNPROCESSABLE_ENTITY with business error code 016" should {

      val httpResponse = HttpResponse(UNPROCESSABLE_ENTITY, Json.obj(
        "errors" -> Json.obj(
          "processingDate" -> "2023-11-28T10:15:10Z",
          "code" -> "016",
          "text" -> "Invalid ID Number"
        )
      ).toString)
      val expected = Left(Error(NOT_FOUND, "No penalty details found"))
      val result = HIPPenaltyDetailsReads.read("", "", httpResponse)

      "return a NOT_FOUND error with no data found message (422 with 016 means no data)" in {
        result shouldEqual expected
      }
    }

    "the http response status is 422 UNPROCESSABLE_ENTITY with other business error" should {

      val httpResponse = HttpResponse(UNPROCESSABLE_ENTITY, Json.obj(
        "errors" -> Json.obj(
          "processingDate" -> "2023-11-28T10:15:10Z",
          "code" -> "002",
          "text" -> "Invalid Tax Regime"
        )
      ).toString)
      val expected = Left(Error(UNPROCESSABLE_ENTITY, "Invalid Tax Regime"))
      val result = HIPPenaltyDetailsReads.read("", "", httpResponse)

      "return a UNPROCESSABLE_ENTITY error with the business error text" in {
        result shouldEqual expected
      }
    }

    "the http response status is 500 INTERNAL_SERVER_ERROR with HIP wrapped error" should {

      val httpResponse = HttpResponse(INTERNAL_SERVER_ERROR, Json.obj(
        "origin" -> "HIP",
        "response" -> Json.obj(
          "failures" -> Json.arr(
            Json.obj(
              "type" -> "TechnicalError",
              "reason" -> "Service unavailable"
            )
          )
        )
      ).toString)
      val expected = Left(Error(INTERNAL_SERVER_ERROR, "Service unavailable"))
      val result = HIPPenaltyDetailsReads.read("", "", httpResponse)

      "return an INTERNAL_SERVER_ERROR with the HIP wrapped error reason" in {
        result shouldEqual expected
      }
    }

    "the http response status is 503 SERVICE_UNAVAILABLE with double-wrapped error" should {

      val httpResponse = HttpResponse(SERVICE_UNAVAILABLE, Json.obj(
        "origin" -> "HIP",
        "response" -> Json.obj(
          "failures" -> Json.arr(
            Json.obj(
              "type" -> "TechnicalError",
              "reason" -> "Service temporarily unavailable"
            )
          )
        )
      ).toString)
      val expected = Left(Error(SERVICE_UNAVAILABLE, "Service temporarily unavailable"))
      val result = HIPPenaltyDetailsReads.read("", "", httpResponse)

      "return a SERVICE_UNAVAILABLE with the double-wrapped error reason" in {
        result shouldEqual expected
      }
    }

    "the http response status is 503 SERVICE_UNAVAILABLE with double-wrapped error and multiple failures" should {

      val httpResponse = HttpResponse(SERVICE_UNAVAILABLE, Json.obj(
        "origin" -> "HIP",
        "response" -> Json.obj(
          "failures" -> Json.arr(
            Json.obj(
              "type" -> "TechnicalError",
              "reason" -> "Primary error"
            ),
            Json.obj(
              "type" -> "BusinessError", 
              "reason" -> "Secondary error"
            )
          )
        )
      ).toString)
      val expected = Left(Error(SERVICE_UNAVAILABLE, "Primary error"))
      val result = HIPPenaltyDetailsReads.read("", "", httpResponse)

      "return a SERVICE_UNAVAILABLE with the first failure reason" in {
        result shouldEqual expected
      }
    }

    "the http response status is 503 SERVICE_UNAVAILABLE with double-wrapped error but empty failures" should {

      val responseBody = Json.obj(
        "origin" -> "HIP",
        "response" -> Json.obj(
          "failures" -> Json.arr()
        )
      ).toString
      val httpResponse = HttpResponse(SERVICE_UNAVAILABLE, responseBody)
      val expected = Left(Error(SERVICE_UNAVAILABLE, responseBody))
      val result = HIPPenaltyDetailsReads.read("", "", httpResponse)

      "return a SERVICE_UNAVAILABLE with the response body when no failures" in {
        result shouldEqual expected
      }
    }

    "the http response status is 503 SERVICE_UNAVAILABLE with unrecognized error format" should {

      val httpResponse = HttpResponse(SERVICE_UNAVAILABLE, "Unrecognized error format")
      val expected = Left(Error(SERVICE_UNAVAILABLE, "Unrecognized error format"))
      val result = HIPPenaltyDetailsReads.read("", "", httpResponse)

      "return a SERVICE_UNAVAILABLE error with the response body" in {
        result shouldEqual expected
      }
    }

    "the http response status is 403 FORBIDDEN" should {

      val httpResponse = HttpResponse(FORBIDDEN, "Forbidden")
      val expected = Left(Error(FORBIDDEN, "Forbidden"))
      val result = HIPPenaltyDetailsReads.read("", "", httpResponse)

      "return a FORBIDDEN error" in {
        result shouldEqual expected
      }
    }

    "the http response status is 409 CONFLICT" should {

      val httpResponse = HttpResponse(CONFLICT, "Conflict")
      val expected = Left(Error(CONFLICT, "Conflict"))
      val result = HIPPenaltyDetailsReads.read("", "", httpResponse)

      "return a CONFLICT error" in {
        result shouldEqual expected
      }
    }
  }
}

