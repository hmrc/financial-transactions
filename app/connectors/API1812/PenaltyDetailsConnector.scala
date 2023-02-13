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

package connectors.API1812

import config.MicroserviceAppConfig
import connectors.API1812.httpParsers.PenaltyDetailsHttpParser.{PenaltyDetailsReads, PenaltyDetailsResponse}
import models.{PenaltyDetailsQueryParameters, TaxRegime}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import utils.LoggerUtil

import java.util.UUID.randomUUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PenaltyDetailsConnector @Inject()(val http: HttpClient, val appConfig: MicroserviceAppConfig) extends LoggerUtil {

  private[connectors] def penaltyDetailsUrl(regime: TaxRegime) =
    s"${appConfig.eisUrl}/penalty/details/${regime.regimeType}/${regime.idType}/${regime.id}"

  def getPenaltyDetails(regime: TaxRegime, queryParameters: PenaltyDetailsQueryParameters)
                       (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[PenaltyDetailsResponse] = {

    val eisHeaders = Seq(
      "Authorization" -> s"Bearer ${appConfig.eisToken}",
      "CorrelationId" -> randomUUID().toString,
      "Environment" -> appConfig.eisEnvironment
    )

    val hc = headerCarrier.copy(authorization = None)
    val url = penaltyDetailsUrl(regime)

    logger.debug("[PenaltyDetailsConnector][getPenaltyDetails] - " +
      s"Calling GET $url \nHeaders: $eisHeaders\n QueryParams: $queryParameters")
    http.GET[PenaltyDetailsResponse](url, queryParameters.toSeqQueryParams, eisHeaders)(PenaltyDetailsReads,hc, ec)
  }
}
