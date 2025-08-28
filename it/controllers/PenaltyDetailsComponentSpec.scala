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

package controllers

import config.{MicroserviceAppConfig, RegimeKeys}
import helpers.ComponentSpecBase
import helpers.servicemocks.{EISPenaltyDetailsStub, HIPPenaltyDetailsStub}
import models.{PenaltyDetailsQueryParameters, VatRegime}
import play.api.Application
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.test.Helpers.running
import testData.PenaltyDetailsTestData


class PenaltyDetailsComponentSpec extends ComponentSpecBase {

  "Sending a request to /financial-transactions/penalty/:regime/:identifier (FinancialTransactions controller)" when {

    val vatRegime = VatRegime("123456789")
    lazy val queryParameters = PenaltyDetailsQueryParameters(dateLimit = Some("02"))

    "calling the IF API" when {
      val app: Application = buildApp(Map("feature-switch.call-api-1812-hip" -> "false"))
      "a successful response is returned by the API" should {

        "return a success response" in {
          running(app) { implicit app: Application =>
            isAuthorised()

            And("I wiremock stub a successful Get Penalty Details response")
            EISPenaltyDetailsStub.stubGetPenaltyDetails(
              vatRegime, queryParameters)(OK, PenaltyDetailsTestData.penaltyDetailsAPIJson)

            When(s"I call GET /financial-transactions/penalty/${RegimeKeys.VAT}/${vatRegime.id}")
            val res = PenaltyDetails.getPenaltyDetails(RegimeKeys.VAT, vatRegime.id, queryParameters)

            Then("a successful response is returned with expected JSON data")
            res should have(
              httpStatus(OK),
              jsonBodyAs(PenaltyDetailsTestData.penaltyDetailsWrittenJson)
            )
          }
        }
      }

      "the API returns a success response with breathing space but no penalty data" should {

        "return a success response with the breathing space data and no penalty data" in {
          running(app) { implicit app: Application =>
            isAuthorised()
            And("I wiremock stub a successful Get Penalty Details response")
            EISPenaltyDetailsStub.stubGetPenaltyDetails(
              vatRegime, queryParameters)(OK, PenaltyDetailsTestData.penaltyDetailsAPIJsonBSOnly)
            When(s"I call GET /financial-transactions/penalty/${RegimeKeys.VAT}/${vatRegime.id}")
            val res = PenaltyDetails.getPenaltyDetails(RegimeKeys.VAT, vatRegime.id, queryParameters)

            Then("a successful response is returned with a BS boolean and no penalty data")
            res should have(
              httpStatus(OK),
              jsonBodyAs(PenaltyDetailsTestData.penaltyDetailsWrittenJsonBSOnly)
            )
          }
        }
      }

      "the API returns a success response with empty JSON" should {

        "return a success response with the breathing space boolean" in {
          running(app) { implicit app: Application =>
          isAuthorised()
          And("I wiremock stub a successful Get Penalty Details response")
          EISPenaltyDetailsStub.stubGetPenaltyDetails(
            vatRegime, queryParameters)(OK, Json.obj())
          When(s"I call GET /financial-transactions/penalty/${RegimeKeys.VAT}/${vatRegime.id}")
          val res = PenaltyDetails.getPenaltyDetails(RegimeKeys.VAT, vatRegime.id, queryParameters)

          Then("a successful response is returned with a BS boolean and no penalty data")
          res should have(
            httpStatus(OK),
            jsonBodyAs(PenaltyDetailsTestData.penaltyDetailsWrittenJsonBSOnly)
          )

        }
      }
    }

      "an unsuccessful response is returned by the API" should {

        lazy val queryParameters = models.PenaltyDetailsQueryParameters()

        "return an error response" in {
          running(app) { implicit app: Application =>
            isAuthorised()

            And("I wiremock stub a bad request response from Get Financial Data")
            EISPenaltyDetailsStub.stubGetPenaltyDetails(vatRegime, queryParameters)(BAD_REQUEST, PenaltyDetailsTestData.errorJson)

            When(s"I call GET /financial-transactions/penalty/${RegimeKeys.VAT}/${vatRegime.id}")
            val res = PenaltyDetails.getPenaltyDetails(RegimeKeys.VAT, vatRegime.id, queryParameters)

            Then("an error is returned by the API with expected JSON data")
            res should have(
              httpStatus(BAD_REQUEST),
              jsonBodyAs[models.API1812.Error](PenaltyDetailsTestData.errorModel)
            )
          }
        }
      }
    }

    "calling the HIP API" when {
      val app: Application = buildApp(Map("feature-switch.call-api-1812-hip" -> "true"))
      "a successful response is returned by the API" should {
        "return a success response" in {
          running(app) { implicit app: Application =>
            isAuthorised()

            And("I wiremock stub a successful Get Penalty Details response")
            HIPPenaltyDetailsStub.stubGetPenaltyDetails(
              vatRegime, queryParameters)(OK, PenaltyDetailsTestData.penaltyDetailsAPIJson)

            When(s"I call GET /financial-transactions/penalty/${RegimeKeys.VAT}/${vatRegime.id}")
            val res = PenaltyDetails.getPenaltyDetails(RegimeKeys.VAT, vatRegime.id, queryParameters)

            Then("a successful response is returned with expected JSON data")
            res should have(
              httpStatus(OK),
              jsonBodyAs(PenaltyDetailsTestData.penaltyDetailsWrittenJson)
            )
          }
        }
      }

      "an unsuccessful response is returned by the API" should {

        lazy val queryParameters = models.PenaltyDetailsQueryParameters()

        "return an error response" in {
          running(app) { implicit app: Application =>
            isAuthorised()

            And("I wiremock stub a bad request response from Get Financial Data")
            HIPPenaltyDetailsStub.stubGetPenaltyDetails(vatRegime, queryParameters)(BAD_REQUEST, PenaltyDetailsTestData.errorJson)

            When(s"I call GET /financial-transactions/penalty/${RegimeKeys.VAT}/${vatRegime.id}")
            val res = PenaltyDetails.getPenaltyDetails(RegimeKeys.VAT, vatRegime.id, queryParameters)

            Then("an error is returned by the API with expected JSON data")
            res should have(
              httpStatus(BAD_REQUEST),
              jsonBodyAs[models.API1812.Error](PenaltyDetailsTestData.errorModel)
            )
          }
        }
      }
    }
  }
}


