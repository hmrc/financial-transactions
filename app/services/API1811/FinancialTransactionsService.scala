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
import connectors.API1811.FinancialDataConnector
import connectors.API1811.httpParsers.FinancialTransactionsHttpParser.FinancialTransactionsResponse
import models.{FinancialRequestQueryParameters, TaxRegime}
import uk.gov.hmrc.http.HeaderCarrier
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

class FinancialTransactionsService @Inject()(val connector: FinancialDataConnector,
                                             implicit val ec: ExecutionContext) extends LoggerUtil {

  def getFinancialTransactions(regime: TaxRegime, queryParameters: FinancialRequestQueryParameters)(
                               implicit headerCarrier: HeaderCarrier): Future[FinancialTransactionsResponse] = {

    logger.debug("[FinancialTransactionsService][getFinancialTransactions] " +
      s"Calling financialDataConnector with Regime: $regime\nParams: $queryParameters")
      connector.getFinancialData(regime, queryParameters)
    }
}
