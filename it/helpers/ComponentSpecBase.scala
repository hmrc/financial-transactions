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

package helpers

import binders.FinancialTransactionsBinders
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, stubFor}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import config.MicroserviceAppConfig
import helpers.servicemocks.AuthStub
import models.RequestQueryParameters
import org.scalatest._
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSResponse
import play.api.{Application, Environment, Mode}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HttpClient

trait ComponentSpecBase extends TestSuite with CustomMatchers
  with GuiceOneServerPerSuite with ScalaFutures with IntegrationPatience
  with WiremockHelper with BeforeAndAfterEach with BeforeAndAfterAll with Eventually {

  val mockHost: String = WiremockHelper.wiremockHost
  val mockPort: String = WiremockHelper.wiremockPort.toString
  val mockUrl = s"http://$mockHost:$mockPort"
  val httpClient : HttpClient = app.injector.instanceOf[HttpClient]
  val appConfig: MicroserviceAppConfig = app.injector.instanceOf[MicroserviceAppConfig]

  def config: Map[String, String] = Map(
    "microservice.services.auth.host" -> mockHost,
    "microservice.services.auth.port" -> mockPort,
    "microservice.services.des.url" -> mockUrl,
    "microservice.services.eis.url" -> mockUrl
  )

  def stubGetRequest(url: String, returnStatus: Int, returnBody: String): StubMapping =
    stubFor(get(url).willReturn(
      aResponse()
        .withStatus(returnStatus)
        .withBody(returnBody)
    ))

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(config)
    .build

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWiremock()
  }

  override def afterAll(): Unit = {
    stopWiremock()
    super.afterAll()
  }

  object FinancialTransactions {
    def get(uri: String): WSResponse = await(buildClient(uri).get())
    def getFinancialTransactions(idType: String, id: String, queryParameters: RequestQueryParameters): WSResponse = {
      val queryParamStart = if(queryParameters.hasQueryParameters) "?" else ""
      get(s"/financial-transactions/$idType/$id$queryParamStart" +
        FinancialTransactionsBinders.financialDataQueryBinder.unbind("", queryParameters))
    }
  }

  def isAuthorised(authorised: Boolean = true): StubMapping = {
    if(authorised){
      Given("I wiremock stub an authorised user response")
      AuthStub.stubAuthorised()
    } else {
      Given("I wiremock stub an unauthorised user response")
      AuthStub.stubUnauthorised()
    }
  }
}
