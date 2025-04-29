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
      targetedSearch = TargetedSearch(
        searchType = queryParams.searchType,
        searchItem = queryParams.searchItem
      ),
      selectionCriteria = SelectionCriteria(
        dateRange = Some(DateRange(
          fromDate = queryParams.fromDate.map(_.toString),
          toDate = queryParams.toDate.map(_.toString)
        )),
        includeClearedItems = queryParams.includeClearedItems,
        includeStatisticalItems = queryParams.includeStatisticalItems,
        includePaymentOnAccount = queryParams.includePaymentOnAccount
      ),
      dataEnrichment = DataEnrichment(
        addRegimeTotalisation = queryParams.addRegimeTotalisation,
        addLockInformation = queryParams.addLockInformation,
        addPenaltyDetails = queryParams.addPenaltyDetails,
        addPostedInterestDetails = queryParams.addPostedInterestDetails,
        addAccruingInterestDetails = queryParams.addAccruingInterestDetails
      )
    )
}