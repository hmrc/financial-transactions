/*
 * Copyright 2025 HM Revenue & Customs
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
import connectors.API1812.httpParsers.HIPPenaltyDetailsHttpParser.{HIPPenaltyDetailsReads, HIPPenaltyDetailsResponse}
import models.API1812.Error
import models.{PenaltyDetailsQueryParameters, TaxRegime}
import play.api.http.Status.BAD_GATEWAY
import uk.gov.hmrc.http.{HeaderCarrier, HttpException, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import utils.LoggerUtil

import java.time.{LocalDateTime, ZoneOffset}
import java.util.UUID.randomUUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HIPPenaltyDetailsConnector @Inject()(val http: HttpClientV2, val appConfig: MicroserviceAppConfig) extends LoggerUtil {

  private[connectors] def penaltyDetailsUrl() =
    s"${appConfig.hipUrl}/etmp/RESTAdapter/cross-regime/taxpayer/penalties"

  def getPenaltyDetails(regime: TaxRegime, queryParameters: PenaltyDetailsQueryParameters)
                       (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HIPPenaltyDetailsResponse] = {

    val correlationID = randomUUID().toString
    val receiptDate = LocalDateTime.now(ZoneOffset.UTC).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))

    val hipHeaders = Seq(
      "Authorization" -> s"Basic ${appConfig.hipToken}",
      "correlationid" -> correlationID,
      "X-Originating-System" -> "MDTP",
      "X-Receipt-Date" -> receiptDate,
      "X-Transmitting-System" -> "HIP"
    )

    val urlString = penaltyDetailsUrl()

    val hipQueryParams = Seq(
      "taxRegime" -> regime.regimeType,
      "idType" -> regime.idType,
      "idNumber" -> regime.id
    ) ++ queryParameters.toSeqQueryParams

    logger.info("[HIPPenaltyDetailsConnector][getPenaltyDetails] - " +
      s"Calling GET $urlString \nHeaders: $hipHeaders\n QueryParams: $hipQueryParams")

    val hc = headerCarrier.copy(authorization = None)
    http.get(url"$urlString?$hipQueryParams")(hc)
      .setHeader(hipHeaders: _*)
      .execute[HIPPenaltyDetailsResponse](HIPPenaltyDetailsReads, ec)
    .recover {
      case ex: HttpException =>
        logger.warn(s"[HIPPenaltyDetailsConnector][getPenaltyDetails] - HTTP exception received: ${ex.message}")
        Left(Error(BAD_GATEWAY, ex.message))
    }
  }
}
