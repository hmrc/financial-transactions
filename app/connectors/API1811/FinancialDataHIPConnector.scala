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
import connectors.API1811.httpParsers.FinancialTransactionsHttpHIPParser.{FinancialTransactionsFailureResponse, FinancialTransactionsHIPReads, FinancialTransactionsHIPResponse}
import models.API1811.{FinancialRequestHIP, FinancialRequestHIPHelper}
import models.{FinancialRequestQueryParameters, TaxRegime}
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import utils.LoggerUtil

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialDataHIPConnector @Inject()(http: HttpClient)
                                         (implicit appConfig: MicroserviceAppConfig) extends LoggerUtil {


  def getFinancialDataHIP(regime: TaxRegime, queryParameters: FinancialRequestQueryParameters)
                      (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[FinancialTransactionsHIPResponse] = {

    val correlationId = UUID.randomUUID().toString
    val hipHeaders = buildHIPHeaders(correlationId)

    val url = s"${appConfig.hipUrl}/etmp/RESTAdapter/cross-regime/taxpayer/financial-data/query"
    val requestBody : FinancialRequestHIP = FinancialRequestHIPHelper.HIPRequestBody(regime, queryParameters)
    val jsonBody = Json.toJson(requestBody)

    http.POST[JsValue, FinancialTransactionsHIPResponse](url, jsonBody, hipHeaders)(
      implicitly,
      implicitly,
      headerCarrier,
      ec
    ).recover {
      case ex: Exception =>
        logger.warn(s"[FinancialDataHIPConnector][getFinancialDataHIP] HIP HTTP exception received: ${ex.getMessage}")
        Left(FinancialTransactionsFailureResponse(INTERNAL_SERVER_ERROR))
    }
  }
  private def buildHIPHeaders(correlationId: String): Seq[(String, String)] = Seq(
    "Authorization" -> s"Basic ${appConfig.hipToken}",
    appConfig.hipServiceOriginatorIdKeyV1 -> appConfig.hipServiceOriginatorIdV1,
    "correlationid" -> correlationId,
    "X-Originating-System" -> "MDTP",
    "X-Receipt-Date"       -> DateTimeFormatter.ISO_INSTANT.format(Instant.now().truncatedTo(ChronoUnit.SECONDS)),
    "X-Transmitting-System" -> "HIP"
  )
}