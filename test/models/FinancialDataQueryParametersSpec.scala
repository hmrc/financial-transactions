/*
 * Copyright 2021 HM Revenue & Customs
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
import FinancialDataQueryParameters._

class FinancialDataQueryParametersSpec extends SpecBase {

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

    "have the correct key value for 'includeLocks'" in {
      includeLocksKey shouldBe "includeLocks"
    }

    "have the correct key value for 'calculateAccruedInterest'" in {
      calculateAccruedInterestKey shouldBe "calculateAccruedInterest"
    }

    "have the correct key value for 'customerPaymentInformation'" in {
      customerPaymentInformationKey shouldBe "customerPaymentInformation"
    }
  }

  "The FinancialDataQueryParameters.toSeqQueryParams method" should {

    "output the expected sequence of key-value pairs" which {

      "for no Query Parameters, has no values" in {
        val queryParams = FinancialDataQueryParameters()
        queryParams.toSeqQueryParams shouldBe Seq()
      }

      "for fromDate Query Param, has a 'dateFrom' param with correct value" in {
        val queryParams = FinancialDataQueryParameters(fromDate = Some("2018-04-06"))
        queryParams.toSeqQueryParams shouldBe Seq(dateFromKey -> "2018-04-06")
      }

      "for toDate Query Param, has a 'dateTo' param with correct value" in {
        val queryParams = FinancialDataQueryParameters(toDate = Some("2019-04-05"))
        queryParams.toSeqQueryParams shouldBe Seq(dateToKey -> "2019-04-05")
      }

      "for onlyOpenItems Query Param, has a 'onlyOpenItems' param with correct value" in {
        val queryParams = FinancialDataQueryParameters(onlyOpenItems = Some(true))
        queryParams.toSeqQueryParams shouldBe Seq(onlyOpenItemsKey -> "true")
      }

      "for includeLocks Query Param, has a 'includeLocks' param with correct value" in {
        val queryParams = FinancialDataQueryParameters(includeLocks = Some(true))
        queryParams.toSeqQueryParams shouldBe Seq(includeLocksKey -> "true")
      }

      "for calculateAccruedInterest Query Param, has a 'calculateAccruedInterest' param with correct value" in {
        val queryParams = FinancialDataQueryParameters(calculateAccruedInterest = Some(true))
        queryParams.toSeqQueryParams shouldBe Seq(calculateAccruedInterestKey -> "true")
      }

      "for customerPaymentInformation Query Param, has a 'customerPaymentInformation' param with correct value" in {
        val queryParams = FinancialDataQueryParameters(customerPaymentInformation = Some(true))
        queryParams.toSeqQueryParams shouldBe Seq(customerPaymentInformationKey -> "true")
      }

      "for all Query Params, outputs them all as expected" in {
        val queryParams = FinancialDataQueryParameters(
          fromDate = Some("2017-04-06"),
          toDate = Some("2018-04-05"),
          onlyOpenItems = Some(false),
          includeLocks = Some(true),
          calculateAccruedInterest = Some(false),
          customerPaymentInformation = Some(false)
        )
        queryParams.toSeqQueryParams shouldBe Seq(
          dateFromKey -> "2017-04-06",
          dateToKey -> "2018-04-05",
          onlyOpenItemsKey -> "false",
          includeLocksKey -> "true",
          calculateAccruedInterestKey -> "false",
          customerPaymentInformationKey -> "false"
        )
      }
    }
  }
}
