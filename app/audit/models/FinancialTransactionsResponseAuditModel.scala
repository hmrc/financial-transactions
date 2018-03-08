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

import models.{FinancialTransactions, SubItem, TaxRegime, Transaction}
import play.api.libs.json.{JsValue, Json}

case class FinancialTransactionsResponseAuditModel(regime: TaxRegime, transactions: FinancialTransactions) extends AuditModel {

  private val paymentReferences: Seq[SubItem] => Seq[String] = _.flatMap(_.paymentReference)

  val toTransactionsAuditJson: Seq[Transaction] => JsValue = transactions =>
    Json.toJson(transactions.map(transaction =>
      TransactionsAuditModel(
        chargeReference = transaction.chargeReference,
        originalAmount = transaction.originalAmount,
        clearedAmount = transaction.clearedAmount,
        outstandingAmount = transaction.outstandingAmount,
        accruedInterest = transaction.accruedInterest,
        paymentReferences = transaction.items.map(paymentReferences)
      )
    ))

  override val transactionName: String = "financial-transactions-response"
  override val auditType: String = "financialTransactionsResponse"
  override val detail: Seq[(String, String)] = Seq(
    "taxRegime" -> regime.regimeType,
    "taxIdentifier" -> regime.id,
    "processingDate" -> transactions.processingDate.toString,
    "transactions" -> transactions.financialTransactions.fold("[]")(toTransactionsAuditJson(_).toString)
  )
}
