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

package mocks.services

import connectors.API1396.httpParsers.DirectDebitCheckHttpParser.HttpGetResult
import models.API1396.DirectDebits
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.mockito.MockitoSugar.mock
import services.API1396.DirectDebitService

import scala.concurrent.Future

trait MockDirectDebitService extends AnyWordSpecLike with Matchers with OptionValues with BeforeAndAfterEach {

  val mockDirectDebitService: DirectDebitService = mock[DirectDebitService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDirectDebitService)
  }

  def setupMockCheckDirectDebitExists(vrn: String)
                                       (response: HttpGetResult[DirectDebits]):
                            OngoingStubbing[Future[HttpGetResult[DirectDebits]]] =
    when(
      mockDirectDebitService.checkDirectDebitExists(
        ArgumentMatchers.eq(vrn)
      )(ArgumentMatchers.any(), ArgumentMatchers.any())
    ).thenReturn(Future.successful(response))
}
