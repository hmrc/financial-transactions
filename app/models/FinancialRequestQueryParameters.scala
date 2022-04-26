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

import java.time.LocalDate

import play.api.libs.json.{Format, Json}

case class FinancialRequestQueryParameters(fromDate: Option[LocalDate] = None,
                                           toDate: Option[LocalDate] = None,
                                           onlyOpenItems: Option[Boolean] = None) {
  import FinancialRequestQueryParameters._
  val toSeqQueryParams: Seq[(String, String)] = Seq(
    fromDate.map(dateFromKey -> _.toString),
    toDate.map(dateToKey -> _.toString),
    onlyOpenItems.map(onlyOpenItemsKey -> _.toString)
  ).flatten
  val hasQueryParameters: Boolean = toSeqQueryParams.nonEmpty
  val api1811QueryParams: Seq[(String, String)] = {
    val openItems = if(onlyOpenItems.isDefined) {
      Seq()
    } else {
      Seq(onlyOpenItemsKey -> "false")
    }
    toSeqQueryParams ++ openItems ++ Seq(
      includeStatisticalKey -> "true",
      includeLocksKey -> "true",
      calculateAccruedInterestKey -> "true",
      removePOAKey -> "false",
      customerPaymentInformationKey -> "true"
    )
  }

}

object FinancialRequestQueryParameters {

  val dateFromKey = "dateFrom"
  val dateToKey = "dateTo"
  val onlyOpenItemsKey = "onlyOpenItems"
  val removePOAKey = "removePOA"
  val includeStatisticalKey = "includeStatistical"
  val includeLocksKey = "includeLocks"
  val calculateAccruedInterestKey = "calculateAccruedInterest"
  val customerPaymentInformationKey = "customerPaymentInformation"

  implicit val format: Format[FinancialRequestQueryParameters] = Json.format[FinancialRequestQueryParameters]
}
