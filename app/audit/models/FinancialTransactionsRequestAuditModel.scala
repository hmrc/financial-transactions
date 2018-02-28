/*
 * Copyright 2018 HM Revenue & Customs
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
  override val detail: Map[String, String] = Map(
    "taxRegime" -> regime.regimeType,
    "taxIdentifier" -> regime.id,
    dateFromKey -> auditHandleOption(queryParams.fromDate),
    dateToKey -> auditHandleOption(queryParams.toDate),
    onlyOpenItemsKey -> auditHandleOption(queryParams.onlyOpenItems),
    includeLocksKey -> auditHandleOption(queryParams.includeLocks),
    calculateAccruedInterestKey -> auditHandleOption(queryParams.calculateAccruedInterest),
    customerPaymentInformationKey -> auditHandleOption(queryParams.customerPaymentInformation)
  )
}
