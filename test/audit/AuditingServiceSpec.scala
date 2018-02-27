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

package audit

import audit.mocks.MockAuditingConnector
import audit.models.AuditModel
import base.SpecBase
import uk.gov.hmrc.play.audit.AuditExtensions
import uk.gov.hmrc.play.audit.http.connector.AuditResult.{Success, Failure, Disabled}
import uk.gov.hmrc.play.audit.model.DataEvent

class AuditingServiceSpec extends SpecBase with MockAuditingConnector {

  object TestAuditingService extends AuditingService(mockAppConfig, mockAuditConnector)

  object TestAuditModel extends AuditModel {
    override val auditType: String = "testEvent"
    override val transactionName: String = "testTransaction"
    override val detail: Map[String, String] = Map(
      "testDetailA" -> "foo",
      "testDetailB" -> "bar"
    )
  }

  val testAuditData: DataEvent = TestAuditingService.toDataEvent(mockAppConfig.appName, TestAuditModel, "/dummy/referer/path")

  "AuditingService.toDataEvent method" should {

    "Correctly format the data to a Audit DataEvent Model" which {

      "has the correct auditSource" in {
        testAuditData.auditSource shouldBe mockAppConfig.appName
      }

      "has the correct auditType" in {
        testAuditData.auditType shouldBe TestAuditModel.auditType
      }

      "has the correct audit tags" in {
        testAuditData.tags shouldBe AuditExtensions.auditHeaderCarrier(hc).toAuditTags(TestAuditModel.transactionName, "/dummy/referer/path")
      }

      "has the correct audit detail" in {
        testAuditData.detail shouldBe AuditExtensions.auditHeaderCarrier(hc).toAuditDetails(TestAuditModel.detail.toSeq: _*)
      }
    }
  }

  "AuditingService.audit" when {

    "Provided with an AuditModel" should {

      "and a success response is mocked should extract the data and pass it into the AuditConnector" in {
        mockSendAuditEvent(testAuditData)(Success)
        await(TestAuditingService.audit(TestAuditModel)) shouldBe Success
        verifySendAuditEvent(testAuditData)
      }

      "and a failure response is mocked should extract the data and pass it into the AuditConnector" in {
        mockSendAuditEvent(testAuditData)(Failure("Oh no, an error!"))
        await(TestAuditingService.audit(TestAuditModel)) shouldBe Failure("Oh no, an error!")
        verifySendAuditEvent(testAuditData)
      }

      "and auditing is disabled response is mocked should extract the data and pass it into the AuditConnector" in {
        mockSendAuditEvent(testAuditData)(Disabled)
        await(TestAuditingService.audit(TestAuditModel)) shouldBe Disabled
        verifySendAuditEvent(testAuditData)
      }
    }
  }
}
