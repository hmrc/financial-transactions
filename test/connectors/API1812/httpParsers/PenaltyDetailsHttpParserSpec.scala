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
import connectors.API1812.httpParsers.PenaltyDetailsHttpParser.PenaltyDetailsReads
import models.API1812.{Error, PenaltyDetails}
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import utils.TestConstantsAPI1812.{LPPJsonMax, apiLPPJson, breathingSpaceJSONAfterBS, penaltyDetailsModelMax}

class PenaltyDetailsHttpParserSpec extends SpecBase {

  "The PenaltyDetailsHttpParser" when {

    "the http response status is 200 OK and contains relevant LPP and breathing space JSON" should {

      val httpResponse = HttpResponse(OK, apiLPPJson(LPPJsonMax, breathingSpaceJSONAfterBS).toString)
      val expected = Right(penaltyDetailsModelMax)
      val result = PenaltyDetailsReads.read("", "", httpResponse)

      "return a PenaltyDetails instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 200 OK with no LPP JSON" should {

      val httpResponse = HttpResponse(OK, Json.obj().toString)
      val expected = Right(PenaltyDetails(None, None))
      val result = PenaltyDetailsReads.read("", "", httpResponse)

      "return an empty PenaltyDetails instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 200 OK but the response is in an invalid format" should {

      val httpResponse = HttpResponse(OK, apiLPPJson(Json.obj("f" -> "f"), Json.obj("g" -> "g")).toString)
      val expected = Left(Error(BAD_REQUEST,
        "UNEXPECTED_JSON_FORMAT - The downstream service responded with json which did not match the expected format."))
      val result = PenaltyDetailsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 400 BAD_REQUEST (single error)" should {

      val httpResponse = HttpResponse(BAD_REQUEST, "ERROR MESSAGE")
      val expected = Left(Error(BAD_REQUEST, "ERROR MESSAGE"))
      val result = PenaltyDetailsReads.read("", "", httpResponse)

      "return a Error instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (Unexpected Json Returned)" should {

      val httpResponse = HttpResponse(BAD_REQUEST, Json.obj("foo" -> "bar").toString)
      val expected = Left(Error(BAD_REQUEST, """{"foo":"bar"}"""))
      val result = PenaltyDetailsReads.read("", "", httpResponse)

      "return a 400 BAD_REQUEST (Unexpected JSON returned)" in {
        result shouldEqual expected
      }
    }

    "the http response status is 400 BAD_REQUEST (Bad Json Returned)" should {

      val httpResponse = HttpResponse(BAD_REQUEST, "Banana")
      val expected = Left(Error(BAD_REQUEST,"Banana"))
      val result = PenaltyDetailsReads.read("", "", httpResponse)

      "return a 400 BAD_REQUEST (BAD Json returned)" in {
        result shouldEqual expected
      }
    }

    "the http response status is 500 ISE" should {

      val httpResponse = HttpResponse(INTERNAL_SERVER_ERROR, "message")
      val expected = Left(Error(code = INTERNAL_SERVER_ERROR, reason = "message"))
      val result = PenaltyDetailsReads.read("", "", httpResponse)

      "return an ISE" in {
        result shouldEqual expected
      }
    }

    "the http response status is NOT_FOUND" should {

      val httpResponse = HttpResponse(NOT_FOUND,"")
      val expected = Left(Error(NOT_FOUND,""))
      val result = PenaltyDetailsReads.read("", "", httpResponse)

      "return an NOT_FOUND " in {
        result shouldEqual expected
      }
    }
  }
}
