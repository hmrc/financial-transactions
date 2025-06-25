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
import models.API1812.Error
import play.api.http.Status.{BAD_GATEWAY, NOT_FOUND}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpException}
import utils.LoggerUtil

import java.time.{LocalDateTime, ZoneOffset}
import java.util.UUID.randomUUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PenaltyDetailsConnector @Inject()(val http: HttpClient, val appConfig: MicroserviceAppConfig) extends LoggerUtil {

  private[connectors] def penaltyDetailsUrl() =
    s"${appConfig.hipUrl}/RESTAdapter/cross-regime/taxpayer/penalties"

  def getPenaltyDetails(regime: TaxRegime, queryParameters: PenaltyDetailsQueryParameters)
                       (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[PenaltyDetailsResponse] = {

    val correlationID = randomUUID().toString
    val receiptDate = LocalDateTime.now(ZoneOffset.UTC).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))

    val hipHeaders = Seq(
      "Authorization" -> s"Bearer ${appConfig.hipToken}",
      "correlationid" -> correlationID,
      "Environment" -> appConfig.hipEnvironment,
      "X-Originating-System" -> appConfig.hipServiceOriginatorIdKey,
      "X-Receipt-Date" -> receiptDate,
      "X-Transmitting-System" -> appConfig.hipServiceOriginatorId
    )

    val hc = headerCarrier.copy(authorization = None)
    val url = penaltyDetailsUrl()

    val hipQueryParams = Seq(
      "taxRegime" -> regime.regimeType,
      "idType" -> regime.idType,
      "idNumber" -> regime.id
    ) ++ queryParameters.toSeqQueryParams

    logger.debug("[PenaltyDetailsConnector][getPenaltyDetails] - " +
      s"Calling GET $url \nHeaders: $hipHeaders\n QueryParams: $hipQueryParams")

    http.GET(url, hipQueryParams, hipHeaders)(PenaltyDetailsReads, hc, ec).map {
      case Left(error) if error.code != NOT_FOUND =>
        logger.warn(s"[PenaltyDetailsConnector][getPenaltyDetails] Unexpected error returned by HIP. " +
          s"Status code: ${error.code}, Body: ${error.reason.trim}, Correlation ID: $correlationID")
        Left(error)
      case expectedResponse => expectedResponse
    }.recover {
      case ex: HttpException =>
        logger.warn(s"[PenaltyDetailsConnector][getPenaltyDetails] - HTTP exception received: ${ex.message}")
        Left(Error(BAD_GATEWAY, ex.message))
    }
  }
}
