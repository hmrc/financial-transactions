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

package connectors.API1811

import config.MicroserviceAppConfig
import connectors.API1811.httpParsers.FinancialTransactionsHttpHIPParser.{FinancialTransactionsHIPReads, FinancialTransactionsHIPResponse}
import models.API1811.Error
import models.{FinancialRequestQueryParameters, TaxRegime}
import play.api.http.Status.BAD_GATEWAY
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpException, StringContextOps}
import utils.LoggerUtil

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialDataHIPConnector @Inject() (http: HttpClientV2)(implicit appConfig: MicroserviceAppConfig) extends LoggerUtil {

  def getFinancialDataHIP(regime: TaxRegime, queryParameters: FinancialRequestQueryParameters)(implicit
      headerCarrier: HeaderCarrier,
      ec: ExecutionContext): Future[FinancialTransactionsHIPResponse] = {

    val correlationId = UUID.randomUUID().toString
    val hipHeaders    = buildHIPHeaders(correlationId)

    val url             = s"${appConfig.hipUrl}/etmp/RESTAdapter/cross-regime/taxpayer/financial-data/query"
    val jsonRequestBody = queryParameters.toQueryRequestBody(regime)

    logger.info(
      "[FinancialDataHIPConnector][getFinancialDataHIP] - " +
        s"Calling POST $url \nHeaders: $hipHeaders\n QueryParamBody: $jsonRequestBody")

    http
      .post(url"$url")(headerCarrier)
      .setHeader(hipHeaders: _*)
      .withBody(jsonRequestBody)
      .execute[FinancialTransactionsHIPResponse](FinancialTransactionsHIPReads, ec)
      .recover { case ex: HttpException =>
        logger.warn(s"[FinancialDataHIPConnector][getFinancialDataHIP] - HTTP exception received: ${ex.message}")
        Left(Error(BAD_GATEWAY, ex.message))
      }
  }
  private def buildHIPHeaders(correlationId: String): Seq[(String, String)] = Seq(
    "Authorization"                       -> s"Basic ${appConfig.hipToken}",
    appConfig.hipServiceOriginatorIdKeyV1 -> appConfig.hipServiceOriginatorIdV1,
    "correlationid"                       -> correlationId,
    "X-Originating-System"                -> "MDTP",
    "X-Receipt-Date"                      -> DateTimeFormatter.ISO_INSTANT.format(Instant.now().truncatedTo(ChronoUnit.SECONDS)),
    "X-Transmitting-System"               -> "HIP"
  )
}
