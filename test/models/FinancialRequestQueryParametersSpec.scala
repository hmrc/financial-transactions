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

package models

import base.SpecBase

import FinancialRequestQueryParameters._

import java.time.LocalDate

class FinancialRequestQueryParametersSpec extends SpecBase {

  "The FinancialDataQueryParameters object" should {

    "have the correct key value for 'dateFrom'" in {
      dateFromKey shouldBe "dateFrom"
    }

    "have the correct key value for 'dateTo'" in {
      dateToKey shouldBe "dateTo"
    }

    "have the correct key value for 'onlyOpenItems'" in {
      onlyOpenItemsKey shouldBe "onlyOpenItems"
    }
  }

  "The FinancialDataQueryParameters.queryParams1166 method" should {

    "output the expected sequence of key-value pairs" which {

      "for no Query Parameters, has no values" in {
        val queryParams = FinancialRequestQueryParameters()
        queryParams.queryParams1166 shouldBe Seq()
      }

      "for fromDate Query Param, has a 'dateFrom' param with correct value" in {
        val queryParams = FinancialRequestQueryParameters(fromDate = Some(LocalDate.parse("2018-04-06")))
        queryParams.queryParams1166 shouldBe Seq(dateFromKey -> "2018-04-06")
      }

      "for toDate Query Param, has a 'dateTo' param with correct value" in {
        val queryParams = FinancialRequestQueryParameters(toDate = Some(LocalDate.parse("2019-04-05")))
        queryParams.queryParams1166 shouldBe Seq(dateToKey -> "2019-04-05")
      }

      "for onlyOpenItems Query Param, has a 'onlyOpenItems' param with correct value" in {
        val queryParams = FinancialRequestQueryParameters(onlyOpenItems = Some(true))
        queryParams.queryParams1166 shouldBe Seq(onlyOpenItemsKey -> "true")
      }

      "for all Query Params, outputs them all as expected" in {
        val queryParams = FinancialRequestQueryParameters(
          fromDate = Some(LocalDate.parse("2017-04-06")),
          toDate = Some(LocalDate.parse("2018-04-05")),
          onlyOpenItems = Some(false)
        )
        queryParams.queryParams1166 shouldBe Seq(
          dateFromKey -> "2017-04-06",
          dateToKey -> "2018-04-05",
          onlyOpenItemsKey -> "false"
        )
      }
    }
  }
  "The FinancialDataQueryParameters.queryParams1811 method" should {

    "output the expected sequence of key value pairs" when {

      "only open items is defined in the request then include cleared items should be set to the opposite" in {
        val queryParams = FinancialRequestQueryParameters(
          fromDate = Some(LocalDate.parse("2017-04-06")),
          toDate = Some(LocalDate.parse("2018-04-05")),
          onlyOpenItems = Some(true)
        )
        queryParams.queryParams1811 shouldBe Seq(
          dateTypeKey -> "POSTING",
          dateFromKey -> "2017-04-06",
          dateToKey -> "2018-04-05",
          includeClearedItemsKey -> "false",
          includeStatisticalItemsKey -> "true",
          includePaymentOnAccountKey -> "true",
          addRegimeTotalisationKey -> "true",
          addLockInformationKey -> "true",
          penaltyDetailsKey -> "true",
          addPostedInterestDetailsKey -> "true",
          addAccruingInterestKey -> "true"
        )
      }

      "only open items is not defined in the request then include cleared items should be set to true" in {
        val queryParams = FinancialRequestQueryParameters(
          fromDate = Some(LocalDate.parse("2017-04-06")),
          toDate = Some(LocalDate.parse("2018-04-05")),
          None
        )
        queryParams.queryParams1811 shouldBe Seq(
          dateTypeKey -> "POSTING",
          dateFromKey -> "2017-04-06",
          dateToKey -> "2018-04-05",
          includeClearedItemsKey -> "true",
          includeStatisticalItemsKey -> "true",
          includePaymentOnAccountKey -> "true",
          addRegimeTotalisationKey -> "true",
          addLockInformationKey -> "true",
          penaltyDetailsKey -> "true",
          addPostedInterestDetailsKey -> "true",
          addAccruingInterestKey -> "true"
        )
      }

      "no dates are provided then no date related parameters should be added to the sequence" in {
        val queryParams = FinancialRequestQueryParameters(
          fromDate = None,
          toDate = None,
          onlyOpenItems = Some(true)
        )
        queryParams.queryParams1811 shouldBe Seq(
          includeClearedItemsKey -> "false",
          includeStatisticalItemsKey -> "true",
          includePaymentOnAccountKey -> "true",
          addRegimeTotalisationKey -> "true",
          addLockInformationKey -> "true",
          penaltyDetailsKey -> "true",
          addPostedInterestDetailsKey -> "true",
          addAccruingInterestKey -> "true"
        )
      }
    }
  }
}
