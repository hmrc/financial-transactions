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

package models

import base.SpecBase
import utils.ImplicitDateFormatter._
import FinancialRequestQueryParameters._

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

  "The FinancialDataQueryParameters.toSeqQueryParams method" should {

    "output the expected sequence of key-value pairs" which {

      "for no Query Parameters, has no values" in {
        val queryParams = FinancialRequestQueryParameters()
        queryParams.toSeqQueryParams shouldBe Seq()
      }

      "for fromDate Query Param, has a 'dateFrom' param with correct value" in {
        val queryParams = FinancialRequestQueryParameters(fromDate = Some("2018-04-06"))
        queryParams.toSeqQueryParams shouldBe Seq(dateFromKey -> "2018-04-06")
      }

      "for toDate Query Param, has a 'dateTo' param with correct value" in {
        val queryParams = FinancialRequestQueryParameters(toDate = Some("2019-04-05"))
        queryParams.toSeqQueryParams shouldBe Seq(dateToKey -> "2019-04-05")
      }

      "for onlyOpenItems Query Param, has a 'onlyOpenItems' param with correct value" in {
        val queryParams = FinancialRequestQueryParameters(onlyOpenItems = Some(true))
        queryParams.toSeqQueryParams shouldBe Seq(onlyOpenItemsKey -> "true")
      }

      "for all Query Params, outputs them all as expected" in {
        val queryParams = FinancialRequestQueryParameters(
          fromDate = Some("2017-04-06"),
          toDate = Some("2018-04-05"),
          onlyOpenItems = Some(false)
        )
        queryParams.toSeqQueryParams shouldBe Seq(
          dateFromKey -> "2017-04-06",
          dateToKey -> "2018-04-05",
          onlyOpenItemsKey -> "false"
        )
      }
    }
  }
  "The FinancialDataQueryParameters.api1811QueryParams method" should {

    "output the expected sequence of key value pairs" which {

      "if only open items is defined it should be added to the sequence" in {
        val queryParams = FinancialRequestQueryParameters(
          fromDate = Some("2017-04-06"),
          toDate = Some("2018-04-05"),
          onlyOpenItems = Some(true)
        )
        queryParams.api1811QueryParams shouldBe Seq(
          dateFromKey -> "2017-04-06",
          dateToKey -> "2018-04-05",
          onlyOpenItemsKey -> "true",
          includeStatisticalKey -> "true",
          includeLocksKey -> "true",
          calculateAccruedInterestKey -> "true",
          removePOAKey -> "false",
          customerPaymentInformationKey -> "true"
        )
      }
      "if only open items is not defined in the request it should be added to the sequence and set to false" in {
        val queryParams = FinancialRequestQueryParameters(
          fromDate = Some("2017-04-06"),
          toDate = Some("2018-04-05"),
          None
        )
        queryParams.api1811QueryParams shouldBe Seq(
          dateFromKey -> "2017-04-06",
          dateToKey -> "2018-04-05",
          onlyOpenItemsKey -> "false",
          includeStatisticalKey -> "true",
          includeLocksKey -> "true",
          calculateAccruedInterestKey -> "true",
          removePOAKey -> "false",
          customerPaymentInformationKey -> "true"
        )
      }
    }

  }
}
