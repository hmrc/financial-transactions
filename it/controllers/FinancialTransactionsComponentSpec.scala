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

import config.RegimeKeys
import helpers.ComponentSpecBase
import helpers.servicemocks.EISFinancialDataStub
import models.{FinancialRequestQueryParameters, VatRegime}
import play.api.http.Status._

import testData.FinancialData1811

class FinancialTransactionsComponentSpec extends ComponentSpecBase {

  "Sending a request to /financial-transactions/:regime/:identifier (FinancialTransactions controller)" when {

    val vatRegime = VatRegime("123456789")

    "a successful response is returned by the API" should {

      lazy val queryParameters = FinancialRequestQueryParameters(onlyOpenItems = Some(false))

      "return a success response" in {

        isAuthorised()

        And("I wiremock stub a successful Get Financial Data response")
        EISFinancialDataStub.stubGetFinancialData(
          vatRegime)(OK, FinancialData1811.fullFinancialTransactionsJsonEIS)

        When(s"I call GET /financial-transactions/${RegimeKeys.VAT}/${vatRegime.id}")
        val res = FinancialTransactions.getFinancialTransactions(RegimeKeys.VAT, vatRegime.id, queryParameters)

        Then("a successful response is returned with expected JSON data")
        res should have(
          httpStatus(OK),
          jsonBodyAs(FinancialData1811.fullFinancialTransactionsOutputJson)
        )
      }
    }

    "an unsuccessful response is returned by the API" should {

      lazy val queryParameters = FinancialRequestQueryParameters()

      "return a single error response" in {

        isAuthorised()

        And("I wiremock stub a bad request response from Get Financial Data")
        EISFinancialDataStub.stubGetFinancialData(vatRegime)(BAD_REQUEST, FinancialData1811.errorJson)

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
}
