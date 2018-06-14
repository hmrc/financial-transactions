/*
 * Copyright 2017 HM Revenue & Customs
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

package connectors

import javax.inject.{Inject, Singleton}
import config.MicroserviceAppConfig
import connectors.httpParsers.DirectDebitCheckHttpParser.DirectDebitCheckReads
import connectors.httpParsers.FinancialTransactionsHttpParser._
import models.{FinancialDataQueryParameters, FinancialTransactions, TaxRegime}
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialDataConnector @Inject()(val http: HttpClient, val appConfig: MicroserviceAppConfig) {

  private[connectors] def financialDataUrl(regime: TaxRegime) =
    s"${appConfig.desUrl}/enterprise/financial-data/${regime.idType}/${regime.id}/${regime.regimeType}"

  private[connectors] def directDebitUrl(vrn: String) =
    s"${appConfig.desUrl}/cross-regime/direct-debits/vatc/vrn/$vrn"

  def getFinancialData(regime: TaxRegime, queryParameters: FinancialDataQueryParameters)
                      (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[FinancialTransactions]] = {

    val url = financialDataUrl(regime)
    val desHC = headerCarrier.copy(authorization =Some(Authorization(s"Bearer ${appConfig.desToken}")))
      .withExtraHeaders("Environment" -> appConfig.desEnvironment)

    Logger.debug(s"[FinancialDataConnector][getFinancialData] - Calling GET $url \nHeaders: $desHC\n QueryParams: $queryParameters")
    http.GET(url, queryParameters.toSeqQueryParams)(FinancialTransactionsReads, desHC, ec).map {
      case financialTransactions@Right(_) => financialTransactions
      case error@Left(message) =>
        Logger.warn("[FinancialDataConnector][getFinancialData] DES Error Received. Message: " + message)
        error
    }
  }

  def checkDirectDebitExists(vrn: String)
                            (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[Boolean]] = {

    val url = directDebitUrl(vrn)
    val desHC = headerCarrier.copy(authorization =Some(Authorization(s"Bearer ${appConfig.desToken}")))
      .withExtraHeaders("Environment" -> appConfig.desEnvironment)

    Logger.debug(s"[FinancialDataConnector][checkDirectDebitExists] - Calling GET $url \nHeaders: $desHC\n Vrn: $vrn")
    http.GET(url)(DirectDebitCheckReads, desHC, ec).map {
      case directDebitStatus@Right(_) => directDebitStatus
      case error@Left(message) =>
        Logger.warn("[FinancialDataConnector][checkDirectDebitExists] DES Error Received. Message: " + message)
        error
    }
  }
}
