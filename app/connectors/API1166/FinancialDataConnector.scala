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

package connectors.API1166

import config.MicroserviceAppConfig
import connectors.API1166.httpParsers.FinancialTransactionsHttpParser
import connectors.httpParsers.DirectDebitCheckHttpParser.{DirectDebitCheckReads, HttpGetResult}
import models.API1166.FinancialTransactions
import models.{DirectDebits, FinancialRequestQueryParameters, TaxRegime}
import play.api.http.Status.NOT_FOUND
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import utils.LoggerUtil
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialDataConnector @Inject()(http: HttpClient,
                                       httpParser: FinancialTransactionsHttpParser)
                                      (implicit appConfig: MicroserviceAppConfig) extends LoggerUtil {

  private[connectors] def financialDataUrl(regime: TaxRegime) =
    s"${appConfig.desUrl}/enterprise/financial-data/${regime.idType}/${regime.id}/${regime.regimeType}"

  private[connectors] def directDebitUrl(vrn: String) =
    s"${appConfig.desUrl}/cross-regime/direct-debits/vatc/vrn/$vrn"

  val desHeaders = Seq("Authorization" -> s"Bearer ${appConfig.desToken}", "Environment" -> appConfig.desEnvironment)

  def getFinancialData(regime: TaxRegime, queryParameters: FinancialRequestQueryParameters)
                      (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[FinancialTransactions]] = {

    val url = financialDataUrl(regime)
    val hc = headerCarrier.copy(authorization = None)

    logger.debug(s"[FinancialDataConnector][getFinancialData] - Calling GET $url \nHeaders: $desHeaders\n QueryParams: ${queryParameters.queryParams1166}")
    http.GET(url, queryParameters.queryParams1166, desHeaders)(httpParser.FinancialTransactionsReads, hc, ec).map {
      case financialTransactions@Right(_) => financialTransactions
      case error@Left(response) => response.status match {
        case NOT_FOUND =>
          logger.debug("[FinancialDataConnector][getFinancialData] Error received: " + response)
          error
        case _ =>
          logger.warn("[FinancialDataConnector][getFinancialData] Error received: " + response)
          error
      }
    }
  }

  def checkDirectDebitExists(vrn: String)
                            (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[DirectDebits]] = {

    val url = directDebitUrl(vrn)
    val hc = headerCarrier.copy(authorization = None)

    logger.debug(s"[FinancialDataConnector][checkDirectDebitExists] - Calling GET $url \nHeaders: $desHeaders\n Vrn: $vrn")
    http.GET(url, headers = desHeaders)(DirectDebitCheckReads, hc, ec).map {
      case directDebitStatus@Right(_) => directDebitStatus
      case error@Left(message) =>
        logger.warn("[FinancialDataConnector][checkDirectDebitExists] DES Error Received. Message: " + message)
        error
    }
  }
}
