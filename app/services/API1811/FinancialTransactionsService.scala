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

package services.API1811

import com.google.inject.Inject
import config.MicroserviceAppConfig
import connectors.API1811.{FinancialDataConnector, FinancialDataHIPConnector}
import connectors.API1811.httpParsers.FinancialTransactionsHttpParser.FinancialTransactionsResponse
import models.API1811.FinancialTransactions
import models.{FinancialRequestQueryParameters, TaxRegime}
import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier
import utils.API1811.ChargeTypes
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

class FinancialTransactionsService @Inject()(val connector: FinancialDataConnector,
                                             val hipConnector: FinancialDataHIPConnector,
                                             implicit val ec: ExecutionContext) extends LoggerUtil {
  def getFinancialTransactions(regime: TaxRegime, queryParameters: FinancialRequestQueryParameters
                              )(implicit headerCarrier: HeaderCarrier, appConfig: MicroserviceAppConfig, request: Request[_]): Future[FinancialTransactionsResponse] = {

    if (appConfig.features.enable1811HIPCall()) {
      logger.debug("[FinancialTransactionsService][getFinancialTransactions] - HIP Call enabled, calling FinancialDataHIPConnector")
      hipConnector.getFinancialDataHIP(regime, queryParameters).map {
        case Right(financialTransactionsHIP) =>
          logger.debug("[FinancialTransactionsService][getFinancialTransactions] - Successfully retrieved HIP financial transactions.")
          val mappedToIf = FinancialTransactions(
            documentDetails = financialTransactionsHIP.financialData.documentDetails
          )
          Right(mappedToIf)

        case Left(error) =>
          logger.warn("[FinancialTransactionsService][getFinancialTransactions] - HIP call returned an error.")
          Left(error)
      }
    } else {
      logger.debug("[FinancialTransactionsService][getFinancialTransactions] " +
        s"Calling financialDataConnector with Regime: $regime\nParams: $queryParameters")
      connector.getFinancialData(regime, queryParameters).map {
        case Right(financialTransactions) =>
          infoLog(s"[FinancialTransactionsService][getFinancialTransactions] successfully retrieved financial transactions. Attempting to remove invalid charges")
          Right(
            financialTransactions.copy(documentDetails = ChargeTypes.removeInvalidCharges(financialTransactions.documentDetails))
          )
        case response: FinancialTransactionsResponse => response
      }
    }
  }
}
