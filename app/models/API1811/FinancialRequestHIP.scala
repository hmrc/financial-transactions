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

import play.api.libs.json.{Json, OWrites}

case class FinancialRequestHIP(
                                taxRegime: String,
                                taxpayerInformation: TaxpayerInformation,
                                targetedSearch: Option[TargetedSearch],
                                selectionCriteria: Option[SelectionCriteria],
                                dataEnrichment: Option[DataEnrichment]
                              )

case class TaxpayerInformation(idType: String, idNumber: String)
case class TargetedSearch(searchType: String, searchItem: String)
case class SelectionCriteria(
                              dateRange: Option[DateRange],
                              includeClearedItems: Boolean,
                              includeStatisticalItems: Boolean,
                              includePaymentOnAccount: Boolean
                            )
case class DateRange(dateType: String, dateFrom: String, dateTo: String)
case class DataEnrichment(
                           addRegimeTotalisation: Boolean,
                           addLockInformation: Boolean,
                           addPenaltyDetails: Boolean,
                           addPostedInterestDetails: Boolean,
                           addAccruingInterestDetails: Boolean
                         )

object FinancialRequestHIP {
  implicit val taxpayerInformationWrites: OWrites[TaxpayerInformation] = Json.writes[TaxpayerInformation]
  implicit val targetedSearchWrites: OWrites[TargetedSearch] = Json.writes[TargetedSearch]
  implicit val dateRangeWrites: OWrites[DateRange] = Json.writes[DateRange]
  implicit val selectionCriteriaWrites: OWrites[SelectionCriteria] = Json.writes[SelectionCriteria]
  implicit val dataEnrichmentWrites: OWrites[DataEnrichment] = Json.writes[DataEnrichment]

  implicit val writes: OWrites[FinancialRequestHIP] = Json.writes[FinancialRequestHIP]
}
