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

package services.API1812

import base.SpecBase
import mocks.connectors.MockPenaltyDetailsConnector
import models.API1812.Error
import models.{PenaltyDetailsQueryParameters, TaxRegime, VatRegime}
import play.api.http.Status
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.TestConstantsAPI1812.{penaltyDetailsModelMax, penaltyDetailsModelNoPen}

class PenaltyDetailsServiceSpec extends SpecBase with MockPenaltyDetailsConnector {

  "The .getPenaltyDetails method" when {

    val queryParams: PenaltyDetailsQueryParameters = PenaltyDetailsQueryParameters(dateLimit = Some(2))
    val regime: TaxRegime = VatRegime("123456789")
    val service = new PenaltyDetailsService(mockPenaltyDetailsConnector)

    "the connector returns a success response with LPP and breathing space items in the array" should {

      "return the same response" in {
        val successResponse = Right(penaltyDetailsModelMax)
        setupPenaltyDetailsCall(regime, queryParams)(successResponse)
        val actual = await(service.getPenaltyDetails(regime, queryParams))

        actual shouldBe successResponse
      }
    }

    "the connector returns a success response with no LPP and breathing space items" should {

      "return the same response" in {
        val successResponse = Right(penaltyDetailsModelNoPen)
        setupPenaltyDetailsCall(regime, queryParams)(successResponse)
        val actual = await(service.getPenaltyDetails(regime, queryParams))

        actual shouldBe successResponse
      }
    }

    "the connector returns a failure response" should {

      "return the same response" in {
        val failureResponse = Left(Error(Status.INTERNAL_SERVER_ERROR, "error"))
        setupPenaltyDetailsCall(regime, queryParams)(failureResponse)
        val actual = await(service.getPenaltyDetails(regime, queryParams))

        actual shouldBe failureResponse
      }
    }
  }
}
