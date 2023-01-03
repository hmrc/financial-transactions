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

package appRoutes

import base.SpecBase
import models.FinancialRequestQueryParameters

import java.time.LocalDate

class AppRoutesSpec extends SpecBase {

  "The reverse route for FinancialTransactionsController.getFinancialTransactions" should {

    "for VAT tax regime" when {

      lazy val idType = "vat"
      lazy val id = "123456"

      "no query parameters are supplied" should {

        lazy val queryParams = FinancialRequestQueryParameters()

        val expected = "/financial-transactions/vat/123456"

        s"have the route '$expected'" in {
          val route = controllers.routes.FinancialTransactionsController.getFinancialTransactions(idType, id, queryParams).url
          route shouldBe expected
        }
      }

      "'dateFrom' query parameter is supplied" should {

        lazy val queryParams = FinancialRequestQueryParameters(Some(LocalDate.parse("2018-02-01")))

        val expected = "/financial-transactions/vat/123456?dateFrom=2018-02-01"

        s"have the route '$expected'" in {
          val route = controllers.routes.FinancialTransactionsController.getFinancialTransactions(idType, id, queryParams).url
          route shouldBe expected
        }
      }

      "'dateFrom, dateTo' query parameters are supplied" should {

        lazy val queryParams = FinancialRequestQueryParameters(Some(LocalDate.parse("2018-02-01")), Some(LocalDate.parse("2019-03-01")))

        val expected = "/financial-transactions/vat/123456?dateFrom=2018-02-01&dateTo=2019-03-01"

        s"have the route '$expected'" in {
          val route = controllers.routes.FinancialTransactionsController.getFinancialTransactions(idType, id, queryParams).url
          route shouldBe expected
        }
      }

      "all query parameters are supplied" should {

        lazy val queryParams = FinancialRequestQueryParameters(
          fromDate = Some(LocalDate.parse("2018-02-01")),
          toDate = Some(LocalDate.parse("2019-03-01")),
          onlyOpenItems = Some(true)
        )

        val expected = "/financial-transactions/vat/123456?dateFrom=2018-02-01" +
          "&dateTo=2019-03-01" +
          "&onlyOpenItems=true"

        s"have the route '$exist" in {
          val route = controllers.routes.FinancialTransactionsController.getFinancialTransactions(idType, id, queryParams).url
          route shouldBe expected
        }
      }
    }
  }
}
