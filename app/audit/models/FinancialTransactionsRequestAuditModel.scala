/*
 * Copyright 2020 HM Revenue & Customs
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

package audit.models

import models._
import models.FinancialDataQueryParameters._

case class FinancialTransactionsRequestAuditModel(regime: TaxRegime, queryParams: FinancialDataQueryParameters) extends AuditModel {

  override val transactionName: String = "financial-transactions-request"
  override val auditType: String = "financialTransactionsRequest"
  override val detail: Seq[(String, String)] = Seq(
    Some("taxRegime" -> regime.regimeType),
    Some("taxIdentifier" -> regime.id),
    queryParams.fromDate.map(dateFromKey -> _.toString),
    queryParams.toDate.map(dateToKey -> _.toString),
    queryParams.onlyOpenItems.map(onlyOpenItemsKey -> _.toString),
    queryParams.includeLocks.map(includeLocksKey -> _.toString),
    queryParams.calculateAccruedInterest.map(calculateAccruedInterestKey -> _.toString),
    queryParams.customerPaymentInformation.map(customerPaymentInformationKey -> _.toString)
  ).flatten
}
