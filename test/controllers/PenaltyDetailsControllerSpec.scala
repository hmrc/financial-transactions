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
import controllers.actions.AuthActionImpl
import mocks.auth.MockMicroserviceAuthorisedFunctions
import mocks.services.MockPenaltyDetailsService
import models.API1166._
import models.API1811.{Error => Error1811}
import models.{RequestQueryParameters, _}
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import utils.TestConstantsAPI1811.{fullFinancialTransactions => fullFinancialTransactions1811}

class PenaltyDetailsControllerSpec extends SpecBase
  with MockPenaltyDetailsService
  with MockMicroserviceAuthorisedFunctions {

  val singleError: Error = Error(code = "CODE", reason = "ERROR MESSAGE")
  val multiError: MultiError = MultiError(
    failures = Seq(
      Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
      Error(code = "ERROR CODE 2", reason = "ERROR MESSAGE 2")
    )
  )

  val authActionImpl = new AuthActionImpl(mockAuth, controllerComponents)

  object TestPenaltyDetailsController extends PenaltyDetailsController(
    authActionImpl,
    mockPenaltyDetailsService,
    controllerComponents,
    mockAppConfig
  )

  "The GET PenaltyDetailsController.getPenaltyDetails method" when {

    val badRequestError = Error1811(Status.BAD_REQUEST, "error")
    val successResponse = Right(fullPenaltyDetails)
    val errorResponse = Left(badRequestError)

    "an authenticated user requests VAT details" when {

      val id = "123456"
      val vatRegime = VatRegime(id)

      "the service returns a success response" should {

        lazy val result = {
          setupMockGetPenaltyTransactions(vatRegime, PenaltyDetailsQueryParameters())(successResponse)
          TestPenaltyDetailsController.getPenaltyDetails(
            id, PenaltyDetailsQueryParameters()
          )(fakeRequest)
        }

        "return a status of 200 (OK)" in {
          status(result) shouldBe Status.OK
        }

        "return a json body with the financial transaction information" in {
          contentAsJson(result) shouldBe Json.toJson(fullFinancialTransactions1811)
        }
      }

      "the service returns a failure response" should {

        lazy val result = {
          setupMockGetPenaltyTransactions(vatRegime, PenaltyDetailsQueryParameters())(errorResponse)
          TestPenaltyDetailsController.getPenaltyDetails(
            id, PenaltyDetailsQueryParameters()
          )(fakeRequest)
        }

        "return the same status as the response" in {
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return the correct error JSON" in {
          contentAsJson(result) shouldBe Json.toJson(badRequestError)
        }
      }
    }

    "an authenticated user requests details for an invalid tax regime" should {

      val id = "123456"
      val vatRegime = VatRegime(id)

      lazy val result = {
        setupMockGetPenaltyTransactions(vatRegime, PenaltyDetailsQueryParameters())(errorResponse)
        TestPenaltyDetailsController.getPenaltyDetails(
          id, PenaltyDetailsQueryParameters()
        )(fakeRequest)
      }

      "return a status of 400 (BAD_REQUEST)" in {
        status(result) shouldBe Status.BAD_REQUEST
      }

      "return a json body with an Invalid Tax Regime message" in {
        contentAsJson(result) shouldBe Json.toJson(InvalidTaxRegime)
      }
    }
  }
}
