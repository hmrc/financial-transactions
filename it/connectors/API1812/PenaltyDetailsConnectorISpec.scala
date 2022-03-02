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

package connectors.API1812

import connectors.API1812.httpParsers.PenaltyDetailsHttpParser.PenaltyDetailsResponse
import helpers.ComponentSpecBase
import models.API1812.Error
import models.{PenaltyDetailsQueryParameters, TaxRegime, VatRegime}
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND, OK, REQUEST_TIMEOUT}
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import testData.PenaltyDetailsTestData.{fullPenaltyDetailsJson, fullPenaltyDetailsModel}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

class PenaltyDetailsConnectorISpec extends ComponentSpecBase {

  val connector: PenaltyDetailsConnector = new PenaltyDetailsConnector(httpClient, appConfig)
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  val regimeType = "VATC"
  val invalidRegimeType = "9"
  val vatRegime: TaxRegime = VatRegime(id = "123456789")
  val invalidRegime: TaxRegime = VatRegime(id = "9")
  val vrn = "123456789"
  val invalidVrn = "9"
  val queryParameters: PenaltyDetailsQueryParameters = PenaltyDetailsQueryParameters()

  def generateUrl(regimeType: String, VRN : String): String =  s"/penalty/details/$regimeType/VRN/$VRN"

  "getPenaltyDetails" should {

    "return a PenaltyDetails model" when {

      s"an $OK response is received and the response can be parsed" in {

        stubGetRequest(
          generateUrl(regimeType, vrn),
          OK,
          fullPenaltyDetailsJson.toString()
        )

        val expectedResult = fullPenaltyDetailsModel

        val result: PenaltyDetailsResponse =
          await(connector.getPenaltyDetails(vatRegime,queryParameters))
        result shouldBe Right(expectedResult)

      }

      "return an ErrorModel" when {

        s"a $OK response is received, but the response cannot be parsed" in {

          stubGetRequest(
            generateUrl(regimeType, vrn),
            OK,
            """{"lateSubmissionPenalty":"complete and utter nonsense"}"""
          )

          val expectedResult = Left(Error(BAD_REQUEST,
            "UNEXPECTED_JSON_FORMAT - The downstream service responded with json which did not match the expected format."))

          val result: PenaltyDetailsResponse =
            await(connector.getPenaltyDetails(vatRegime, queryParameters))
          result shouldBe expectedResult

        }
        s"a $BAD_REQUEST response is received" in {

          stubGetRequest(
            generateUrl(regimeType, invalidVrn),
            BAD_REQUEST,
            Json.obj("code" -> 400,
              "reason" -> "BAD REQUEST").toString()
          )

          val expectedResult = Left(Error(BAD_REQUEST,
            """{"code":400,"reason":"BAD REQUEST"}"""))

          val result: PenaltyDetailsResponse =
            await(connector.getPenaltyDetails(invalidRegime, queryParameters))
          result shouldBe expectedResult
        }
        s"a $NOT_FOUND response is received" in {

          stubGetRequest(
            generateUrl(regimeType, vrn),
            NOT_FOUND,
            Json.obj("code" -> 404,
              "reason" -> "A not found error has been received").toString()
          )

          val expectedResult = Left(Error(NOT_FOUND,
            """{"code":404,"reason":"A not found error has been received"}"""))

          val result: PenaltyDetailsResponse =
            await(connector.getPenaltyDetails(vatRegime, queryParameters))
          result shouldBe expectedResult
        }
        "an unexpected response is received" in {

          stubGetRequest(
            generateUrl(regimeType, vrn),
            REQUEST_TIMEOUT,
            "AN UNKNOWN ERROR HAS OCCURRED"
          )

          val expectedResult = Left(Error( REQUEST_TIMEOUT, "AN UNKNOWN ERROR HAS OCCURRED"))

          val result: PenaltyDetailsResponse =
            await(connector.getPenaltyDetails(vatRegime, queryParameters))
          result shouldBe expectedResult
        }
      }
    }

  }

}
