/*
 * Copyright 2018 HM Revenue & Customs
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


import base.SpecBase
import connectors.httpParsers.FinancialTransactionsHttpParser
import mocks.MockHttp
import models._
import play.api.http.Status
import utils.ImplicitDateFormatter._
import models.FinancialDataQueryParameters._

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
  val itRegime = IncomeTaxRegime(id = "AB123456B")

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

    "format the request Url correctly for Income Tax TaxRegime requests" in {

      val actualUrl: String = TestFinancialDataConnector.financialDataUrl(itRegime)
      val expectedUrl: String = s"${mockAppConfig.desUrl}/enterprise/financial-data/${itRegime.idType}/${itRegime.id}/${itRegime.regimeType}"

      actualUrl shouldBe expectedUrl
    }

    "when calling the getFinancialData" when {

      val successResponse: Either[Nothing, FinancialTransactions] = Right(FinancialTransactions(
        idType = Some("MTDBSA"),
        idNumber = Some("XQIT00000000001"),
        regimeType = Some("ITSA"),
        processingDate = "2017-03-07T22:55:56.987Z",
        financialTransactions = Some(Seq(Transaction(
          chargeType = Some("PAYE"),
          mainType = Some("2100"),
          periodKey = Some("13RL"),
          periodKeyDescription = Some("abcde"),
          taxPeriodFrom = Some("2017-4-6"),
          taxPeriodTo = Some("2018-4-5"),
          businessPartner = Some("6622334455"),
          contractAccountCategory = Some("02"),
          contractAccount = Some("X"),
          contractObjectType = Some("ABCD"),
          contractObject = Some("00000003000000002757"),
          sapDocumentNumber = Some("1040000872"),
          sapDocumentNumberItem = Some("XM00"),
          chargeReference = Some("XM002610011594"),
          mainTransaction = Some("1234"),
          subTransaction = Some("5678"),
          originalAmount = Some(3400.0),
          outstandingAmount = Some(1400.0),
          clearedAmount = Some(2000.0),
          accruedInterest = Some(0.23),
          items = Some(Seq(SubItem(
            subItem = Some("000"),
            dueDate = Some("2018-2-14"),
            amount = Some(3400.00),
            clearingDate = Some("2018-2-17"),
            clearingReason = Some("A"),
            outgoingPaymentMethod = Some("B"),
            paymentLock = Some("C"),
            clearingLock = Some("D"),
            interestLock = Some("E"),
            dunningLock = Some("1"),
            returnFlag = Some(false),
            paymentReference = Some("F"),
            paymentAmount = Some(2000.00),
            paymentMethod = Some("G"),
            paymentLot = Some("H"),
            paymentLotItem = Some("112"),
            clearingSAPDocument = Some("3350000253"),
            statisticalDocument = Some("I"),
            returnReason = Some("J"),
            promiseToPay = Some("K")
          )))
        )))
      ))

      "calling for a VAT user with all Query Parameters defined and a success response received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq(
            dateFromKey -> "2017-04-06",
            dateToKey -> "2018-04-05",
            onlyOpenItemsKey -> "false",
            includeLocksKey -> "true",
            calculateAccruedInterestKey -> "false",
            customerPaymentInformationKey -> "false"
          ))(successResponse)
          val result: Future[FinancialTransactionsHttpParser.HttpGetResult[FinancialTransactions]] =
            TestFinancialDataConnector.getFinancialData(
            regime = vatRegime,
            queryParameters = FinancialDataQueryParameters(
              fromDate = Some("2017-04-06"),
              toDate = Some("2018-04-05"),
              onlyOpenItems = Some(false),
              includeLocks = Some(true),
              calculateAccruedInterest = Some(false),
              customerPaymentInformation = Some(false)
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
            queryParameters = FinancialDataQueryParameters(
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
            queryParameters = FinancialDataQueryParameters(
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
            queryParameters = FinancialDataQueryParameters(
              onlyOpenItems = Some(true)
            )
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a VAT user with includeLocks Query Parameter and a success response received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime),
            Seq(includeLocksKey -> "true"))(successResponse)
          val result = TestFinancialDataConnector.getFinancialData(
            regime = vatRegime,
            queryParameters = FinancialDataQueryParameters(
              includeLocks = Some(true)
            )
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a VAT user with calculateAccruedInterest Query Parameter and a success response received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime),
            Seq(calculateAccruedInterestKey -> "false"))(successResponse)
          val result: Future[FinancialTransactionsHttpParser.HttpGetResult[FinancialTransactions]] =
            TestFinancialDataConnector.getFinancialData(
            regime = vatRegime,
            queryParameters = FinancialDataQueryParameters(
              calculateAccruedInterest = Some(false)
            )
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a VAT user with customerPaymentInformation Query Parameter and a success response received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime),
            Seq(customerPaymentInformationKey -> "false"))(successResponse)
          val result: Future[FinancialTransactionsHttpParser.HttpGetResult[FinancialTransactions]] =
            TestFinancialDataConnector.getFinancialData(
            regime = vatRegime,
            queryParameters = FinancialDataQueryParameters(
              customerPaymentInformation = Some(false)
            )
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a VAT user with no Query Parameters defined and a success response received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq())(successResponse)
          val result: Future[FinancialTransactionsHttpParser.HttpGetResult[FinancialTransactions]] =
            TestFinancialDataConnector.getFinancialData(regime = vatRegime, FinancialDataQueryParameters())
          await(result) shouldBe successResponse
        }
      }

      "calling for a VAT user and a non-success response received, single error" should {

        "return a Error model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq())(badRequestSingleError)
          val result: Future[FinancialTransactionsHttpParser.HttpGetResult[FinancialTransactions]] =
            TestFinancialDataConnector.getFinancialData(regime = vatRegime, FinancialDataQueryParameters())
          await(result) shouldBe badRequestSingleError
        }
      }

      "calling for a VAT user and a non-success response received, multi error" should {

        "return a MultiError model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq())(badRequestMultiError)
          val result: Future[FinancialTransactionsHttpParser.HttpGetResult[FinancialTransactions]] =
            TestFinancialDataConnector.getFinancialData(regime = vatRegime, FinancialDataQueryParameters())
          await(result) shouldBe badRequestMultiError
        }
      }

    }

    "when calling the checkDirectDebitExists" when {

      val successResponse: Either[Nothing, Boolean] = Right(true)
      val vrn: String = "5555555555"
      setupMockHttpGet(TestFinancialDataConnector.directDebitUrl(vrn = vrn))(successResponse)

      "calling check direct debit exists with success response received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.directDebitUrl(vrn = vrn))(successResponse)
          val result: Future[FinancialTransactionsHttpParser.HttpGetResult[Boolean]] =
            TestFinancialDataConnector.checkDirectDebitExists(vrn = vrn)
          await(result) shouldBe successResponse
        }
      }

      "calling the checkDirectDebitExist, single error" should {

        "return a Error model" in {
          setupMockHttpGet(TestFinancialDataConnector.directDebitUrl(vrn = vrn))(badRequestSingleError)
          val result: Future[FinancialTransactionsHttpParser.HttpGetResult[Boolean]] =
            TestFinancialDataConnector.checkDirectDebitExists(vrn = vrn)
          await(result) shouldBe badRequestSingleError
        }
      }

      "calling the checkDirectDebitExist, multi error" should {

        "return a MultiError model" in {
          setupMockHttpGet(TestFinancialDataConnector.directDebitUrl(vrn = vrn))(badRequestMultiError)
          val result: Future[FinancialTransactionsHttpParser.HttpGetResult[Boolean]] =
            TestFinancialDataConnector.checkDirectDebitExists(vrn = vrn)
          await(result) shouldBe badRequestMultiError
        }
      }

    }

  }
}
