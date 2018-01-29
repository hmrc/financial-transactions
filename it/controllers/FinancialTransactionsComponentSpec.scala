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
import models.{FinancialDataQueryParameters, FinancialTransactions, IncomeTaxRegime}
import play.api.http.Status
import play.api.http.Status._
import testData.FinancialData

class FinancialTransactionsComponentSpec extends ComponentSpecBase {

  "Calling the FinancialTransactionsController" when {

    "Requesting Income Tax transactions" should {

      lazy val mtditid = "XAIT000000123456"
      lazy val incomeTaxRegime = IncomeTaxRegime(mtditid)

      "authorised with a valid request with no query parameters" should {

        lazy val queryParameters = FinancialDataQueryParameters()

        "return a success response" in {

          isAuthorised()

          And("I wiremock stub a successful Get Financial Data response")
          DesFinancialDataStub.stubGetFinancialData(incomeTaxRegime, queryParameters)(Status.OK, FinancialData.successResponse)

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
  }
}
