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

package connectors.API1166

import base.SpecBase
import connectors.API1166.httpParsers.FinancialTransactionsHttpParser
import mocks.MockHttp
import models.API1166._
import models._
import models.FinancialRequestQueryParameters._
import play.api.http.Status
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.ImplicitDateFormatter._
import utils.TestConstants.fullFinancialTransactions

import scala.concurrent.Future

class FinancialDataConnectorSpec extends SpecBase with MockHttp {

  val badRequestSingleError: Either[ErrorResponse, Nothing] =
    Left(ErrorResponse(Status.BAD_REQUEST, Error(code = "CODE", reason = "ERROR MESSAGE")))

  val badRequestMultiError = Left(ErrorResponse(Status.BAD_REQUEST, MultiError(
    failures = Seq(
      Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
      Error(code = "ERROR CODE 2", reason = "ERROR MESSAGE 2")
    )
  )))

  val vatRegime: TaxRegime = VatRegime(id = "12345678")

  object TestFinancialDataConnector extends FinancialDataConnector(mockHttpGet, mockAppConfig)

  "The FinancialDataConnector" should {

    "format the request Url correctly for check direct debit requests" in {

      val vrn:String = "555555555"
      val actualUrl: String = TestFinancialDataConnector.directDebitUrl(vrn = vrn)
      val expectedUrl: String = s"${mockAppConfig.desUrl}/cross-regime/direct-debits/vatc/vrn/$vrn"

      actualUrl shouldBe expectedUrl
    }

    "format the request Url correctly for VAT TaxRegime requests" in {

      val actualUrl: String = TestFinancialDataConnector.financialDataUrl(vatRegime)
      val expectedUrl: String = s"${mockAppConfig.desUrl}/enterprise/financial-data/${vatRegime.idType}/${vatRegime.id}/${vatRegime.regimeType}"

      actualUrl shouldBe expectedUrl
    }

    "when calling the getFinancialData" when {

      val successResponse: Either[Nothing, FinancialTransactions] = Right(fullFinancialTransactions)

      "calling for a VAT user with all Query Parameters defined and a success response received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq(
            dateFromKey -> "2017-04-06",
            dateToKey -> "2018-04-05",
            onlyOpenItemsKey -> "false"
          ))(successResponse)
          val result: Future[FinancialTransactionsHttpParser.HttpGetResult[FinancialTransactions]] =
            TestFinancialDataConnector.getFinancialData(
              regime = vatRegime,
              queryParameters = FinancialRequestQueryParameters(
                fromDate = Some("2017-04-06"),
                toDate = Some("2018-04-05"),
                onlyOpenItems = Some(false)
              )
            )
          await(result) shouldBe successResponse
        }
      }

      "calling for a VAT user with dateFrom Query Parameter and a success response is received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq(
            dateFromKey -> "2017-04-06"
          ))(successResponse)
          val result: Future[FinancialTransactionsHttpParser.HttpGetResult[FinancialTransactions]] =
            TestFinancialDataConnector.getFinancialData(
              regime = vatRegime,
              queryParameters = FinancialRequestQueryParameters(
                fromDate = Some("2017-04-06")
              )
            )
          await(result) shouldBe successResponse
        }
      }

      "calling for a VAT user with dateTo Query Parameter and a success response received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime),
            Seq(dateToKey -> "2018-04-05"))(successResponse)
          val result: Future[FinancialTransactionsHttpParser.HttpGetResult[FinancialTransactions]] =
            TestFinancialDataConnector.getFinancialData(
              regime = vatRegime,
              queryParameters = FinancialRequestQueryParameters(
                toDate = Some("2018-04-05")
              )
            )
          await(result) shouldBe successResponse
        }
      }

      "calling for a VAT user with onlyOpenItems Query Parameter and a success response received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime),
            Seq(onlyOpenItemsKey -> "true"))(successResponse)
          val result = TestFinancialDataConnector.getFinancialData(
            regime = vatRegime,
            queryParameters = FinancialRequestQueryParameters(
              onlyOpenItems = Some(true)
            )
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a VAT user with no Query Parameters defined and a success response received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq())(successResponse)
          val result: Future[FinancialTransactionsHttpParser.HttpGetResult[FinancialTransactions]] =
            TestFinancialDataConnector.getFinancialData(regime = vatRegime, FinancialRequestQueryParameters())
          await(result) shouldBe successResponse
        }
      }

      "calling for a VAT user and a non-success response received, single error" should {

        "return a Error model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq())(badRequestSingleError)
          val result: Future[FinancialTransactionsHttpParser.HttpGetResult[FinancialTransactions]] =
            TestFinancialDataConnector.getFinancialData(regime = vatRegime, FinancialRequestQueryParameters())
          await(result) shouldBe badRequestSingleError
        }
      }

      "calling for a VAT user and a non-success response received, multi error" should {

        "return a MultiError model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq())(badRequestMultiError)
          val result: Future[FinancialTransactionsHttpParser.HttpGetResult[FinancialTransactions]] =
            TestFinancialDataConnector.getFinancialData(regime = vatRegime, FinancialRequestQueryParameters())
          await(result) shouldBe badRequestMultiError
        }
      }
    }

    "when calling the checkDirectDebitExists" when {

      val successResponse: Either[Nothing, Boolean] = Right(true)
      val vrn: String = "5555555555"
      setupMockHttpGet(TestFinancialDataConnector.directDebitUrl(vrn = vrn))(successResponse)

      "calling check direct debit exists with success response received" should {

        "return a that a direct debit was found" in {
          setupMockHttpGet(TestFinancialDataConnector.directDebitUrl(vrn = vrn))(successResponse)
          val result: Future[FinancialTransactionsHttpParser.HttpGetResult[DirectDebits]] =
            TestFinancialDataConnector.checkDirectDebitExists(vrn = vrn)
          await(result) shouldBe successResponse
        }
      }

      "calling the checkDirectDebitExist, single error" should {

        "return a Error model" in {
          setupMockHttpGet(TestFinancialDataConnector.directDebitUrl(vrn = vrn))(badRequestSingleError)
          val result: Future[FinancialTransactionsHttpParser.HttpGetResult[DirectDebits]] =
            TestFinancialDataConnector.checkDirectDebitExists(vrn = vrn)
          await(result) shouldBe badRequestSingleError
        }
      }

      "calling the checkDirectDebitExist, multi error" should {

        "return a MultiError model" in {
          setupMockHttpGet(TestFinancialDataConnector.directDebitUrl(vrn = vrn))(badRequestMultiError)
          val result: Future[FinancialTransactionsHttpParser.HttpGetResult[DirectDebits]] =
            TestFinancialDataConnector.checkDirectDebitExists(vrn = vrn)
          await(result) shouldBe badRequestMultiError
        }
      }
    }
  }
}