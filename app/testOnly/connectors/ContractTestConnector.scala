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

package testOnly.connectors

import config.MicroserviceAppConfig
import play.api.mvc.Request
import testOnly.models.APIResponseModel
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads}
import utils.LoggerUtil

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ContractTestConnector @Inject()(httpClient: HttpClient)(implicit appConfig: MicroserviceAppConfig) extends LoggerUtil {

  val host: String =
    if(appConfig.eisUrl.contains("localhost")) "https://admin.qa.tax.service.gov.uk/ifs" else appConfig.eisUrl

  def callAPI(url: String, queryStringParameters: Seq[(String, String)])
             (implicit request: Request[_], headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[APIResponseModel] = {

    val apiUrl = host + url
    val headers: Seq[(String, String)] = if(request.headers.headers.exists(_._1 == "Authorization")) {
      request.headers.headers
    } else {
      Seq("Authorization" -> s"Bearer ${appConfig.eisToken}") ++ request.headers.headers
    }

    val hc = headerCarrier.copy(authorization = None)

    logger.debug("[ContractTestConnector][callAPI] - " +
     s"Calling URL: $apiUrl\nQuery params: $queryStringParameters\nHeaders: $headers")

    httpClient.GET(apiUrl, queryStringParameters, headers)(HttpReads[APIResponseModel], hc, ec)
  }
}
