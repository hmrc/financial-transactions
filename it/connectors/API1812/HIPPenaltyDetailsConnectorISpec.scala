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

import connectors.API1812.httpParsers.HIPPenaltyDetailsHttpParser.HIPPenaltyDetailsResponse
import helpers.ComponentSpecBase
import helpers.servicemocks.HIPPenaltyDetailsStub
import models.API1812.Error
import models.{PenaltyDetailsQueryParameters, TaxRegime, VatRegime}
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import testData.HIPPenaltyDetailsTestData.{penaltyDetailsAPIJson, penaltyDetailsModel}

class HIPPenaltyDetailsConnectorISpec extends ComponentSpecBase {

  val connector: HIPPenaltyDetailsConnector = new HIPPenaltyDetailsConnector(httpClient, appConfig)

  val vatRegime: TaxRegime = VatRegime(id = "123456789")
  val queryParameters: PenaltyDetailsQueryParameters = PenaltyDetailsQueryParameters()
  val queryParametersWithDateLimit: PenaltyDetailsQueryParameters = PenaltyDetailsQueryParameters(dateLimit = Some("12"))

  "getPenaltyDetails" should {

    "return a PenaltyDetails model" when {

      "a 200 OK response is received with valid penalty data and headers are validated" in {
        HIPPenaltyDetailsStub.stubGetPenaltyDetailsWithHeaderValidation(
          vatRegime, queryParameters)(OK, penaltyDetailsAPIJson)

        val expectedResult = penaltyDetailsModel
        val result: HIPPenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

        result shouldBe Right(expectedResult)
      }

      "a 200 OK response is received with dateLimit parameter" in {
        HIPPenaltyDetailsStub.stubGetPenaltyDetailsWithHeaderValidation(
          vatRegime, queryParametersWithDateLimit)(OK, penaltyDetailsAPIJson)

        val expectedResult = penaltyDetailsModel
        val result: HIPPenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParametersWithDateLimit))

        result shouldBe Right(expectedResult)
      }

      "a 200 OK response is received with no penalty data" in {
        HIPPenaltyDetailsStub.stubGetPenaltyDetailsWithHeaderValidation(
          vatRegime, queryParameters)(OK, HIPPenaltyDetailsStub.noDataSuccessResponse())

        val result: HIPPenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

        result.isRight shouldBe true
        result.map(_.LPPDetails) shouldBe Right(None)
        result.map(_.breathingSpace) shouldBe Right(None)
      }
    }

    "return an Error model" when {

      "a 200 OK response is received but with invalid JSON format" in {
        val invalidJson = Json.obj(
          "success" -> Json.obj(
            "processingDate" -> "2023-11-28T10:15:10Z",
            "penaltyData" -> Json.obj(
              "lpp" -> Json.obj(
                "lppDetails" -> "invalid content"
              )
            )
          )
        )

        HIPPenaltyDetailsStub.stubGetPenaltyDetailsWithHeaderValidation(
          vatRegime, queryParameters)(OK, invalidJson)

        val expectedResult = Left(Error(BAD_REQUEST,
          "UNEXPECTED_JSON_FORMAT - The downstream service responded with json which did not match the expected format."))
        val result: HIPPenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

        result shouldBe expectedResult
      }

      "a 400 BAD_REQUEST response is received with technical error" in {
        val technicalError = HIPPenaltyDetailsStub.technicalErrorResponse("400", "Invalid JSON message content used")

        HIPPenaltyDetailsStub.stubGetPenaltyDetailsWithHeaderValidation(
          vatRegime, queryParameters)(BAD_REQUEST, technicalError)

        val result: HIPPenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

        result.isLeft shouldBe true
        result.left.map(_.code) shouldBe Left(BAD_REQUEST)
        result.left.map(_.reason) shouldBe Left("Invalid JSON message content used")
      }

             "a 400 BAD_REQUEST response is received with HIP wrapped error" in {
         val wrappedError = HIPPenaltyDetailsStub.hipWrappedErrorResponse("Type of Failure", "Reason for Failure")

         HIPPenaltyDetailsStub.stubGetPenaltyDetailsWithHeaderValidation(
           vatRegime, queryParameters)(BAD_REQUEST, wrappedError)

         val result: HIPPenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

         result.isLeft shouldBe true
         result.left.map(_.code) shouldBe Left(BAD_REQUEST)
         result.left.map(_.reason) shouldBe Left("Reason for Failure")
       }

             "a 404 NOT_FOUND response is received with empty body" in {
         HIPPenaltyDetailsStub.stubGetPenaltyDetailsEmptyBody(
           vatRegime, queryParameters)(NOT_FOUND)

         val result: HIPPenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

         result.isLeft shouldBe true
         result.left.map(_.code) shouldBe Left(NOT_FOUND)
         result.left.map(_.reason) shouldBe Left("No penalty details found")
       }

      "a 404 NOT_FOUND response is received with Invalid ID Number (code 016)" in {
        val businessError = HIPPenaltyDetailsStub.businessErrorResponse("016", "Invalid ID Number")

        HIPPenaltyDetailsStub.stubGetPenaltyDetailsWithHeaderValidation(
          vatRegime, queryParameters)(NOT_FOUND, businessError)

        val expectedResult = Left(Error(NOT_FOUND, "No penalty details found"))
        val result: HIPPenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

        result shouldBe expectedResult
      }

      "a 422 UNPROCESSABLE_ENTITY response is received with Invalid Tax Regime (code 002)" in {
        val businessError = HIPPenaltyDetailsStub.businessErrorResponse("002", "Invalid Tax Regime")

        HIPPenaltyDetailsStub.stubGetPenaltyDetailsWithHeaderValidation(
          vatRegime, queryParameters)(UNPROCESSABLE_ENTITY, businessError)

        val result: HIPPenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

        result.isLeft shouldBe true
        result.left.map(_.code) shouldBe Left(UNPROCESSABLE_ENTITY)
        result.left.map(_.reason) shouldBe Left("Invalid Tax Regime")
      }

      "a 422 UNPROCESSABLE_ENTITY response is received with Request could not be processed (code 003)" in {
        val businessError = HIPPenaltyDetailsStub.businessErrorResponse("003", "Request could not be processed")

        HIPPenaltyDetailsStub.stubGetPenaltyDetailsWithHeaderValidation(
          vatRegime, queryParameters)(UNPROCESSABLE_ENTITY, businessError)

        val result: HIPPenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

        result.isLeft shouldBe true
        result.left.map(_.code) shouldBe Left(UNPROCESSABLE_ENTITY)
        result.left.map(_.reason) shouldBe Left("Request could not be processed")
      }

      "a 422 UNPROCESSABLE_ENTITY response is received with Invalid ID Type (code 015)" in {
        val businessError = HIPPenaltyDetailsStub.businessErrorResponse("015", "Invalid ID Type")

        HIPPenaltyDetailsStub.stubGetPenaltyDetailsWithHeaderValidation(
          vatRegime, queryParameters)(UNPROCESSABLE_ENTITY, businessError)

        val result: HIPPenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

        result.isLeft shouldBe true
        result.left.map(_.code) shouldBe Left(UNPROCESSABLE_ENTITY)
        result.left.map(_.reason) shouldBe Left("Invalid ID Type")
      }

      "a 422 UNPROCESSABLE_ENTITY response is received with Duplicate submission reference (code 135)" in {
        val businessError = HIPPenaltyDetailsStub.businessErrorResponse("135", "Duplicate submission reference")

        HIPPenaltyDetailsStub.stubGetPenaltyDetailsWithHeaderValidation(
          vatRegime, queryParameters)(UNPROCESSABLE_ENTITY, businessError)

        val result: HIPPenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

        result.isLeft shouldBe true
        result.left.map(_.code) shouldBe Left(UNPROCESSABLE_ENTITY)
        result.left.map(_.reason) shouldBe Left("Duplicate submission reference")
      }

      "a 500 INTERNAL_SERVER_ERROR response is received with technical error" in {
        val technicalError = HIPPenaltyDetailsStub.technicalErrorResponse("500", "Error while sending message to module processor")

        HIPPenaltyDetailsStub.stubGetPenaltyDetailsWithHeaderValidation(
          vatRegime, queryParameters)(INTERNAL_SERVER_ERROR, technicalError)

        val result: HIPPenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

        result.isLeft shouldBe true
        result.left.map(_.code) shouldBe Left(INTERNAL_SERVER_ERROR)
        result.left.map(_.reason) shouldBe Left("Error while sending message to module processor")
      }

             "a 500 INTERNAL_SERVER_ERROR response is received with HIP wrapped error" in {
         val wrappedError = HIPPenaltyDetailsStub.hipWrappedErrorResponse("Type of Failure", "Internal Server Error")

         HIPPenaltyDetailsStub.stubGetPenaltyDetailsWithHeaderValidation(
           vatRegime, queryParameters)(INTERNAL_SERVER_ERROR, wrappedError)

         val result: HIPPenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

         result.isLeft shouldBe true
         result.left.map(_.code) shouldBe Left(INTERNAL_SERVER_ERROR)
         result.left.map(_.reason) shouldBe Left("Internal Server Error")
       }

      "a 503 SERVICE_UNAVAILABLE response is received" in {
        val serviceUnavailableError = HIPPenaltyDetailsStub.hipWrappedErrorResponse("Service Unavailable", "The service is currently unavailable")

        HIPPenaltyDetailsStub.stubGetPenaltyDetailsWithHeaderValidation(
          vatRegime, queryParameters)(SERVICE_UNAVAILABLE, serviceUnavailableError)

        val result: HIPPenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

        result.isLeft shouldBe true
        result.left.map(_.code) shouldBe Left(SERVICE_UNAVAILABLE)
        result.left.map(_.reason) shouldBe Left("The service is currently unavailable")
      }

      "an unexpected response status is received" in {
        HIPPenaltyDetailsStub.stubGetPenaltyDetailsWithHeaderValidation(
          vatRegime, queryParameters)(REQUEST_TIMEOUT, Json.obj("error" -> "AN UNKNOWN ERROR HAS OCCURRED"))

        val expectedResult = Left(Error(REQUEST_TIMEOUT, """{"error":"AN UNKNOWN ERROR HAS OCCURRED"}"""))
        val result: HIPPenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

        result shouldBe expectedResult
      }
    }

    "send the correct headers" when {
      
      "making any request" in {
        HIPPenaltyDetailsStub.stubGetPenaltyDetailsWithHeaderValidation(
          vatRegime, queryParameters)(OK, penaltyDetailsAPIJson)

        val result: HIPPenaltyDetailsResponse = await(connector.getPenaltyDetails(vatRegime, queryParameters))

        result.isRight shouldBe true
      }
    }
  }
}
