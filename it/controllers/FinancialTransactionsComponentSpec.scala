/*
 * Copyright 2017 HM Revenue & Customs
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
import models.{Error, FinancialDataQueryParameters, FinancialTransactions, IncomeTaxRegime, MultiError, VatRegime}
import play.api.http.Status._
import play.api.libs.json.Json
import testData.FinancialData

class FinancialTransactionsComponentSpec extends ComponentSpecBase {

  "Sending a request to /financial-transactions/:regime/:identifier (FinancialTransactions controller)" when {

    "Requesting Income Tax transactions" should {

      lazy val mtditid = "XAIT000000123456"
      lazy val incomeTaxRegime = IncomeTaxRegime(mtditid)

      "authorised with a valid request with no query parameters and a success response" should {

        lazy val queryParameters = FinancialDataQueryParameters()

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

      "authorised with a valid request with no query parameters and an error response" should {

        lazy val queryParameters = FinancialDataQueryParameters()

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

      "authorised with a valid request with no query parameters and a multi error response" should {

        lazy val queryParameters = FinancialDataQueryParameters()

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


      "unauthorised" should {

        "return an FORBIDDEN response" in {

          isAuthorised(false)

          When(s"I call GET /financial-transactions/it/$mtditid")
          val res = FinancialTransactions.getFinancialTransactions("it", mtditid, FinancialDataQueryParameters())

          res should have(
            httpStatus(FORBIDDEN)
          )
        }
      }
    }

    "Requesting VAT transactions" should {

      lazy val vrn = "123456"
      lazy val vatRegime = VatRegime(vrn)

      "authorised with a valid request with no query parameters and a success response" should {

        lazy val queryParameters = FinancialDataQueryParameters()

        "return a success response" in {

          isAuthorised()

          And("I wiremock stub a successful Get Financial Data response")
          DesFinancialDataStub.stubGetFinancialData(vatRegime, queryParameters)(OK, Json.toJson(FinancialData.successResponse))

          When(s"I call GET /financial-transactions/it/$vrn")
          val res = FinancialTransactions.getFinancialTransactions("vat", vrn, queryParameters)

          DesFinancialDataStub.verifyGetDesBusinessDetails(vatRegime, queryParameters)

          Then("a successful response is returned with the correct estimate")

          res should have(
            httpStatus(OK),
            jsonBodyAs[FinancialTransactions](FinancialData.successResponse)
          )
        }
      }

      "authorised with a valid request with no query parameters and an error response" should {

        lazy val queryParameters = FinancialDataQueryParameters()

        "return a single error response" in {

          isAuthorised()

          And("I wiremock stub a successful Get Financial Data response")
          DesFinancialDataStub.stubGetFinancialData(vatRegime, queryParameters)(BAD_REQUEST, Json.toJson(FinancialData.singleErrorResponse))

          When(s"I call GET /financial-transactions/it/$vrn")
          val res = FinancialTransactions.getFinancialTransactions("vat", vrn, queryParameters)

          DesFinancialDataStub.verifyGetDesBusinessDetails(vatRegime, queryParameters)

          Then("a successful response is returned with the correct estimate")

          res should have(
            httpStatus(BAD_REQUEST),
            jsonBodyAs[Error](FinancialData.singleErrorResponse)
          )
        }
      }

      "authorised with a valid request with no query parameters and a multi error response" should {

        lazy val queryParameters = FinancialDataQueryParameters()

        "return a multi error response model" in {

          isAuthorised()

          And("I wiremock stub a successful Get Financial Data response")
          DesFinancialDataStub.stubGetFinancialData(vatRegime, queryParameters)(BAD_REQUEST, Json.toJson(FinancialData.multiErrorModel))

          When(s"I call GET /financial-transactions/it/$vrn")
          val res = FinancialTransactions.getFinancialTransactions("vat", vrn, queryParameters)

          DesFinancialDataStub.verifyGetDesBusinessDetails(vatRegime, queryParameters)

          Then("a successful response is returned with the correct estimate")

          res should have(
            httpStatus(BAD_REQUEST),
            jsonBodyAs[MultiError](FinancialData.multiErrorModel)
          )
        }
      }


      "unauthorised" should {

        "return an FORBIDDEN response" in {

          isAuthorised(false)

          When(s"I call GET /financial-transactions/it/$vrn")
          val res = FinancialTransactions.getFinancialTransactions("vat", vrn, FinancialDataQueryParameters())

          res should have(
            httpStatus(FORBIDDEN)
          )
        }
      }
    }
  }
}
