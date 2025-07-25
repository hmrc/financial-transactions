/*
 * Copyright 2025 HM Revenue & Customs
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

package models.API1811

import models.{FinancialRequestQueryParameters, TaxRegime}

object FinancialRequestHIPHelper {

  def HIPRequestBody(regime: TaxRegime, queryParams: FinancialRequestQueryParameters): FinancialRequestHIP =
    FinancialRequestHIP(
      taxRegime = regime.regimeType,
      taxpayerInformation = TaxpayerInformation(
        idType = regime.idType,
        idNumber = regime.id
      ),
      targetedSearch = for {
        sType <- queryParams.searchType
        sItem <- queryParams.searchItem
      } yield TargetedSearch(
        searchType = sType,
        searchItem = sItem
      ),
      selectionCriteria = for {
        dateType <- queryParams.dateType
        from <- queryParams.fromDate.map(_.toString)
        to <- queryParams.toDate.map(_.toString)
        clearedItems <- queryParams.includeClearedItems
        statisticalItems <- queryParams.includeStatisticalItems
        paymentonAccount <- queryParams.includePaymentOnAccount
      } yield SelectionCriteria(
        dateRange = Some(DateRange(dateType, dateFrom = from, dateTo = to)),
        includeClearedItems = clearedItems,
        includeStatisticalItems = statisticalItems,
        includePaymentOnAccount = paymentonAccount
      ),
      dataEnrichment = for {
        regimeTotalisation <- queryParams.addRegimeTotalisation
        lockInformation <- queryParams.addLockInformation
        penaltyDetails <- queryParams.addPenaltyDetails
        postedInterestDetails <- queryParams.addPostedInterestDetails
        accruingInterestDetails <- queryParams.addAccruingInterestDetails
      } yield DataEnrichment(
        addRegimeTotalisation = regimeTotalisation,
        addLockInformation = lockInformation,
        addPenaltyDetails = penaltyDetails,
        addPostedInterestDetails = postedInterestDetails,
        addAccruingInterestDetails = accruingInterestDetails
      )
    )
}