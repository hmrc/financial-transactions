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
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.Mockito.{verify, when}
import play.api.http.Status._
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, RequestTimeoutException}

import scala.concurrent.Future

class HIPPenaltyDetailsConnectorSpec extends SpecBase with MockHttp {

  val connector = new HIPPenaltyDetailsConnector(mockHttpGet, mockAppConfig)
  val vatRegime: VatRegime = VatRegime("555555555")
  val queryParams: PenaltyDetailsQueryParameters = PenaltyDetailsQueryParameters()
  val queryParamsWithDateLimit: PenaltyDetailsQueryParameters = PenaltyDetailsQueryParameters(dateLimit = Some("12"))

  val testPenaltyDetails: PenaltyDetails = models.API1812.PenaltyDetails(LPPDetails = None, breathingSpace = None)
  val testSuccessResponse: Right[Nothing, PenaltyDetails] = Right(testPenaltyDetails)
  val testHttpResponse: HttpResponse = HttpResponse(OK, "{}")
  val testErrorResponse: Left[Error, Nothing] = Left(Error(INTERNAL_SERVER_ERROR, "Test error"))

  "The HIPPenaltyDetailsConnector" should {

    "construct the correct URL" in {
      connector.penaltyDetailsUrl() shouldBe s"${mockAppConfig.hipUrl}/etmp/RESTAdapter/cross-regime/taxpayer/penalties"
    }

    "make a successful call with correct headers and query parameters" in {
      when(mockHttpGet.GET[Either[Error, _]](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(testSuccessResponse))

      val headerCaptor = ArgumentCaptor.forClass(classOf[Seq[(String, String)]])
      val queryParamCaptor = ArgumentCaptor.forClass(classOf[Seq[(String, String)]])

      await(connector.getPenaltyDetails(vatRegime, queryParamsWithDateLimit))

      verify(mockHttpGet).GET[Either[Error, _]](
        meq(s"${mockAppConfig.hipUrl}/etmp/RESTAdapter/cross-regime/taxpayer/penalties"),
        queryParamCaptor.capture(),
        headerCaptor.capture()
      )(any(), any(), any())

      val capturedHeaders = headerCaptor.getValue
      val capturedQueryParams = queryParamCaptor.getValue

      capturedHeaders should contain("Authorization" -> s"Basic ${mockAppConfig.hipToken}")
      capturedHeaders.exists(_._1 == "correlationid") shouldBe true
      capturedHeaders should contain("X-Originating-System" -> "MDTP")
      capturedHeaders should contain("X-Transmitting-System" -> "HIP")
      capturedHeaders.exists(_._1 == "X-Receipt-Date") shouldBe true

      val correlationId = capturedHeaders.find(_._1 == "correlationid").map(_._2).get
      correlationId should fullyMatch regex "^[0-9a-fA-F]{8}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{12}$"

      val receiptDate = capturedHeaders.find(_._1 == "X-Receipt-Date").map(_._2).get
      receiptDate should fullyMatch regex "^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z$"

      capturedQueryParams should contain("taxRegime" -> "VATC")
      capturedQueryParams should contain("idType" -> "VRN")
      capturedQueryParams should contain("idNumber" -> "555555555")
      capturedQueryParams should contain("dateLimit" -> "12")
    }

    "make a call without dateLimit when not provided" in {
      when(mockHttpGet.GET[Either[Error, _]](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(testSuccessResponse))

      val queryParamCaptor = ArgumentCaptor.forClass(classOf[Seq[(String, String)]])

      await(connector.getPenaltyDetails(vatRegime, queryParams))

      verify(mockHttpGet).GET[Either[Error, _]](
        any(),
        queryParamCaptor.capture(),
        any()
      )(any(), any(), any())

      val capturedQueryParams = queryParamCaptor.getValue

      capturedQueryParams should contain("taxRegime" -> "VATC")
      capturedQueryParams should contain("idType" -> "VRN")
      capturedQueryParams should contain("idNumber" -> "555555555")
      capturedQueryParams.exists(_._1 == "dateLimit") shouldBe false
    }

    "remove Authorization from HeaderCarrier and use custom headers" in {
      val customHeaderCarrier = HeaderCarrier(authorization = Some(uk.gov.hmrc.http.Authorization("Bearer original-token")))
      
      when(mockHttpGet.GET[Either[Error, _]](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(testSuccessResponse))

      val headerCarrierCaptor = ArgumentCaptor.forClass(classOf[HeaderCarrier])

      await(connector.getPenaltyDetails(vatRegime, queryParams)(customHeaderCarrier, ec))

      verify(mockHttpGet).GET[Either[Error, _]](
        any(),
        any(),
        any()
      )(any(), headerCarrierCaptor.capture(), any())

      val capturedHeaderCarrier = headerCarrierCaptor.getValue
      capturedHeaderCarrier.authorization shouldBe None
    }

    "return successful response for 200 status" in {
      when(mockHttpGet.GET[Either[Error, _]](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(testSuccessResponse))

      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))

      result shouldBe testSuccessResponse
    }

    "return error for 404 status with empty body" in {
      when(mockHttpGet.GET[Either[Error, _]](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Left(Error(NOT_FOUND, "No penalty details found"))))

      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))

      result shouldBe Left(Error(NOT_FOUND, "No penalty details found"))
    }

    "return error for 404 status with Invalid ID Number (code 016)" in {
      when(mockHttpGet.GET[Either[Error, _]](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Left(Error(NOT_FOUND, "No penalty details found"))))

      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))

      result shouldBe Left(Error(NOT_FOUND, "No penalty details found"))
    }

    "return error for 400 Bad Request" in {
      val error = Error(BAD_REQUEST, "Bad Request")
      when(mockHttpGet.GET[Either[Error, _]](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Left(error)))

      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))

      result shouldBe Left(error)
    }

    "return error for 422 Unprocessable Entity" in {
      val error = Error(UNPROCESSABLE_ENTITY, "Request could not be processed")
      when(mockHttpGet.GET[Either[Error, _]](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Left(error)))

      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))

      result shouldBe Left(error)
    }

    "return error for 500 Internal Server Error (unexpected JSON format)" in {
      val error = Error(INTERNAL_SERVER_ERROR, "UNEXPECTED_JSON_FORMAT - The downstream service responded with json which did not match the expected format.")
      when(mockHttpGet.GET[Either[Error, _]](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Left(error)))

      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))

      result shouldBe Left(error)
    }

    "return error for 503 Service Unavailable" in {
      val error = Error(SERVICE_UNAVAILABLE, "Service Unavailable")
      when(mockHttpGet.GET[Either[Error, _]](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Left(error)))

      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))

      result shouldBe Left(error)
    }

    "handle HTTP exceptions and return 502 error" in {
      val exception = new RequestTimeoutException("Request timed out!!!")
      when(mockHttpGet.GET[Either[Error, _]](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.failed(exception))

      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))

      result shouldBe Left(Error(BAD_GATEWAY, exception.message))
    }

    "log warnings for unexpected errors that are not 404" in {
      val error = Error(INTERNAL_SERVER_ERROR, "Unexpected error")
      when(mockHttpGet.GET[Either[Error, _]](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Left(error)))

      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))

      result shouldBe Left(error)
    }

    "not log warnings for 404 errors" in {
      val error = Error(NOT_FOUND, "No penalty details found")
      when(mockHttpGet.GET[Either[Error, _]](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Left(error)))

      val result = await(connector.getPenaltyDetails(vatRegime, queryParams))

      result shouldBe Left(error)
    }
  }
}

