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

package mocks.services

import connectors.API1812.httpParsers.PenaltyDetailsHttpParser.PenaltyDetailsResponse
import models.{PenaltyDetailsQueryParameters, TaxRegime}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import org.scalatestplus.mockito.MockitoSugar.mock
import services.API1812.PenaltyDetailsService

import scala.concurrent.Future

trait MockPenaltyDetailsService {

  val mockPenaltyDetailsService: PenaltyDetailsService = mock[PenaltyDetailsService]

  def setupMockGetPenaltyDetails(regime: TaxRegime, queryParameters: PenaltyDetailsQueryParameters)
                                (response: PenaltyDetailsResponse): OngoingStubbing[Future[PenaltyDetailsResponse]] =
    when(
      mockPenaltyDetailsService.getPenaltyDetails(
        ArgumentMatchers.eq(regime),
        ArgumentMatchers.eq(queryParameters)
      )(ArgumentMatchers.any(), ArgumentMatchers.any())
    ).thenReturn(Future.successful(response))
}
