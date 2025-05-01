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

package connectors.API1811

import com.github.tomakehurst.wiremock.WireMockServer
import connectors.API1811.httpParsers.FinancialTransactionsHttpHIPParser.FinancialTransactionsHIPResponse
import connectors.API1811.httpParsers.FinancialTransactionsHttpParser.FinancialTransactionsResponse
import helpers.ComponentSpecBase
import models.API1811.Error
import models.{FinancialRequestQueryParameters, TaxRegime, VatRegime}
import org.scalatest.concurrent.ScalaFutures
import play.api.http.Status._
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import testData.FinancialDataHIP1811.{fullFinancialTransactionsHIP, fullFinancialTransactionsJsonHIP, multipleErrorsHIP, multipleErrorsHIPModel, singleErrorHIP, singleErrorHIPModel}

class FinancialDataConnectorHIPISpec extends ComponentSpecBase {

  val connector: FinancialDataHIPConnector = app.injector.instanceOf[FinancialDataHIPConnector]
  val regimeType = "VATC"
  val vatRegime: TaxRegime = VatRegime(id = "123456789")
  val queryParameters: FinancialRequestQueryParameters = FinancialRequestQueryParameters()

  "getFinancialDataHIP" should {

    "return a FinancialTransactionsHIPModel" when {

      s"a $OK response is received from financial transactions and the response can be parsed" in {

        stubPostRequest(
          url = s"/RESTAdapter/cross-regime/taxpayer/financial-data/query",
          responseStatus = OK,
          responseBody = fullFinancialTransactionsJsonHIP.toString()
        )

        val expectedResult = fullFinancialTransactionsHIP

        val result: FinancialTransactionsHIPResponse =
          await(connector.getFinancialDataHIP(vatRegime, queryParameters))
        result shouldBe Right(expectedResult)
      }
    }
  }
}