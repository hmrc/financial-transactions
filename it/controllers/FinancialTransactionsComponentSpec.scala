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

import config.RegimeKeys
import helpers.ComponentSpecBase
import helpers.servicemocks.{DesFinancialDataStub, EISFinancialDataStub}
import models.API1166._
import models.{IncomeTaxRegime, RequestQueryParameters, VatRegime}
import play.api.http.Status._
import play.api.libs.json.Json
import testData.{FinancialData1166, FinancialData1811}

class FinancialTransactionsComponentSpec extends ComponentSpecBase {

  "Sending a request to /financial-transactions/:regime/:identifier (FinancialTransactions controller)" when {

    "the useApi1811 feature switch is enabled" when {

      val vatRegime = VatRegime("123456789")

      "a successful response is returned by the API" should {

        lazy val queryParameters = RequestQueryParameters(onlyOpenItems = Some(true))

        "return a success response" in {

          appConfig.features.useApi1811(true)
          isAuthorised()

          And("I wiremock stub a successful Get Financial Data response")
          EISFinancialDataStub.stubGetFinancialData(
            vatRegime, queryParameters)(OK, FinancialData1811.fullFinancialTransactionsJsonEIS)

          When(s"I call GET /financial-transactions/${RegimeKeys.VAT}/${vatRegime.id}")
          val res = FinancialTransactions.getFinancialTransactions(RegimeKeys.VAT, vatRegime.id, queryParameters)

          Then("a successful response is returned with expected JSON data")
          res should have(
            httpStatus(OK),
            jsonBodyAs(FinancialData1811.fullFinancialTransactionsJsonOutput)
          )
        }
      }

      "an unsuccessful response is returned by the API" should {

        lazy val queryParameters = RequestQueryParameters()

        "return a single error response" in {

          isAuthorised()

          And("I wiremock stub a bad request response from Get Financial Data")
          EISFinancialDataStub.stubGetFinancialData(vatRegime, queryParameters)(BAD_REQUEST, FinancialData1811.errorJson)

          When(s"I call GET /financial-transactions/${RegimeKeys.VAT}/${vatRegime.id}")
          val res = FinancialTransactions.getFinancialTransactions(RegimeKeys.VAT, vatRegime.id, queryParameters)

          Then("an error is returned by the API with expected JSON data")
          res should have(
            httpStatus(BAD_REQUEST),
            jsonBodyAs[models.API1811.Error](FinancialData1811.errorModel)
          )
        }
      }
    }

    "the useApi1811 feature switch is disabled" when {

      "requesting Income Tax transactions" when {

        lazy val mtditid = "XAIT000000123456"
        lazy val incomeTaxRegime = IncomeTaxRegime(mtditid)

        "a successful response is returned by the API" should {

          lazy val queryParameters = RequestQueryParameters()

          "return a success response" in {

            appConfig.features.useApi1811(false)
            isAuthorised()

            And("I wiremock stub a successful Get Financial Data response")
            DesFinancialDataStub.stubGetFinancialData(
              incomeTaxRegime, queryParameters)(OK, Json.toJson(FinancialData1166.successResponse))

            When(s"I call GET /financial-transactions/it/$mtditid")
            val res = FinancialTransactions.getFinancialTransactions(RegimeKeys.IT, incomeTaxRegime.id, queryParameters)

            DesFinancialDataStub.verifyGetDesBusinessDetails(incomeTaxRegime, queryParameters)

            Then("a successful response is returned with the correct estimate")

            res should have(
              httpStatus(OK),
              jsonBodyAs[models.API1166.FinancialTransactions](FinancialData1166.successResponse)
            )
          }
        }

        "a bad request response is returned by the API, containing one error" should {

          lazy val queryParameters = RequestQueryParameters()

          "return a single error response" in {

            isAuthorised()

            And("I wiremock stub a successful Get Financial Data response")
            DesFinancialDataStub.stubGetFinancialData(
              incomeTaxRegime, queryParameters)(BAD_REQUEST, Json.toJson(FinancialData1166.singleErrorResponse))

            When(s"I call GET /financial-transactions/it/$mtditid")
            val res = FinancialTransactions.getFinancialTransactions(RegimeKeys.IT, incomeTaxRegime.id, queryParameters)

            DesFinancialDataStub.verifyGetDesBusinessDetails(incomeTaxRegime, queryParameters)

            Then("a successful response is returned with the correct estimate")

            res should have(
              httpStatus(BAD_REQUEST),
              jsonBodyAs[Error](FinancialData1166.singleErrorResponse)
            )
          }
        }

        "a bad request response is returned by the API, containing multiple errors" should {

          lazy val queryParameters = RequestQueryParameters()

          "return a multi error response model" in {

            isAuthorised()

            And("I wiremock stub a successful Get Financial Data response")
            DesFinancialDataStub.stubGetFinancialData(
              incomeTaxRegime, queryParameters)(BAD_REQUEST, Json.toJson(FinancialData1166.multiErrorModel))

            When(s"I call GET /financial-transactions/it/$mtditid")
            val res = FinancialTransactions.getFinancialTransactions(RegimeKeys.IT, incomeTaxRegime.id, queryParameters)

            DesFinancialDataStub.verifyGetDesBusinessDetails(incomeTaxRegime, queryParameters)

            Then("a successful response is returned with the correct estimate")

            res should have(
              httpStatus(BAD_REQUEST),
              jsonBodyAs[MultiError](FinancialData1166.multiErrorModel)
            )
          }
        }


        "the request is unauthorised" should {

          "return an FORBIDDEN response" in {

            isAuthorised(false)

            When(s"I call GET /financial-transactions/it/$mtditid")
            val res = FinancialTransactions.getFinancialTransactions(RegimeKeys.IT, incomeTaxRegime.id, RequestQueryParameters())

            res should have(
              httpStatus(FORBIDDEN)
            )
          }
        }
      }

      "requesting VAT transactions" when {

        lazy val vrn = "123456789"
        lazy val vatRegime = VatRegime(vrn)

        "a successful response is returned by the API" should {

          lazy val queryParameters = RequestQueryParameters()

          "return a success response" in {

            isAuthorised()

            And("I wiremock stub a successful Get Financial Data response")
            DesFinancialDataStub.stubGetFinancialData(
              vatRegime, queryParameters)(OK, Json.toJson(FinancialData1166.successResponse))

            When(s"I call GET /financial-transactions/vat/$vrn")
            val res = FinancialTransactions.getFinancialTransactions(RegimeKeys.VAT, vatRegime.id, queryParameters)

            DesFinancialDataStub.verifyGetDesBusinessDetails(vatRegime, queryParameters)

            Then("a successful response is returned with the correct estimate")

            res should have(
              httpStatus(OK),
              jsonBodyAs[FinancialTransactions](FinancialData1166.successResponse)
            )
          }
        }

        "a bad request response is returned by the API, containing one error" should {

          lazy val queryParameters = RequestQueryParameters()

          "return a single error response" in {

            isAuthorised()

            And("I wiremock stub a successful Get Financial Data response")
            DesFinancialDataStub.stubGetFinancialData(
              vatRegime, queryParameters)(BAD_REQUEST, Json.toJson(FinancialData1166.singleErrorResponse))

            When(s"I call GET /financial-transactions/vat/$vrn")
            val res = FinancialTransactions.getFinancialTransactions(RegimeKeys.VAT, vatRegime.id, queryParameters)

            DesFinancialDataStub.verifyGetDesBusinessDetails(vatRegime, queryParameters)

            Then("a successful response is returned with the correct estimate")

            res should have(
              httpStatus(BAD_REQUEST),
              jsonBodyAs[Error](FinancialData1166.singleErrorResponse)
            )
          }
        }

        "a bad request response is returned by the API, containing multiple errors" should {

          lazy val queryParameters = RequestQueryParameters()

          "return a multi error response model" in {

            isAuthorised()

            And("I wiremock stub a successful Get Financial Data response")
            DesFinancialDataStub.stubGetFinancialData(
              vatRegime, queryParameters)(BAD_REQUEST, Json.toJson(FinancialData1166.multiErrorModel))

            When(s"I call GET /financial-transactions/vat/$vrn")
            val res = FinancialTransactions.getFinancialTransactions(RegimeKeys.VAT, vatRegime.id, queryParameters)

            DesFinancialDataStub.verifyGetDesBusinessDetails(vatRegime, queryParameters)

            Then("a successful response is returned with the correct estimate")

            res should have(
              httpStatus(BAD_REQUEST),
              jsonBodyAs[MultiError](FinancialData1166.multiErrorModel)
            )
          }
        }

        "the request is unauthorised" should {

          "return an FORBIDDEN response" in {

            isAuthorised(false)

            When(s"I call GET /financial-transactions/vat/$vrn")
            val res = FinancialTransactions.getFinancialTransactions(RegimeKeys.VAT, vatRegime.id, RequestQueryParameters())

            res should have(
              httpStatus(FORBIDDEN)
            )
          }
        }
      }
    }
  }
}
