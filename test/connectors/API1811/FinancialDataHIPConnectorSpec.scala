/*
 * Copyright 2026 HM Revenue & Customs
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

package connectors.API1811

import base.SpecBase
import mocks.MockHttp
import models.API1811.{DocumentDetails, Error, FinancialData}
import models.{FinancialRequestQueryParameters, VatRegime}
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.verify
import play.api.http.Status.{BAD_GATEWAY, BAD_REQUEST, INTERNAL_SERVER_ERROR}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.{HttpException, StringContextOps}

import scala.concurrent.Future

class FinancialDataHIPConnectorSpec extends SpecBase with MockHttp {

  val connector = new FinancialDataHIPConnector(mockHttpClientV2)

  private val postFinancialDataUrl = s"http://localhost:9156/etmp/RESTAdapter/cross-regime/taxpayer/financial-data/query"

  "getFinancialData" should {
    "return the FinancialData in a Right" when {
      "HttpClientV2 returns a success response" in {
        val successResponse = Right(FinancialData(Seq.empty[DocumentDetails]))

        setupMockHttpPostV2(url"$postFinancialDataUrl")(Future.successful(successResponse))

        val result = await(connector.getFinancialData(VatRegime("123456"), FinancialRequestQueryParameters()))

        result shouldBe successResponse
      }
    }

    "return the API error in a left" when {
      "HttpClientV2 returns an API error response" in {
        val apiErrorResponse = Left(Error(BAD_REQUEST, "Request was bad"))

        setupMockHttpPostV2(url"$postFinancialDataUrl")(Future.successful(apiErrorResponse))

        val result = await(connector.getFinancialData(VatRegime("123456"), FinancialRequestQueryParameters()))

        result shouldBe apiErrorResponse
      }
    }

    "return a 502 Error in a Left with the error message" when {
      "HttpClientV2 returns an HttpException" in {
        val exception = new HttpException(responseCode = INTERNAL_SERVER_ERROR, message = "Upstream failure")

        setupMockHttpPostV2(url"$postFinancialDataUrl")(Future.failed(exception))

        val result = await(connector.getFinancialData(VatRegime("123456"), FinancialRequestQueryParameters()))

        result shouldBe Left(Error(BAD_GATEWAY, "Upstream failure"))
      }
    }

    "send the expected HIP headers" in {
      setupMockHttpPostV2(url"$postFinancialDataUrl")(Future.successful(Right(FinancialData(Seq.empty))))

      await(connector.getFinancialData(VatRegime("123456"), FinancialRequestQueryParameters()))

      val headersCaptor = ArgumentCaptor.forClass(classOf[Seq[(String, String)]])

      verify(mockRequestBuilder).setHeader(headersCaptor.capture(): _*)

      val headers = headersCaptor.getValue.toMap

      headers("Authorization") shouldBe s"Basic ${mockAppConfig.hipToken}"
      headers("X-Originating-System") shouldBe "MDTP"
      headers("X-Transmitting-System") shouldBe "HIP"

      headers should contain key "correlationid"
      headers should contain key "X-Receipt-Date"
    }
  }

}
