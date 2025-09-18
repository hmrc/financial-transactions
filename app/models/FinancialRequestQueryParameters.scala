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

package models

import models.API1811._
import play.api.libs.json.{Format, JsValue, Json}

import java.time.LocalDate

case class FinancialRequestQueryParameters(fromDate: Option[LocalDate] = None,
                                           toDate: Option[LocalDate] = None,
                                           onlyOpenItems: Option[Boolean] = None,
                                           includeClearedItems: Option[Boolean] = None,
                                           includeStatisticalItems: Option[Boolean] = None,
                                           includePaymentOnAccount: Option[Boolean] = None,
                                           addRegimeTotalisation: Option[Boolean] = None,
                                           addLockInformation: Option[Boolean] = None,
                                           addPenaltyDetails: Option[Boolean] = None,
                                           addPostedInterestDetails: Option[Boolean] = None,
                                           addAccruingInterestDetails: Option[Boolean] = None,
                                           searchType: Option[String] = None,
                                           searchItem: Option[String] = None,
                                           dateType: Option[String] = None) {

  import FinancialRequestQueryParameters._

  val queryParams1811: Seq[(String, String)] =
    Seq(
      fromDate.map(_ => dateTypeKey -> "POSTING"),
      fromDate.map(dateFromKey -> _.toString),
      toDate.map(dateToKey -> _.toString),
      Some(onlyOpenItems.fold(includeClearedItemsKey -> "true")(boolean => includeClearedItemsKey -> (!boolean).toString)),
      Some(includeStatisticalItemsKey  -> "true"),
      Some(includePaymentOnAccountKey  -> "true"),
      Some(addRegimeTotalisationKey    -> "true"),
      Some(addLockInformationKey       -> "true"),
      Some(penaltyDetailsKey           -> "true"),
      Some(addPostedInterestDetailsKey -> "true"),
      Some(addAccruingInterestKey      -> "true")
    ).flatten

  val queryParams1166: Seq[(String, String)] =
    Seq(
      fromDate.map(dateFromKey -> _.toString),
      toDate.map(dateToKey -> _.toString),
      onlyOpenItems.map(onlyOpenItemsKey -> _.toString)
    ).flatten

  def toQueryRequestBody(regime: TaxRegime): JsValue = {
    val taxpayerInformation = TaxpayerInformation(
      idType = regime.idType,
      idNumber = regime.id
    )
    val targetedSearch = for {
      sType <- searchType
      sItem <- searchItem
    } yield TargetedSearch(
      searchType = sType,
      searchItem = sItem
    )
    val dateRange = for {
      fd <- fromDate
      td <- toDate
    } yield DateRange("POSTING", fd.toString, td.toString)
    val selectionCriteria = SelectionCriteria(
      dateRange = dateRange,
      includeClearedItems = !onlyOpenItems.getOrElse(false),
      includeStatisticalItems = true,
      includePaymentOnAccount = true
    )
    val dataEnrichment = DataEnrichment(
      addRegimeTotalisation = true,
      addLockInformation = true,
      addPenaltyDetails = true,
      addPostedInterestDetails = true,
      addAccruingInterestDetails = true
    )

    val requestBody = FinancialRequestHIP(
      taxRegime = regime.regimeType,
      taxpayerInformation = taxpayerInformation,
      targetedSearch = targetedSearch,
      selectionCriteria = Some(selectionCriteria),
      dataEnrichment = Some(dataEnrichment)
    )
    Json.toJson(requestBody)
  }
}

object FinancialRequestQueryParameters {

  val dateTypeKey                 = "dateType"
  val dateFromKey                 = "dateFrom"
  val dateToKey                   = "dateTo"
  val includeClearedItemsKey      = "includeClearedItems"
  val includeStatisticalItemsKey  = "includeStatisticalItems"
  val includePaymentOnAccountKey  = "includePaymentOnAccount"
  val addRegimeTotalisationKey    = "addRegimeTotalisation"
  val addLockInformationKey       = "addLockInformation"
  val penaltyDetailsKey           = "addPenaltyDetails"
  val addPostedInterestDetailsKey = "addPostedInterestDetails"
  val addAccruingInterestKey      = "addAccruingInterestDetails"
  val onlyOpenItemsKey            = "onlyOpenItems"

  implicit val format: Format[FinancialRequestQueryParameters] = Json.format[FinancialRequestQueryParameters]
}
