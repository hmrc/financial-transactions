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

package connectors.API1396

import config.MicroserviceAppConfig
import connectors.API1396.httpParsers.DirectDebitCheckHttpParser.{DirectDebitCheckReads, HttpGetResult}
import models.API1396.DirectDebits
import models.{Error, ErrorResponse}
import play.api.http.Status.BAD_GATEWAY
import uk.gov.hmrc.http.{HeaderCarrier, HttpException, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import utils.LoggerUtil

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DirectDebitDataConnector @Inject()(http: HttpClientV2)
                                        (implicit appConfig: MicroserviceAppConfig) extends LoggerUtil {

  private[connectors] def directDebitUrl(vrn: String) =
    s"${appConfig.desUrl}/cross-regime/direct-debits/vatc/vrn/$vrn"

  val desHeaders: Seq[(String, String)] =
    Seq("Authorization" -> s"Bearer ${appConfig.desToken}", "Environment" -> appConfig.desEnvironment)

  def checkDirectDebitExists(vrn: String)
                            (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[DirectDebits]] = {

    val urlString = directDebitUrl(vrn)

    logger.debug(s"[FinancialDataConnector][checkDirectDebitExists] - Calling GET $urlString \nHeaders: $desHeaders\n Vrn: $vrn")
    val hc = headerCarrier.copy(authorization = None)
    http.get(url"$urlString")(hc).setHeader(desHeaders: _*).execute[HttpGetResult[DirectDebits]](DirectDebitCheckReads, ec).map {
      case directDebitStatus@Right(_) => directDebitStatus
      case error@Left(message) =>
        logger.warn("[FinancialDataConnector][checkDirectDebitExists] DES Error Received. Message: " + message)
        error
    }.recover {
      case ex: HttpException =>
        logger.warn(s"[DirectDebitDataConnector][checkDirectDebitExists] - HTTP exception received: ${ex.message}")
        Left(ErrorResponse(BAD_GATEWAY, Error("BAD_GATEWAY", ex.message)))
    }
  }
}
