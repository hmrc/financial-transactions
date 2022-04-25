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

package connectors.API1811

import connectors.API1811
import connectors.API1811.httpParsers.FinancialTransactionsHttpParser.FinancialTransactionsResponse
import helpers.ComponentSpecBase
import models.API1811.Error
import models.{FinancialRequestQueryParameters, TaxRegime, VatRegime}
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import testData.FinancialData1811.{fullFinancialTransactions, fullFinancialTransactionsJsonEIS}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

class FinancialDataConnectorISpec extends ComponentSpecBase {

  val connector: API1811.FinancialDataConnector = new API1811.FinancialDataConnector(httpClient, appConfig)
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  val regimeType = "VATC"
  val invalidRegimeType = "9"
  val vatRegime: TaxRegime = VatRegime(id = "123456789")
  val invalidRegime: TaxRegime = VatRegime(id = "9")
  val vrn = "123456789"
  val invalidVrn = "9"
  val queryParameters: FinancialRequestQueryParameters = FinancialRequestQueryParameters()

  def generateUrl(regimeType: String, VRN : String): String =  s"/penalty/financial-data/VRN/$VRN/$regimeType" +
    s"?onlyOpenItems=false&includeStatistical=true&includeLocks=true&calculateAccruedInterest=true&removePOA=true&customerPaymentInformation=true"

  "getFinancialData" should {

    "return a FinancialTransactionsModel" when {

      s"a $OK response is received from financial transactions and the response can be parsed" in {

        stubGetRequest(
          generateUrl(regimeType, vrn),
          OK,
          fullFinancialTransactionsJsonEIS.toString()
        )

        val expectedResult = fullFinancialTransactions

        val result: FinancialTransactionsResponse =
          await(connector.getFinancialData(vatRegime,queryParameters))
        result shouldBe Right(expectedResult)

      }

      "return an ErrorModel" when {

        s"a $OK response is received from financialTransactions, but the response cannot be parsed" in {

          stubGetRequest(
            generateUrl(regimeType, vrn),
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
            generateUrl(regimeType, invalidVrn),
            BAD_REQUEST,
            Json.obj("code" -> 400,
              "reason" -> "BAD REQUEST").toString()
          )

            val expectedResult = Left(Error(BAD_REQUEST,
              """{"code":400,"reason":"BAD REQUEST"}"""))

          val result: FinancialTransactionsResponse =
            await(connector.getFinancialData(invalidRegime, queryParameters))
          result shouldBe expectedResult
        }
        s"a $NOT_FOUND response is received from Financial Transactions" in {

          stubGetRequest(
            generateUrl(regimeType, vrn),
            NOT_FOUND,
            Json.obj("code" -> 404,
              "reason" -> "A not found error has been received from financial transactions").toString()
          )

          val expectedResult = Left(Error(NOT_FOUND,
            """{"code":404,"reason":"A not found error has been received from financial transactions"}"""))

          val result: FinancialTransactionsResponse =
            await(connector.getFinancialData(vatRegime, queryParameters))
          result shouldBe expectedResult
        }
        "an unexpected response is received from financial transactions" in {

          stubGetRequest(
            generateUrl(regimeType, vrn),
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
