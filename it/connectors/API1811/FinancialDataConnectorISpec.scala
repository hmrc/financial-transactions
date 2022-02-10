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

package connectors

import connectors.API1811.httpParsers.FinancialTransactionsHttpParser.FinancialTransactionsResponse
import helpers.ComponentSpecBase
import models.API1811.{Error, FinancialDataQueryParameters}
import models.{TaxRegime, VatRegime}
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import testData.FinancialData1811.{fullFinancialTransactions, fullFinancialTransactionsJson}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

class FinancialDataConnectorISpec extends ComponentSpecBase {

  val connector: API1811.FinancialDataConnector = new API1811.FinancialDataConnector(httpClient, appConfig)
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  val regimeType = "VATC"
  val invalidRegimeType = "9"
  val vatRegime: TaxRegime = VatRegime(id = "12345678")
  val queryParameters: FinancialDataQueryParameters = FinancialDataQueryParameters()

  def generateUrl(regimeType: String): String =  s"/penalty/financial-data/VRN/12345678/$regimeType" +
    s"?onlyOpenItems=false&includeLocks=true&calculateAccruedInterest=true&removePOA=true&customerPaymentInformation=true"

  "getFinancialData" should {

    "return a FinancialTransactionsModel" when {

      s"an $OK response is received from financial transactions and the response can be parsed" in {

        stubGetRequest(
          generateUrl(regimeType),
          OK,
          fullFinancialTransactionsJson.toString()
        )

        val expectedResult = fullFinancialTransactions

        val result: FinancialTransactionsResponse =
          await(connector.getFinancialData(vatRegime,queryParameters))
        result shouldBe Right(expectedResult)

      }

      "return an ErrorModel" when {

        s"a $OK response is received from financialTransactions, but the response cannot be parsed" in {

          stubGetRequest(
            generateUrl(regimeType),
            OK,
            """{"foo":"bar"}"""
          )

          val expectedResult = Left(Error(BAD_REQUEST,
            "UNEXPECTED_JSON_FORMAT - The downstream service responded with json which did not match the expected format."))

          val result: FinancialTransactionsResponse =
            await(connector.getFinancialData(vatRegime, queryParameters))
          result shouldBe expectedResult

        }
        s"a $BAD_REQUEST response is received from financial transactions" in {

          stubGetRequest(
            generateUrl(invalidRegimeType),
            BAD_REQUEST,
            Json.obj("code" -> 400,
              "reason" -> "BAD REQUEST").toString()
          )

            val expectedResult = Left(Error(BAD_REQUEST,
              "UNEXPECTED_JSON_FORMAT - The downstream service responded with json which did not match the expected format."))

          val result: FinancialTransactionsResponse =
            await(connector.getFinancialData(vatRegime, queryParameters))
          result shouldBe expectedResult
        }
        s"a $NOT_FOUND response is received from Financial Transactions" in {

          stubGetRequest(
            generateUrl(regimeType),
            NOT_FOUND,
            Json.obj("code" -> 404,
              "reason" -> "A bad request has been made, this could be due to one or more issues with the request").toString()
          )

          val expectedResult = Left(Error(NOT_FOUND,
            """{"code":404,"reason":"A bad request has been made, this could be due to one or more issues with the request"}"""))

          val result: FinancialTransactionsResponse =
            await(connector.getFinancialData(vatRegime, queryParameters))
          result shouldBe expectedResult
        }
        "an unexpected response is received from financial transactions" in {

          stubGetRequest(
            generateUrl(regimeType),
            REQUEST_TIMEOUT,
            "AN UNKNOWN ERROR HAS OCCURRED"
          )

          val expectedResult = Left(Error( REQUEST_TIMEOUT, "AN UNKNOWN ERROR HAS OCCURRED"))

          val result: FinancialTransactionsResponse =
            await(connector.getFinancialData(vatRegime, queryParameters))
          result shouldBe expectedResult
        }
      }
    }

  }

}
