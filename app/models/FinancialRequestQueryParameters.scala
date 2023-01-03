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

import java.time.LocalDate
import play.api.libs.json.{Format, Json}

case class FinancialRequestQueryParameters(fromDate: Option[LocalDate] = None,
                                           toDate: Option[LocalDate] = None,
                                           onlyOpenItems: Option[Boolean] = None) {

  import FinancialRequestQueryParameters._

  val queryParams1811: Seq[(String, String)] =
    Seq(
      fromDate.map(_ => dateTypeKey -> "BILLING"),
      fromDate.map(dateFromKey -> _.toString),
      toDate.map(dateToKey -> _.toString),
      Some(onlyOpenItems.fold(includeClearedItemsKey -> "true")(boolean => includeClearedItemsKey -> (!boolean).toString)),
      Some(includeStatisticalItemsKey -> "true"),
      Some(includePaymentOnAccountKey -> "true"),
      Some(addRegimeTotalisationKey -> "true"),
      Some(addLockInformationKey -> "true"),
      Some(penaltyDetailsKey -> "true"),
      Some(addPostedInterestDetailsKey -> "true"),
      Some(addAccruingInterestKey -> "true")
    ).flatten

  val queryParams1166: Seq[(String, String)] =
    Seq(
      fromDate.map(dateFromKey -> _.toString),
      toDate.map(dateToKey -> _.toString),
      onlyOpenItems.map(onlyOpenItemsKey -> _.toString)
    ).flatten

}

object FinancialRequestQueryParameters {

  val dateTypeKey = "dateType"
  val dateFromKey = "dateFrom"
  val dateToKey = "dateTo"
  val includeClearedItemsKey = "includeClearedItems"
  val includeStatisticalItemsKey = "includeStatisticalItems"
  val includePaymentOnAccountKey = "includePaymentOnAccount"
  val addRegimeTotalisationKey = "addRegimeTotalisation"
  val addLockInformationKey = "addLockInformation"
  val penaltyDetailsKey = "addPenaltyDetails"
  val addPostedInterestDetailsKey = "addPostedInterestDetails"
  val addAccruingInterestKey = "addAccruingInterestDetails"
  val onlyOpenItemsKey = "onlyOpenItems"

  implicit val format: Format[FinancialRequestQueryParameters] = Json.format[FinancialRequestQueryParameters]
}
