/*
 * Copyright 2025 HM Revenue & Customs
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

import base.SpecBase
import mocks.MockHttp
import models.API1812.{Error, PenaltyDetails}
import models.{PenaltyDetailsQueryParameters, VatRegime}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import play.api.http.Status._
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, RequestTimeoutException, StringContextOps}
import connectors.API1812.httpParsers.HIPPenaltyDetailsHttpParser.HIPPenaltyDetailsResponse

import scala.concurrent.{ExecutionContext, Future}

class HIPPenaltyDetailsConnectorSpec extends SpecBase with MockHttp {

  val connector = new HIPPenaltyDetailsConnector(mockHttpClientV2, mockAppConfig)
  val vatRegime: VatRegime = VatRegime("555555555")
  val queryParams: PenaltyDetailsQueryParameters = PenaltyDetailsQueryParameters()
  val queryParamsWithDateLimit: PenaltyDetailsQueryParameters = PenaltyDetailsQueryParameters(dateLimit = Some("12"))

  val testPenaltyDetails: PenaltyDetails = models.API1812.PenaltyDetails(LPPDetails = None, breathingSpace = None)
  val testSuccessResponse: Right[Nothing, PenaltyDetails] = Right(testPenaltyDetails)
  val testHttpResponse: HttpResponse = HttpResponse(OK, "{}")
  val testErrorResponse: Left[Error, Nothing] = Left(Error(INTERNAL_SERVER_ERROR, "Test error"))

  private def buildExpectedUrl(queryParameters: PenaltyDetailsQueryParameters): java.net.URL = {
    val urlString = connector.penaltyDetailsUrl()
    val hipQueryParams = Seq(
      "taxRegime" -> vatRegime.regimeType,
      "idType" -> vatRegime.idType,
      "idNumber" -> vatRegime.id
    ) ++ queryParameters.toSeqQueryParams
    url"$urlString?$hipQueryParams"
  }

  "The HIPPenaltyDetailsConnector" should {

    "construct the correct URL" in {
      connector.penaltyDetailsUrl() shouldBe s"${mockAppConfig.hipUrl}/etmp/RESTAdapter/cross-regime/taxpayer/penalties"
    }

    "make a successful call with correct headers and query parameters" in {
      val expectedUrl = buildExpectedUrl(queryParams)
      
      setupMockHttpGetV2(expectedUrl)(Future.successful(testSuccessResponse))
      
      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))
      
      result shouldBe testSuccessResponse
      verify(mockHttpClientV2).get(any[java.net.URL])(any[HeaderCarrier])
      verify(mockRequestBuilder).setHeader(any[(String, String)])
      verify(mockRequestBuilder).execute[HIPPenaltyDetailsResponse](any[HttpReads[HIPPenaltyDetailsResponse]], any[ExecutionContext])
    }

    "make a call without dateLimit when not provided" in {
      val expectedUrl = buildExpectedUrl(queryParams)
      
      setupMockHttpGetV2(expectedUrl)(Future.successful(testSuccessResponse))
      
      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))
      
      result shouldBe testSuccessResponse
    }

    "make a call with dateLimit when provided" in {
      val expectedUrl = buildExpectedUrl(queryParamsWithDateLimit)
      
      setupMockHttpGetV2(expectedUrl)(Future.successful(testSuccessResponse))
      
      val result = await(connector.getPenaltyDetails(vatRegime, queryParamsWithDateLimit))
      
      result shouldBe testSuccessResponse
    }

    "remove Authorization from HeaderCarrier and use custom headers" in {
      val expectedUrl = buildExpectedUrl(queryParams)
      
      setupMockHttpGetV2(expectedUrl)(Future.successful(testSuccessResponse))
      
      val headerCarrierWithAuth = HeaderCarrier(authorization = Some(uk.gov.hmrc.http.Authorization("Bearer token")))
      val result = await(connector.getPenaltyDetails(vatRegime, queryParams)(headerCarrierWithAuth, scala.concurrent.ExecutionContext.global))
      
      result shouldBe testSuccessResponse
      verify(mockHttpClientV2).get(any[java.net.URL])(any[HeaderCarrier])
    }

    "return successful response for 200 status" in {
      val expectedUrl = buildExpectedUrl(queryParams)
      
      setupMockHttpGetV2(expectedUrl)(Future.successful(testSuccessResponse))
      
      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))
      
      result shouldBe testSuccessResponse
    }

    "return error for 404 status with empty body" in {
      val expectedUrl = buildExpectedUrl(queryParams)
      val errorResponse = Left(Error(NOT_FOUND, ""))
      
      setupMockHttpGetV2(expectedUrl)(Future.successful(errorResponse))
      
      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))
      
      result shouldBe errorResponse
    }

    "return error for 400 status with technical error" in {
      val expectedUrl = buildExpectedUrl(queryParams)
      val errorResponse = Left(Error(BAD_REQUEST, "Bad request error"))
      
      setupMockHttpGetV2(expectedUrl)(Future.successful(errorResponse))
      
      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))
      
      result shouldBe errorResponse
    }

    "return error for 400 status with HIP wrapped error" in {
      val expectedUrl = buildExpectedUrl(queryParams)
      val errorResponse = Left(Error(BAD_REQUEST, "HIP wrapped error"))
      
      setupMockHttpGetV2(expectedUrl)(Future.successful(errorResponse))
      
      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))
      
      result shouldBe errorResponse
    }

    "return error for 422 status with Invalid ID Number" in {
      val expectedUrl = buildExpectedUrl(queryParams)
      val errorResponse = Left(Error(NOT_FOUND, "No penalty details found"))
      
      setupMockHttpGetV2(expectedUrl)(Future.successful(errorResponse))
      
      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))
      
      result shouldBe errorResponse
    }

    "return error for 422 status with Invalid Tax Regime" in {
      val expectedUrl = buildExpectedUrl(queryParams)
      val errorResponse = Left(Error(UNPROCESSABLE_ENTITY, "Invalid Tax Regime"))
      
      setupMockHttpGetV2(expectedUrl)(Future.successful(errorResponse))
      
      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))
      
      result shouldBe errorResponse
    }

    "return error for 422 status with Request could not be processed" in {
      val expectedUrl = buildExpectedUrl(queryParams)
      val errorResponse = Left(Error(UNPROCESSABLE_ENTITY, "Request could not be processed"))
      
      setupMockHttpGetV2(expectedUrl)(Future.successful(errorResponse))
      
      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))
      
      result shouldBe errorResponse
    }

    "return error for 422 status with Invalid ID Type" in {
      val expectedUrl = buildExpectedUrl(queryParams)
      val errorResponse = Left(Error(UNPROCESSABLE_ENTITY, "Invalid ID Type"))
      
      setupMockHttpGetV2(expectedUrl)(Future.successful(errorResponse))
      
      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))
      
      result shouldBe errorResponse
    }

    "return error for 422 status with Duplicate submission reference" in {
      val expectedUrl = buildExpectedUrl(queryParams)
      val errorResponse = Left(Error(UNPROCESSABLE_ENTITY, "Duplicate submission reference"))
      
      setupMockHttpGetV2(expectedUrl)(Future.successful(errorResponse))
      
      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))
      
      result shouldBe errorResponse
    }

    "return error for 500 status with technical error" in {
      val expectedUrl = buildExpectedUrl(queryParams)
      val errorResponse = Left(Error(INTERNAL_SERVER_ERROR, "Internal server error"))
      
      setupMockHttpGetV2(expectedUrl)(Future.successful(errorResponse))
      
      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))
      
      result shouldBe errorResponse
    }

    "return error for 500 status with HIP wrapped error" in {
      val expectedUrl = buildExpectedUrl(queryParams)
      val errorResponse = Left(Error(INTERNAL_SERVER_ERROR, "HIP wrapped error"))
      
      setupMockHttpGetV2(expectedUrl)(Future.successful(errorResponse))
      
      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))
      
      result shouldBe errorResponse
    }

    "return error for 503 status" in {
      val expectedUrl = buildExpectedUrl(queryParams)
      val errorResponse = Left(Error(SERVICE_UNAVAILABLE, "Service unavailable"))
      
      setupMockHttpGetV2(expectedUrl)(Future.successful(errorResponse))
      
      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))
      
      result shouldBe errorResponse
    }

    "return error for unexpected response" in {
      val expectedUrl = buildExpectedUrl(queryParams)
      val errorResponse = Left(Error(418, "I'm a teapot"))
      
      setupMockHttpGetV2(expectedUrl)(Future.successful(errorResponse))
      
      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))
      
      result shouldBe errorResponse
    }

    "return a 502 error when there is a HTTP exception" in {
      val exception = new RequestTimeoutException("Request timed out!!!")
      val expectedUrl = buildExpectedUrl(queryParams)
      
      setupMockHttpGetV2(expectedUrl)(Future.failed(exception))
      
      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))
      
      result shouldBe Left(Error(BAD_GATEWAY, exception.message))
    }
  }
}