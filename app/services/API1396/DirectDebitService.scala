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

import audit.AuditingService
import audit.models._
import connectors.API1396.DirectDebitDataConnector

import javax.inject.{Inject, Singleton}
import models.API1396._
import models.ErrorResponse
import uk.gov.hmrc.http.HeaderCarrier
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DirectDebitService @Inject()(val directDebitDataConnector: DirectDebitDataConnector,
                                   val auditingService: AuditingService) extends LoggerUtil {

  def checkDirectDebitExists(vrn: String)
                            (implicit headerCarrier: HeaderCarrier,
                             ec: ExecutionContext): Future[Either[ErrorResponse, DirectDebits]] = {

    logger.debug(s"[DirectDebitService][checkDirectDebitExists] Auditing Financial Transactions request")

    auditingService.audit(DirectDebitCheckRequestAuditModel(vrn))

    logger.debug(s"[DirectDebitService][checkDirectDebitExists] Calling directDebitConnector with vrn: $vrn")
    directDebitDataConnector.checkDirectDebitExists(vrn).map {
      case success@Right(hasDirectDebit) =>
        logger.debug(s"[DirectDebitService][checkDirectDebitExists] Auditing Financial Transactions response")
        auditingService.audit(DirectDebitsCheckResponseAuditModel(vrn, hasDirectDebit.directDebitMandateFound))
        success
      case error@Left(_) => error
    }
  }
}
