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

import connectors.API1811.httpParsers.FinancialTransactionsHttpHIPParser.FinancialTransactionsHIPResponse
import helpers.ComponentSpecBase
import models.API1811.Error
import models.{FinancialRequestQueryParameters, TaxRegime, VatRegime}
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import testData.FinancialDataHIP1811.{fullFinancialTransactionsHIP, financialDetailsInHipWrapperJson}

class FinancialDataConnectorHIPISpec extends ComponentSpecBase {

  val connector: FinancialDataHIPConnector             = app.injector.instanceOf[FinancialDataHIPConnector]
  val regimeType                                       = "VATC"
  val vatRegime: TaxRegime                             = VatRegime(id = "123456789")
  val queryParameters: FinancialRequestQueryParameters = FinancialRequestQueryParameters()

  "getFinancialDataHIP" should {

    "return a FinancialTransactionsHIPModel" when {

      s"a $CREATED response is received from financial transactions and the response can be parsed" in {

        stubPostRequest(
          url = s"/etmp/RESTAdapter/cross-regime/taxpayer/financial-data/query",
          responseStatus = CREATED,
          responseBody = financialDetailsInHipWrapperJson.toString()
        )

        val expectedResult = fullFinancialTransactionsHIP

        val result: FinancialTransactionsHIPResponse =
          await(connector.getFinancialDataHIP(vatRegime, queryParameters))
        result shouldBe Right(expectedResult)
      }

      s"a $BAD_REQUEST response is received from financial transactions" in {

        val errorResponseBody = Json.obj("code" -> 400, "reason" -> "BAD REQUEST").toString()
        stubPostRequest(
          url = s"/etmp/RESTAdapter/cross-regime/taxpayer/financial-data/query",
          responseStatus = BAD_REQUEST,
          errorResponseBody
        )

        val expectedResult = Left(Error(BAD_REQUEST, errorResponseBody))

        val result: FinancialTransactionsHIPResponse =
          await(connector.getFinancialDataHIP(vatRegime, queryParameters))
        result shouldBe expectedResult
      }
    }
  }
}
