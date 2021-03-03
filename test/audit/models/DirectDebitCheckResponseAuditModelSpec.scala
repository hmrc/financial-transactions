/*
 * Copyright 2021 HM Revenue & Customs
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

class DirectDebitCheckResponseAuditModelSpec extends SpecBase {

  "The DirectDebitsCheckResponseAuditModel" when {

    val transactionName: String = "direct-debit-check-response"
    val auditEvent: String = "directDebitCheckResponse"
    val vrn: String = "555555555"

    "passed hasDirectDebit as true in the response" should {

      object TestDirectDebitsCheckResponseAuditModel extends DirectDebitsCheckResponseAuditModel(vrn, hasDirectDebit = true)

      s"Have the correct transaction name of '$transactionName'" in {
        TestDirectDebitsCheckResponseAuditModel.transactionName shouldBe transactionName
      }

      s"Have the correct audit event type of '$auditEvent'" in {
        TestDirectDebitsCheckResponseAuditModel.auditType shouldBe auditEvent
      }

      "Have the correct details for the audit event" in {

        val (_, vrnDetail: String) = TestDirectDebitsCheckResponseAuditModel.detail.head
        val (_, hasDirectDebitDetail: String) = TestDirectDebitsCheckResponseAuditModel.detail.last

        vrnDetail shouldBe vrn
        hasDirectDebitDetail shouldBe "true"
      }
    }

    "passed hasDirectDebit as false in the response" should {
      object TestDirectDebitsCheckResponseAuditModel extends DirectDebitsCheckResponseAuditModel(vrn = vrn, hasDirectDebit = false)

      "Have the correct details for the audit event" in {
        val (_, vrnDetail: String) = TestDirectDebitsCheckResponseAuditModel.detail.head
        val (_, hasDirectDebitDetail: String) = TestDirectDebitsCheckResponseAuditModel.detail.last

        vrnDetail shouldBe vrn
        hasDirectDebitDetail shouldBe "false"

      }
    }
  }
}
