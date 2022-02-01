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

package models.API1811

import java.time.LocalDate

import play.api.libs.json.{Format, Json}

case class FinancialDataQueryParameters(fromDate: Option[LocalDate] = None,
                                        toDate: Option[LocalDate] = None,
                                        onlyOpenItems: Option[Boolean] = Some(false),
                                        includeLocks: Option[Boolean] = Some(true),
                                        calculateAccruedInterest: Option[Boolean] = Some(true),
                                        removePOA: Option[Boolean] = Some(true),
                                        customerPaymentInformation: Option[Boolean] = Some(true)) {
  import FinancialDataQueryParameters._
  val toSeqQueryParams: Seq[(String, String)] = Seq(
    fromDate.map(dateFromKey -> _.toString),
    toDate.map(dateToKey -> _.toString),
    onlyOpenItems.map(onlyOpenItemsKey -> _.toString),
    includeLocks.map(includeLocksKey -> _.toString),
    calculateAccruedInterest.map(calculateAccruedInterestKey -> _.toString),
    removePOA.map(removePOAKey -> _.toString),
    customerPaymentInformation.map(customerPaymentInformationKey -> _.toString)
  ).flatten
  val hasQueryParameters: Boolean = toSeqQueryParams.nonEmpty
}

object FinancialDataQueryParameters {

  val dateFromKey = "dateFrom"
  val dateToKey = "dateTo"
  val onlyOpenItemsKey = "onlyOpenItems"
  val includeLocksKey = "includeLocks"
  val calculateAccruedInterestKey = "calculateAccruedInterest"
  val removePOAKey = "removePOA"
  val customerPaymentInformationKey = "customerPaymentInformation"

  implicit val format: Format[FinancialDataQueryParameters] = Json.format[FinancialDataQueryParameters]
}
