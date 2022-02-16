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

import helpers.ComponentSpecBase
import helpers.servicemocks.DesFinancialDataStub
import models.API1166._
import models.{RequestQueryParameters, IncomeTaxRegime, VatRegime}
import play.api.http.Status._
import play.api.libs.json.Json
import testData.FinancialData

class FinancialTransactionsComponentSpec extends ComponentSpecBase {

  "Sending a request to /financial-transactions/:regime/:identifier (FinancialTransactions controller)" when {

    "requesting Income Tax transactions" when {

      lazy val mtditid = "XAIT000000123456"
      lazy val incomeTaxRegime = IncomeTaxRegime(mtditid)

      "a successful response is returned by the API" should {

        lazy val queryParameters = RequestQueryParameters()

        "return a success response" in {

          isAuthorised()

          And("I wiremock stub a successful Get Financial Data response")
          DesFinancialDataStub.stubGetFinancialData(incomeTaxRegime, queryParameters)(OK, Json.toJson(FinancialData.successResponse))

          When(s"I call GET /financial-transactions/it/$mtditid")
          val res = FinancialTransactions.getFinancialTransactions("it", mtditid, queryParameters)

          DesFinancialDataStub.verifyGetDesBusinessDetails(incomeTaxRegime, queryParameters)

          Then("a successful response is returned with the correct estimate")

          res should have(
            httpStatus(OK),
            jsonBodyAs[FinancialTransactions](FinancialData.successResponse)
          )
        }
      }

      "a bad request response is returned by the API, containing one error" should {

        lazy val queryParameters = RequestQueryParameters()

        "return a single error response" in {

          isAuthorised()

          And("I wiremock stub a successful Get Financial Data response")
          DesFinancialDataStub.stubGetFinancialData(incomeTaxRegime, queryParameters)(BAD_REQUEST, Json.toJson(FinancialData.singleErrorResponse))

          When(s"I call GET /financial-transactions/it/$mtditid")
          val res = FinancialTransactions.getFinancialTransactions("it", mtditid, queryParameters)

          DesFinancialDataStub.verifyGetDesBusinessDetails(incomeTaxRegime, queryParameters)

          Then("a successful response is returned with the correct estimate")

          res should have(
            httpStatus(BAD_REQUEST),
            jsonBodyAs[Error](FinancialData.singleErrorResponse)
          )
        }
      }

      "a bad request response is returned by the API, containing multiple errors" should {

        lazy val queryParameters = RequestQueryParameters()

        "return a multi error response model" in {

          isAuthorised()

          And("I wiremock stub a successful Get Financial Data response")
          DesFinancialDataStub.stubGetFinancialData(incomeTaxRegime, queryParameters)(BAD_REQUEST, Json.toJson(FinancialData.multiErrorModel))

          When(s"I call GET /financial-transactions/it/$mtditid")
          val res = FinancialTransactions.getFinancialTransactions("it", mtditid, queryParameters)

          DesFinancialDataStub.verifyGetDesBusinessDetails(incomeTaxRegime, queryParameters)

          Then("a successful response is returned with the correct estimate")

          res should have(
            httpStatus(BAD_REQUEST),
            jsonBodyAs[MultiError](FinancialData.multiErrorModel)
          )
        }
      }


      "the request is unauthorised" should {

        "return an FORBIDDEN response" in {

          isAuthorised(false)

          When(s"I call GET /financial-transactions/it/$mtditid")
          val res = FinancialTransactions.getFinancialTransactions("it", mtditid, RequestQueryParameters())

          res should have(
            httpStatus(FORBIDDEN)
          )
        }
      }
    }

    "requesting VAT transactions" when {

      lazy val vrn = "123456"
      lazy val vatRegime = VatRegime(vrn)

      "a successful response is returned by the API" should {

        lazy val queryParameters = RequestQueryParameters()

        "return a success response" in {

          isAuthorised()

          And("I wiremock stub a successful Get Financial Data response")
          DesFinancialDataStub.stubGetFinancialData(vatRegime, queryParameters)(OK, Json.toJson(FinancialData.successResponse))

          When(s"I call GET /financial-transactions/vat/$vrn")
          val res = FinancialTransactions.getFinancialTransactions("vat", vrn, queryParameters)

          DesFinancialDataStub.verifyGetDesBusinessDetails(vatRegime, queryParameters)

          Then("a successful response is returned with the correct estimate")

          res should have(
            httpStatus(OK),
            jsonBodyAs[FinancialTransactions](FinancialData.successResponse)
          )
        }
      }

      "a bad request response is returned by the API, containing one error" should {

        lazy val queryParameters = RequestQueryParameters()

        "return a single error response" in {

          isAuthorised()

          And("I wiremock stub a successful Get Financial Data response")
          DesFinancialDataStub.stubGetFinancialData(vatRegime, queryParameters)(BAD_REQUEST, Json.toJson(FinancialData.singleErrorResponse))

          When(s"I call GET /financial-transactions/vat/$vrn")
          val res = FinancialTransactions.getFinancialTransactions("vat", vrn, queryParameters)

          DesFinancialDataStub.verifyGetDesBusinessDetails(vatRegime, queryParameters)

          Then("a successful response is returned with the correct estimate")

          res should have(
            httpStatus(BAD_REQUEST),
            jsonBodyAs[Error](FinancialData.singleErrorResponse)
          )
        }
      }

      "a bad request response is returned by the API, containing multiple errors" should {

        lazy val queryParameters = RequestQueryParameters()

        "return a multi error response model" in {

          isAuthorised()

          And("I wiremock stub a successful Get Financial Data response")
          DesFinancialDataStub.stubGetFinancialData(vatRegime, queryParameters)(BAD_REQUEST, Json.toJson(FinancialData.multiErrorModel))

          When(s"I call GET /financial-transactions/vat/$vrn")
          val res = FinancialTransactions.getFinancialTransactions("vat", vrn, queryParameters)

          DesFinancialDataStub.verifyGetDesBusinessDetails(vatRegime, queryParameters)

          Then("a successful response is returned with the correct estimate")

          res should have(
            httpStatus(BAD_REQUEST),
            jsonBodyAs[MultiError](FinancialData.multiErrorModel)
          )
        }
      }
      
      "the request is unauthorised" should {

        "return an FORBIDDEN response" in {

          isAuthorised(false)

          When(s"I call GET /financial-transactions/vat/$vrn")
          val res = FinancialTransactions.getFinancialTransactions("vat", vrn, RequestQueryParameters())

          res should have(
            httpStatus(FORBIDDEN)
          )
        }
      }
    }
  }
}
