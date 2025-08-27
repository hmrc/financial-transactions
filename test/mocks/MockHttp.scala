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

package mocks

import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads}

import scala.concurrent.{ExecutionContext, Future}

trait MockHttp extends AnyWordSpecLike with Matchers with OptionValues with BeforeAndAfterEach with MockitoSugar {

  val mockHttpClientV2: HttpClientV2 = mock[HttpClientV2]
  val mockRequestBuilder: RequestBuilder = mock[RequestBuilder]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockHttpClientV2)
    reset(mockRequestBuilder)
  }

  def setupMockHttpGetV2[A](url: java.net.URL)(response: Future[A]): Unit = {
    when(mockHttpClientV2.get(ArgumentMatchers.eq(url))(any[HeaderCarrier]))
      .thenReturn(mockRequestBuilder)
    when(mockRequestBuilder.setHeader(any[(String, String)]))
      .thenReturn(mockRequestBuilder)
    when(mockRequestBuilder.execute[A](any[HttpReads[A]], any[ExecutionContext]))
      .thenReturn(response)
  }

  def setupMockHttpPostV2[I, O](url: java.net.URL)(response: Future[O]): Unit = {
    when(mockHttpClientV2.post(ArgumentMatchers.eq(url))(any[HeaderCarrier]))
      .thenReturn(mockRequestBuilder)
    when(mockRequestBuilder.setHeader(any[(String, String)]))
      .thenReturn(mockRequestBuilder)
    when(mockRequestBuilder.withBody(any[I])(any(), any(), any()))
      .thenReturn(mockRequestBuilder)
    when(mockRequestBuilder.execute[O](any[HttpReads[O]], any[ExecutionContext]))
      .thenReturn(response)
  }
}
