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

import config.MicroserviceAppConfig
import connectors.API1811.httpParsers.FinancialTransactionsHttpParser
import connectors.API1811.httpParsers.FinancialTransactionsHttpParser.FinancialTransactionsResponse
import models.{FinancialRequestQueryParameters, TaxRegime}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import utils.LoggerUtil
import java.util.UUID.randomUUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialDataConnector @Inject()(http: HttpClient, httpParser: FinancialTransactionsHttpParser)
                                      (implicit appConfig: MicroserviceAppConfig) extends LoggerUtil {

  private[connectors] def financialDataUrl(regime: TaxRegime) =
    s"${appConfig.eisUrl}/penalty/financial-data/${regime.idType}/${regime.id}/${regime.regimeType}"

  def getFinancialData(regime: TaxRegime, queryParameters: FinancialRequestQueryParameters)
                      (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[FinancialTransactionsResponse] = {

    val eisHeaders = Seq(
      "Authorization" -> s"Bearer ${appConfig.eisToken}",
      "CorrelationId" -> randomUUID().toString,
      "Environment" -> appConfig.eisEnvironment
    )

    val url = financialDataUrl(regime)

    logger.debug("[FinancialDataConnector][getFinancialData] - " +
      s"Calling GET $url \nHeaders: $eisHeaders\n QueryParams: ${queryParameters.queryParams1811}")

    http.GET[FinancialTransactionsResponse](
      url, queryParameters.queryParams1811, eisHeaders)(httpParser.FinancialTransactionsReads, hc, ec)
  }
}
