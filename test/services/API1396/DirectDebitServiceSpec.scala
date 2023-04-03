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

package services.API1396

import audit.models._
import base.SpecBase
import mocks.audit.MockAuditingService
import mocks.connectors.Mock1396DirectDebitDataConnector
import models.API1396.DirectDebits
import models._
import play.api.http.Status
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.API1396.TestConstants.multipleDirectDebits

class DirectDebitServiceSpec extends SpecBase with Mock1396DirectDebitDataConnector with MockAuditingService {

  object TestDirectDebitService extends DirectDebitService(mockDirectDebitDataConnector, mockAuditingService)

  "The DirectDebitService.checkDirectDebitExists method" should {

    val vrn = "123456"

    "CheckDirectDebitExists Returns direct debit details when a success response is returned from the Connector" in {

      val successResponse: Either[Nothing, DirectDebits] = Right(multipleDirectDebits)
      setupMockCheckDirectDebitExists(vrn)(successResponse)

      setupMockAuditEventResponse(DirectDebitsCheckResponseAuditModel(vrn, hasDirectDebit = true))
      setupMockAuditEventResponse(DirectDebitCheckRequestAuditModel(vrn))

      val actual: Either[ErrorResponse, DirectDebits] = await(TestDirectDebitService.checkDirectDebitExists(vrn))

      actual shouldBe successResponse

      verifyAuditEvent(DirectDebitCheckRequestAuditModel(vrn))
      verifyAuditEvent(DirectDebitsCheckResponseAuditModel(vrn, hasDirectDebit = true))

    }

    "CheckDirectDebitExists Return Error when a single error is returned from the Connector" in {

      val singleErrorResponse: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, Error("CODE", "REASON")))

      setupMockCheckDirectDebitExists(vrn)(singleErrorResponse)

      val actual: Either[ErrorResponse, DirectDebits] = await(TestDirectDebitService.checkDirectDebitExists(vrn))

      actual shouldBe singleErrorResponse

    }

    "CheckDirectDebitExists Return a MultiError when multiple error responses are returned from the Connector" in {

      val multiErrorResponse: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, MultiError(Seq(
        Error("CODE 1", "REASON 1"),
        Error("CODE 2", "REASON 2")
      ))))

      setupMockCheckDirectDebitExists(vrn)(multiErrorResponse)

      val actual: Either[ErrorResponse, DirectDebits] = await(TestDirectDebitService.checkDirectDebitExists(vrn))

      actual shouldBe multiErrorResponse

    }
  }
}
