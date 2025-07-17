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

package connectors.API1812

import connectors.API1812.httpParsers.PenaltyDetailsHttpParser.PenaltyDetailsResponse
import helpers.ComponentSpecBase
import helpers.servicemocks.HIPPenaltyDetailsStub
import models.API1812.Error
import models.{PenaltyDetailsQueryParameters, TaxRegime, VatRegime}
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND, OK, REQUEST_TIMEOUT}
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import testData.HIPPenaltyDetailsTestData.{penaltyDetailsAPIJson, penaltyDetailsModel}

import connectors.API1812.HIPPenaltyDetailsConnector

class HIPPenaltyDetailsConnectorISpec extends ComponentSpecBase {

  val connector: HIPPenaltyDetailsConnector = new HIPPenaltyDetailsConnector(httpClient, appConfig)

  val vatRegime: TaxRegime = VatRegime(id = "123456789")
  val queryParameters: PenaltyDetailsQueryParameters = PenaltyDetailsQueryParameters()
  val url: String = "/RESTAdapter/cross-regime/taxpayer/penalties?taxRegime=VATC&idType=VRN&idNumber=123456789"

  "getPenaltyDetails" should {

    "return a PenaltyDetails model" when {

      s"an $OK response is received and the response can be parsed" in {

        HIPPenaltyDetailsStub.stubGetPenaltyDetails(
          vatRegime, queryParameters)(OK, penaltyDetailsAPIJson)

        val expectedResult = penaltyDetailsModel
        val result: PenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

        result shouldBe Right(expectedResult)
      }
    }

    "return an ErrorModel" when {

      s"a $OK response is received, but the response is in an unexpected format" in {

        HIPPenaltyDetailsStub.stubGetPenaltyDetails(
          vatRegime, queryParameters)(OK, Json.obj(
            "success" -> Json.obj(
              "processingDate" -> "2023-11-28T10:15:10Z",
              "penaltyData" -> Json.obj(
                "lpp" -> Json.obj(
                  "lppDetails" -> "invalid content"
                )
              )
            )
          ))

        val expectedResult = Left(Error(BAD_REQUEST,
          "UNEXPECTED_JSON_FORMAT - The downstream service responded with json which did not match the expected format."))
        val result: PenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

        result shouldBe expectedResult
      }

      s"a $BAD_REQUEST response is received" in {

        HIPPenaltyDetailsStub.stubGetPenaltyDetails(
          vatRegime, queryParameters)(BAD_REQUEST, Json.obj("code" -> BAD_REQUEST, "reason" -> "BAD REQUEST"))

        val expectedResult = Left(Error(BAD_REQUEST, """{"code":400,"reason":"BAD REQUEST"}"""))
        val result: PenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

        result shouldBe expectedResult
      }

      s"a $NOT_FOUND response is received" in {

        HIPPenaltyDetailsStub.stubGetPenaltyDetails(
          vatRegime, queryParameters)(NOT_FOUND, Json.obj("code" -> NOT_FOUND, "reason" -> "A not found error has been received"))

        val expectedResult = Left(Error(NOT_FOUND, """{"code":404,"reason":"A not found error has been received"}"""))
        val result: PenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

        result shouldBe expectedResult
      }

      "an unexpected response is received" in {

        HIPPenaltyDetailsStub.stubGetPenaltyDetails(
          vatRegime, queryParameters)(REQUEST_TIMEOUT, Json.obj("error" -> "AN UNKNOWN ERROR HAS OCCURRED"))

        val expectedResult = Left(Error(REQUEST_TIMEOUT, """{"error":"AN UNKNOWN ERROR HAS OCCURRED"}"""))
        val result: PenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

        result shouldBe expectedResult
      }
    }
  }
}
