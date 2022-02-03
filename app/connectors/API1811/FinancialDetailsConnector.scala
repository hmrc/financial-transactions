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
import connectors.API1811.httpParsers.FinancialTransactionsHttpParser.{FinancialTransactionsReads, FinancialTransactionsResponse}
import javax.inject.{Inject, Singleton}
import models.TaxRegime
import models.API1811.FinancialDataQueryParameters
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialDetailsConnector @Inject()(val http: HttpClient, val appConfig: MicroserviceAppConfig) extends LoggerUtil {

  private[connectors] def finanicalDataUrl(regime: TaxRegime) =
    s"${appConfig.eisUrl}/penalty/financial-data/${regime.idType}/${regime.id}/${regime.regimeType}"

  val eisHeaders = Seq("Authorization" -> s"Bearer ${appConfig.desToken}", "Environment" -> appConfig.eisEnvironment)

  def getFinancialDetails(regime: TaxRegime, queryParameters: FinancialDataQueryParameters)
                         (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[FinancialTransactionsResponse] = {

    val url = finanicalDataUrl(regime)
    val hc = headerCarrier

    logger.debug(s"[FinancialDataConnector][getFinancialData] - Calling GET $url \nHeaders: $desHeaders\n QueryParams: $queryParameters")
    http.GET[FinancialTransactionsResponse](url, queryParameters.toSeqQueryParams, desHeaders)(FinancialTransactionsReads,hc, ec)
    }
}
