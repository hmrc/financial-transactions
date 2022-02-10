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

package services

import javax.inject.{Inject, Singleton}
import audit.AuditingService
import audit.models._
import connectors.API1166.httpParsers.FinancialDataConnector
import models.API1166._
import models._
import uk.gov.hmrc.http.HeaderCarrier
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialTransactionsService @Inject()(val financialDataConnector: FinancialDataConnector,
                                             val auditingService: AuditingService) extends LoggerUtil {

  def getFinancialTransactions(regime: TaxRegime,
                               queryParameters: FinancialDataQueryParameters)
                              (implicit headerCarrier: HeaderCarrier,
                               ec: ExecutionContext): Future[Either[ErrorResponse, FinancialTransactions]] = {

    logger.debug(s"[FinancialTransactionsService][getFinancialTransactions] Auditing Financial Transactions request")
    auditingService.audit(FinancialTransactionsRequestAuditModel(regime, queryParameters))

    logger.debug("[FinancialTransactionsService][getFinancialTransactions] " +
      s"Calling financialDataConnector with Regime: $regime\nParams: $queryParameters")
    financialDataConnector.getFinancialData(regime, queryParameters).map {
      case success@Right(financialTransactions) =>
        logger.debug(s"[FinancialTransactionsService][getFinancialTransactions] Auditing Financial Transactions response")
        auditingService.audit(FinancialTransactionsResponseAuditModel(regime, financialTransactions))
        success
      case error@Left(_) => error
    }
  }


  def checkDirectDebitExists(vrn: String)
                            (implicit headerCarrier: HeaderCarrier,
                             ec: ExecutionContext): Future[Either[ErrorResponse, DirectDebits]] = {

    logger.debug(s"[FinancialTransactionsService][checkDirectDebitExists] Auditing Financial Transactions request")

    auditingService.audit(DirectDebitCheckRequestAuditModel(vrn))

    logger.debug(s"[FinancialTransactionsService][checkDirectDebitExists] Calling directDebitConnector with vrn: $vrn")
    financialDataConnector.checkDirectDebitExists(vrn).map {
      case success@Right(hasDirectDebit) =>
        logger.debug(s"[FinancialTransactionsService][checkDirectDebitExists] Auditing Financial Transactions response")
        auditingService.audit(DirectDebitsCheckResponseAuditModel(vrn, hasDirectDebit.directDebitMandateFound))
        success
      case error@Left(_) => error
    }
  }
}
