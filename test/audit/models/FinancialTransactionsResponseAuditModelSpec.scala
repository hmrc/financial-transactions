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

import base.SpecBase
import _root_.models.{FinancialTransactions, IncomeTaxRegime, SubItem, Transaction}
import play.api.libs.json.Json
import utils.ImplicitDateFormatter._

class FinancialTransactionsResponseAuditModelSpec extends SpecBase {

  val transactionName = "financial-transactions-response"
  val auditEvent = "financialTransactionsResponse"

  val testRegime = IncomeTaxRegime("XQIT00000000001")

  "The FinancialTransactionsResponseAuditModel" when {

    "passed some transaction data in the response" should {

      val testTransactions = FinancialTransactions(
        idType = Some("MTDBSA"),
        idNumber = Some("XQIT00000000001"),
        regimeType = Some("ITSA"),
        processingDate = "2018-03-07T22:55:56.987Z",
        financialTransactions = Some(Seq(
          Transaction(
            chargeReference = Some("XM002610011594"),
            originalAmount = Some(3400.0),
            outstandingAmount = Some(400.0),
            clearedAmount = Some(3000.0),
            accruedInterest = Some(0.23),
            items = Some(Seq(
              SubItem(
                subItem = Some("000"),
                dueDate = Some("2018-2-14"),
                amount = Some(3400.00)
              ),
              SubItem(
                subItem = Some("001"),
                paymentReference = Some("XAGG0001234"),
                paymentAmount = Some(2000.00),
                paymentMethod = Some("Card")
              ),
              SubItem(
                subItem = Some("002"),
                paymentReference = Some("XAGG0005566"),
                paymentAmount = Some(1000.00),
                paymentMethod = Some("Card")
              )
            ))
          ),
          Transaction(
            chargeReference = Some("XM002610017788"),
            originalAmount = Some(1200.0),
            outstandingAmount = Some(1200.0),
            items = Some(Seq(
              SubItem(
                subItem = Some("000"),
                dueDate = Some("2018-7-1"),
                amount = Some(1200.00)
              )
            ))
          )
        ))
      )
      object TestFinancialTransactionsResponseAuditModel extends FinancialTransactionsResponseAuditModel(testRegime, testTransactions)

      "when calling the toTransactionsAuditJson method" should {

        "use the correct Audit Details taken from the Financial Transactions Model" in {
          val expected = Json.arr(
            Json.toJson(TransactionsAuditModel(
              chargeReference = Some("XM002610011594"),
              originalAmount = Some(3400),
              clearedAmount = Some(3000),
              outstandingAmount = Some(400),
              accruedInterest = Some(0.23),
              paymentReferences = Some(Seq("XAGG0001234", "XAGG0005566"))
            )),
            Json.toJson(TransactionsAuditModel(
              chargeReference = Some("XM002610017788"),
              originalAmount = Some(1200),
              clearedAmount = None,
              outstandingAmount = Some(1200),
              accruedInterest = None,
              paymentReferences = Some(Seq())
            ))
          )

          val actual = TestFinancialTransactionsResponseAuditModel.toTransactionsAuditJson(testTransactions.financialTransactions.get)

          actual shouldBe expected
        }
      }

      s"Have the correct transaction name of '$transactionName'" in {
        TestFinancialTransactionsResponseAuditModel.transactionName shouldBe transactionName
      }

      s"Have the correct audit event type of '$auditEvent'" in {
        TestFinancialTransactionsResponseAuditModel.auditType shouldBe auditEvent
      }

      "Have the correct details for the audit event" in {
        TestFinancialTransactionsResponseAuditModel.detail shouldBe Map(
          "taxRegime" -> testRegime.regimeType,
          "taxIdentifier" -> testRegime.id,
          "processingDate" -> testTransactions.processingDate.toString,
          "transactions" -> TestFinancialTransactionsResponseAuditModel.toTransactionsAuditJson(testTransactions.financialTransactions.get).toString
        )
      }
    }

    "not passed any transactions in the response" should {

      val noTransactions = FinancialTransactions(processingDate = "2018-03-07T22:55:56.987Z")
      object TestFinancialTransactionsResponseAuditModel extends FinancialTransactionsResponseAuditModel(testRegime, noTransactions)

      "Have the correct details for the audit event" in {
        TestFinancialTransactionsResponseAuditModel.detail shouldBe Map(
          "taxRegime" -> testRegime.regimeType,
          "taxIdentifier" -> testRegime.id,
          "processingDate" -> noTransactions.processingDate.toString,
          "transactions" -> "[]"
        )
      }
    }
  }
}
