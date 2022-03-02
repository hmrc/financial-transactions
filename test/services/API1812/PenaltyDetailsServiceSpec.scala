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

package services.API1812

import base.SpecBase
import models.API1811.Error
import models.{RequestQueryParameters, TaxRegime, VatRegime}
import play.api.http.Status
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.ImplicitDateFormatter._
import utils.TestConstantsAPI1811.fullFinancialTransactions

class PenaltyDetailsServiceSpec extends SpecBase with MockPenaltyDetailsConnector {

  object Service extends PenaltyDetailsService(mockPenaltyDetailsConnector, ec)

  "The FinancialTransactionsService.getFinancialTransactions method" when {

    val queryParams: RequestQueryParameters = RequestQueryParameters(
      fromDate = Some("2017-04-06"),
      toDate = Some("2018-04-05"),
      onlyOpenItems = Some(false)
    )
    val regime: TaxRegime = VatRegime("123456")

    "the connector returns a success response" should {

      "return the same response" in {

        val successResponse = Right(fullFinancialTransactions)
        setupPenaltyDetailsData(regime, queryParams)(successResponse)
        val actual = await(Service.getPenaltyDetails()(regime, queryParams))

        actual shouldBe successResponse

      }
    }

    "the connector returns a failure response" should {

      "return the same response" in {
        val failureResponse = Left(Error(Status.INTERNAL_SERVER_ERROR, "error"))
        setupPenaltyDetailsData(regime, queryParams)(failureResponse)
        val actual = await(Service.getPenaltyDetails(regime, queryParams))

        actual shouldBe failureResponse
      }
    }
  }
}
