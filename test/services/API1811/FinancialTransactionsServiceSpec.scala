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

package services.API1811

import base.SpecBase
import mocks.connectors.Mock1811FinancialDataConnector
import models.API1811.Error
import models.{FinancialRequestQueryParameters, TaxRegime, VatRegime}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.API1811.TestConstants.fullFinancialTransactions

import play.api.http.Status

import java.time.LocalDate

class FinancialTransactionsServiceSpec extends SpecBase with Mock1811FinancialDataConnector {

  object Service extends FinancialTransactionsService(mockFinancialDataConnector, ec)

  "The FinancialTransactionsService.getFinancialTransactions method" when {

    val queryParams: FinancialRequestQueryParameters = FinancialRequestQueryParameters(
      fromDate = Some(LocalDate.parse("2017-04-06")),
      toDate = Some(LocalDate.parse("2018-04-05")),
      onlyOpenItems = Some(false)
    )
    val regime: TaxRegime = VatRegime("123456")

    "the connector returns a success response" should {

      "return the same response" in {

        val successResponse = Right(fullFinancialTransactions)
        setupMockGetFinancialData(regime, queryParams)(successResponse)
        val actual = await(Service.getFinancialTransactions(regime, queryParams))

        actual shouldBe successResponse

      }
    }

    "the connector returns a failure response" should {

      "return the same response" in {
        val failureResponse = Left(Error(Status.INTERNAL_SERVER_ERROR, "error"))
        setupMockGetFinancialData(regime, queryParams)(failureResponse)
        val actual = await(Service.getFinancialTransactions(regime, queryParams))

        actual shouldBe failureResponse
      }
    }
  }
}
