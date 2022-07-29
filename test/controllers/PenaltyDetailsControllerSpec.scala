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

package controllers

import base.SpecBase
import config.RegimeKeys
import controllers.actions.AuthActionImpl
import mocks.auth.MockMicroserviceAuthorisedFunctions
import mocks.services.MockPenaltyDetailsService
import models.API1166.UnauthenticatedError
import models.API1812.Error
import models.{PenaltyDetailsQueryParameters, VatRegime}
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import utils.TestConstantsAPI1812.{LPPJsonMax, penaltyDetailsModelMax, writtenLPPJson}

class PenaltyDetailsControllerSpec extends SpecBase with MockPenaltyDetailsService with MockMicroserviceAuthorisedFunctions {

  val authActionImpl = new AuthActionImpl(mockAuth, controllerComponents)

  val controller = new PenaltyDetailsController(
    authActionImpl,
    mockPenaltyDetailsService,
    controllerComponents
  )

  val vrn = "123456789"
  val vatRegime: VatRegime = VatRegime(vrn)

  "The .getPenaltyDetails method" when {

    "an authenticated user requests details for the VAT regime" when {

      "the service returns a success response" should {

        lazy val result = {
          setupMockGetPenaltyDetails(vatRegime, PenaltyDetailsQueryParameters())(Right(penaltyDetailsModelMax))
          controller.getPenaltyDetails(RegimeKeys.VAT, vrn, PenaltyDetailsQueryParameters())(fakeRequest)
        }

        "return a status of 200 (OK)" in {
          status(result) shouldBe Status.OK
        }

        "return a json body with the penalty details information" in {
          contentAsJson(result) shouldBe writtenLPPJson(LPPJsonMax)
        }
      }

      "the service returns a failure response" should {

        val error = Error(Status.INSUFFICIENT_STORAGE, "ERROR MESSAGE")
        val errorJson = Json.obj("code" -> 507, "reason" -> "ERROR MESSAGE")
        lazy val result = {
          setupMockGetPenaltyDetails(vatRegime, PenaltyDetailsQueryParameters())(Left(error))
          controller.getPenaltyDetails(RegimeKeys.VAT, vrn, PenaltyDetailsQueryParameters())(fakeRequest)
        }

        "return the same status as the response" in {
          status(result) shouldBe Status.INSUFFICIENT_STORAGE
        }

        "return the correct error JSON" in {
          contentAsJson(result) shouldBe errorJson
        }
      }
    }

    "an unauthenticated user requests details" should {

      lazy val result = {
        setupMockAuthorisationException()
        controller.getPenaltyDetails(RegimeKeys.VAT, vrn, PenaltyDetailsQueryParameters())(fakeRequest)
      }

      "return a status of 401 (UNAUTHORIZED)" in {
        status(result) shouldBe Status.UNAUTHORIZED
      }

      "return the expected JSON" in {
        contentAsJson(result) shouldBe Json.toJson(UnauthenticatedError)
      }
    }
  }
}
