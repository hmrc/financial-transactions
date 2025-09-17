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
import models.FinancialRequestQueryParameters._
import play.api.libs.json.{JsObject, JsValue, Json}

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
          dateFromKey      -> "2017-04-06",
          dateToKey        -> "2018-04-05",
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
          dateTypeKey                 -> "POSTING",
          dateFromKey                 -> "2017-04-06",
          dateToKey                   -> "2018-04-05",
          includeClearedItemsKey      -> "false",
          includeStatisticalItemsKey  -> "true",
          includePaymentOnAccountKey  -> "true",
          addRegimeTotalisationKey    -> "true",
          addLockInformationKey       -> "true",
          penaltyDetailsKey           -> "true",
          addPostedInterestDetailsKey -> "true",
          addAccruingInterestKey      -> "true"
        )
      }

      "only open items is not defined in the request then include cleared items should be set to true" in {
        val queryParams = FinancialRequestQueryParameters(
          fromDate = Some(LocalDate.parse("2017-04-06")),
          toDate = Some(LocalDate.parse("2018-04-05")),
          None
        )
        queryParams.queryParams1811 shouldBe Seq(
          dateTypeKey                 -> "POSTING",
          dateFromKey                 -> "2017-04-06",
          dateToKey                   -> "2018-04-05",
          includeClearedItemsKey      -> "true",
          includeStatisticalItemsKey  -> "true",
          includePaymentOnAccountKey  -> "true",
          addRegimeTotalisationKey    -> "true",
          addLockInformationKey       -> "true",
          penaltyDetailsKey           -> "true",
          addPostedInterestDetailsKey -> "true",
          addAccruingInterestKey      -> "true"
        )
      }

      "no dates are provided then no date related parameters should be added to the sequence" in {
        val queryParams = FinancialRequestQueryParameters(
          fromDate = None,
          toDate = None,
          onlyOpenItems = Some(true)
        )
        queryParams.queryParams1811 shouldBe Seq(
          includeClearedItemsKey      -> "false",
          includeStatisticalItemsKey  -> "true",
          includePaymentOnAccountKey  -> "true",
          addRegimeTotalisationKey    -> "true",
          addLockInformationKey       -> "true",
          penaltyDetailsKey           -> "true",
          addPostedInterestDetailsKey -> "true",
          addAccruingInterestKey      -> "true"
        )
      }
    }
  }

  private val taxRegime = VatRegime("123456789")
  private val fullQueryParameters = FinancialRequestQueryParameters(
    fromDate = Some(LocalDate.of(2022, 1, 1)),
    toDate = Some(LocalDate.of(2025, 1, 1)),
    onlyOpenItems = Some(false),
    includeClearedItems = Some(true),
    includeStatisticalItems = Some(true),
    includePaymentOnAccount = Some(true),
    addRegimeTotalisation = Some(true),
    addLockInformation = Some(true),
    addPenaltyDetails = Some(true),
    addPostedInterestDetails = Some(true),
    addAccruingInterestDetails = Some(true),
    searchType = Some("CHGREF"),
    searchItem = Some("XC00178236592"),
    dateType = Some("POSTING")
  )
  private val emptyQueryParameters = FinancialRequestQueryParameters()

  private val targetedSearchJson = Json.obj(
    "searchType" -> "CHGREF",
    "searchItem" -> "XC00178236592"
  )

  private def selectionCriteriaJsonWithoutDateRange(includeClearedItemsValue: Boolean): JsObject = Json.obj(
    "includeClearedItems"     -> includeClearedItemsValue,
    "includeStatisticalItems" -> true,
    "includePaymentOnAccount" -> true
  )

  private val selectionCriteriaJsonWithDateRange = Json.obj(
    "dateRange" -> Json.obj(
      "dateType" -> "POSTING",
      "dateFrom" -> "2022-01-01",
      "dateTo"   -> "2025-01-01"
    ),
    "includeClearedItems"     -> true,
    "includeStatisticalItems" -> true,
    "includePaymentOnAccount" -> true
  )

  private def buildJsonBody(hasDateRange: Boolean, hasTargetedSearch: Boolean, includeClearedItemsValue: Boolean = true): JsValue = {
    val selectionCriteria =
      if (hasDateRange) selectionCriteriaJsonWithDateRange else selectionCriteriaJsonWithoutDateRange(includeClearedItemsValue)

    val baseJson = Json.obj(
      "taxRegime" -> "VATC",
      "taxpayerInformation" -> Json.obj(
        "idType"   -> "VRN",
        "idNumber" -> "123456789"
      )
    )
    val defaultFiltersJson = Json.obj(
      "selectionCriteria" -> selectionCriteria,
      "dataEnrichment" -> Json.obj(
        "addRegimeTotalisation"      -> true,
        "addLockInformation"         -> true,
        "addPenaltyDetails"          -> true,
        "addPostedInterestDetails"   -> true,
        "addAccruingInterestDetails" -> true
      )
    )

    if (hasTargetedSearch) {
      baseJson ++ Json.obj("targetedSearch" -> targetedSearchJson) ++ defaultFiltersJson
    } else {
      baseJson ++ defaultFiltersJson
    }
  }

  "toQueryRequestBody" should {
    "return Json body with full values" when {
      "all query parameters are given" in {
        val expectedJson = buildJsonBody(hasDateRange = true, hasTargetedSearch = true)

        fullQueryParameters.toQueryRequestBody(taxRegime) shouldBe expectedJson
      }
    }

    "return Json body with 'dateRange' in the 'selectionCriteria'" when {
      "'fromDate' and 'toDate' query parameters are given" in {
        val queryParameters = emptyQueryParameters.copy(
          fromDate = Some(LocalDate.of(2022, 1, 1)),
          toDate = Some(LocalDate.of(2025, 1, 1))
        )
        val expectedJson = buildJsonBody(hasDateRange = true, hasTargetedSearch = false)

        queryParameters.toQueryRequestBody(taxRegime) shouldBe expectedJson
      }
    }

    "return Json body with 'targetedSearch'" when {
      "'searchType' and 'searchItem' query parameters are given" in {
        val queryParameters = emptyQueryParameters.copy(
          searchType = Some("CHGREF"),
          searchItem = Some("XC00178236592")
        )
        val expectedJson = buildJsonBody(hasDateRange = false, hasTargetedSearch = true)

        queryParameters.toQueryRequestBody(taxRegime) shouldBe expectedJson
      }
    }

    "return the minimum Json body with only ID and default values" when {
      val minimumDefaultJsonBody = buildJsonBody(hasDateRange = false, hasTargetedSearch = false)
      "no query parameters are given" in {
        emptyQueryParameters.toQueryRequestBody(taxRegime) shouldBe minimumDefaultJsonBody
      }

      "one of 'fromDate' or 'toDate' are given but not both" in {
        val queryWithFromDate = emptyQueryParameters.copy(
          fromDate = Some(LocalDate.of(2022, 1, 1)),
          toDate = None
        )
        val queryWithToDate = emptyQueryParameters.copy(
          fromDate = None,
          toDate = Some(LocalDate.of(2025, 1, 1))
        )

        queryWithFromDate.toQueryRequestBody(taxRegime) shouldBe minimumDefaultJsonBody
        queryWithToDate.toQueryRequestBody(taxRegime) shouldBe minimumDefaultJsonBody
      }

      "one of 'searchType' or 'searchItem' are given but not both" in {
        val queryWithSearchType = emptyQueryParameters.copy(
          searchType = Some("CHGREF"),
          searchItem = None
        )
        val queryWithSearchItem = emptyQueryParameters.copy(
          searchType = None,
          searchItem = Some("XC00178236592")
        )

        queryWithSearchType.toQueryRequestBody(taxRegime) shouldBe minimumDefaultJsonBody
        queryWithSearchItem.toQueryRequestBody(taxRegime) shouldBe minimumDefaultJsonBody
      }
    }

    "return Json body with 'includeClearedItems' as 'true" when {
      "'onlyOpenItems' is 'false'" in {
        val queryParameters = emptyQueryParameters.copy(onlyOpenItems = Some(false))
        val expectedJson    = buildJsonBody(hasDateRange = false, hasTargetedSearch = false, includeClearedItemsValue = true)

        queryParameters.toQueryRequestBody(taxRegime) shouldBe expectedJson
      }
      "'onlyOpenItems' is not present" in {
        val queryParameters = emptyQueryParameters.copy(onlyOpenItems = None)
        val expectedJson    = buildJsonBody(hasDateRange = false, hasTargetedSearch = false, includeClearedItemsValue = true)

        queryParameters.toQueryRequestBody(taxRegime) shouldBe expectedJson
      }
    }

    "return Json body with 'includeClearedItems' as 'false" when {
      "'onlyOpenItems' is 'true'" in {
        val queryParameters = emptyQueryParameters.copy(onlyOpenItems = Some(true))
        val expectedJson    = buildJsonBody(hasDateRange = false, hasTargetedSearch = false, includeClearedItemsValue = false)

        queryParameters.toQueryRequestBody(taxRegime) shouldBe expectedJson
      }
    }
  }
}
