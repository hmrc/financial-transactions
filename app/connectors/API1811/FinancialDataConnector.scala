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

package connectors.API1811

import java.util.UUID.randomUUID

import config.MicroserviceAppConfig
import connectors.API1811.httpParsers.FinancialTransactionsHttpParser.{FinancialTransactionsReads, FinancialTransactionsResponse}
import javax.inject.{Inject, Singleton}
import models.{FinancialRequestQueryParameters, TaxRegime}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialDataConnector @Inject()(val http: HttpClient, val appConfig: MicroserviceAppConfig) extends LoggerUtil {

  private[connectors] def financialDataUrl(regime: TaxRegime) =
    s"${appConfig.eisUrl}/penalty/financial-data/${regime.idType}/${regime.id}/${regime.regimeType}"

  def getFinancialData(regime: TaxRegime, queryParameters: FinancialRequestQueryParameters)
                         (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[FinancialTransactionsResponse] = {

    val eisHeaders = Seq("Authorization" -> s"Bearer ${appConfig.eisToken}","CorrelationId" -> randomUUID().toString, "Environment" -> appConfig.eisEnvironment)

    val url = financialDataUrl(regime)

    logger.debug(s"[FinancialDataConnector][getFinancialData] - Calling GET $url \nHeaders: $eisHeaders\n QueryParams: ${queryParameters.api1811QueryParams}")
    http.GET[FinancialTransactionsResponse](url, queryParameters.api1811QueryParams, eisHeaders)(FinancialTransactionsReads,hc, ec)
    }
}
