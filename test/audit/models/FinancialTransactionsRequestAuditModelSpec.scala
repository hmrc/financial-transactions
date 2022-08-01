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

package audit.models

import _root_.models.{FinancialRequestQueryParameters, _}
import base.SpecBase
import models.FinancialRequestQueryParameters._
import utils.ImplicitDateFormatter._

class FinancialTransactionsRequestAuditModelSpec extends SpecBase {

  val transactionName = "financial-transactions-request"
  val auditEvent = "financialTransactionsRequest"

  val testRegime = VatRegime("XQIT00000000001")

  "The FinancialTransactionsRequestAuditModel" when {

    "all QueryParameters are passed to it" should {

      val testQueryParams = FinancialRequestQueryParameters(
        fromDate = Some("2018-01-01"),
        toDate = Some("2019-01-01"),
        onlyOpenItems = Some(true)
      )
      object TestFinancialTransactionsRequestAuditModel extends FinancialTransactionsRequestAuditModel(testRegime, testQueryParams)

      s"Have the correct transaction name of '$transactionName'" in {
        TestFinancialTransactionsRequestAuditModel.transactionName shouldBe transactionName
      }

      s"Have the correct audit event type of '$auditEvent'" in {
        TestFinancialTransactionsRequestAuditModel.auditType shouldBe auditEvent
      }

      "Have the correct details for the audit event" in {
        TestFinancialTransactionsRequestAuditModel.detail shouldBe Seq(
          "taxRegime" -> testRegime.regimeType,
          "taxIdentifier" -> testRegime.id,
          dateFromKey -> testQueryParams.fromDate.get.toString,
          dateToKey -> testQueryParams.toDate.get.toString,
          onlyOpenItemsKey -> testQueryParams.onlyOpenItems.get.toString
        )
      }
    }

    "not passed any Query Parameters" should {

      val noQueryParams = FinancialRequestQueryParameters()
      object TestFinancialTransactionsRequestAuditModel extends FinancialTransactionsRequestAuditModel(testRegime, noQueryParams)

      "Have the correct details for the audit event" in {
        TestFinancialTransactionsRequestAuditModel.detail shouldBe Seq(
          "taxRegime" -> testRegime.regimeType,
          "taxIdentifier" -> testRegime.id
        )
      }
    }
  }
}
