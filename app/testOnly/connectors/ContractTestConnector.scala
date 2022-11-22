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

import akka.actor.ActorSystem
import config.MicroserviceAppConfig
import play.api.libs.ws.StandaloneWSResponse
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.mvc.Request
import utils.LoggerUtil

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class ContractTestConnector @Inject()(implicit appConfig: MicroserviceAppConfig) extends LoggerUtil {

  implicit val system: ActorSystem = ActorSystem()
  val wsClient: StandaloneAhcWSClient = StandaloneAhcWSClient()

  val host: String =
    if(appConfig.eisUrl.contains("localhost")) "https://admin.qa.tax.service.gov.uk/ifs/" else appConfig.eisUrl

  def callAPI(url: String)(implicit request: Request[_]): Future[StandaloneWSResponse] = {

    val apiUrl = host + url
    val headers: Seq[(String, String)] = if(request.headers.headers.exists(_._1 == "Authorization")) {
      request.headers.headers
    } else {
      Seq("Authorization" -> appConfig.eisToken) ++ request.headers.headers
    }

    logger.debug(s"[ContractTestConnector][callAPI] - Calling URL: $apiUrl")

    wsClient.url(apiUrl).addHttpHeaders(headers:_*).get()
  }
}
