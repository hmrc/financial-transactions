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
import mocks.MockHttp
import models._
import utils.ImplicitDateFormatter._

class FinancialDataConnectorSpec extends SpecBase with MockHttp {

  val successResponse = Right(FinancialTransactions(
    idType = "MTDBSA",
    idNumber = "XQIT00000000001",
    regimeType = "ITSA",
    processingDate = "2017-03-07T22:55:56.987Z",
    financialTransactions = Seq(Transaction(
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
      items = Seq(SubItem(
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
      ))
    ))
  ))
  val badRequestSingleError = Left(DesError(code = "CODE", reason = "ERROR MESSAGE"))
  val badRequestMultiError = Left(DesMultiError(
    failures = Seq(
      DesError(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
      DesError(code = "ERROR CODE 2", reason = "ERROR MESSAGE 2")
    )
  ))

  val vatRegime = VatRegime(id = "12345678")
  val itRegime = IncomeTaxRegime(id = "AB123456B")

  object TestFinancialDataConnector extends FinancialDataConnector(mockHttpGet, mockAppConfig)

  "The FinancialDataConnector" should {


    "format the request Url correctly for VAT TaxRegime requests" in {

      val actualUrl = TestFinancialDataConnector.financialDataUrl(vatRegime)
      val expectedUrl = s"${mockAppConfig.desUrl}/enterprise/financial-data/${vatRegime.idType}/${vatRegime.id}/${vatRegime.regimeType}"

      actualUrl shouldBe expectedUrl
    }

    "format the request Url correctly for Income Tax TaxRegime requests" in {

      val actualUrl = TestFinancialDataConnector.financialDataUrl(itRegime)
      val expectedUrl = s"${mockAppConfig.desUrl}/enterprise/financial-data/${itRegime.idType}/${itRegime.id}/${itRegime.regimeType}"

      actualUrl shouldBe expectedUrl
    }

    "when calling the getFinancialTransactions" when {

      "calling for a VAT user with all Query Parameters defined and a success response received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq(
            "dateFrom" -> "2017-04-06",
            "dateTo" -> "2018-04-05",
            "onlyOpenItems" -> "false",
            "includeLocks" -> "true",
            "calculateAccruedInterest" -> "false",
            "customerPaymentInformation" -> "false"
          ))(successResponse)
          val result = TestFinancialDataConnector.getFinancialTransactions(
            regime = vatRegime,
            fromDate = Some("2017-04-06"),
            toDate = Some("2018-04-05"),
            onlyOpenItems = Some(false),
            includeLocks = Some(true),
            calculateAccruedInterest = Some(false),
            customerPaymentInformation = Some(false)
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a VAT user with dateFrom Query Parameter and a success response is received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq(
            "dateFrom" -> "2017-04-06"
          ))(successResponse)
          val result = TestFinancialDataConnector.getFinancialTransactions(
            regime = vatRegime,
            fromDate = Some("2017-04-06")
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a VAT user with dateTo Query Parameter and a success response received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq("dateTo" -> "2018-04-05"))(successResponse)
          val result = TestFinancialDataConnector.getFinancialTransactions(
            regime = vatRegime,
            toDate = Some("2018-04-05")
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a VAT user with onlyOpenItems Query Parameter and a success response received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq("onlyOpenItems" -> "true"))(successResponse)
          val result = TestFinancialDataConnector.getFinancialTransactions(
            regime = vatRegime,
            onlyOpenItems = Some(true)
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a VAT user with includeLocks Query Parameter and a success response received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq("includeLocks" -> "true"))(successResponse)
          val result = TestFinancialDataConnector.getFinancialTransactions(
            regime = vatRegime,
            includeLocks = Some(true)
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a VAT user with calculateAccruedInterest Query Parameter and a success response received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq("calculateAccruedInterest" -> "false"))(successResponse)
          val result = TestFinancialDataConnector.getFinancialTransactions(
            regime = vatRegime,
            calculateAccruedInterest = Some(false)
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a VAT user with customerPaymentInformation Query Parameter and a success response received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq("customerPaymentInformation" -> "false"))(successResponse)
          val result = TestFinancialDataConnector.getFinancialTransactions(
            regime = vatRegime,
            customerPaymentInformation = Some(false)
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a VAT user with no Query Parameters defined and a success response received" should {

        "return a FinancialTransactions model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq())(successResponse)
          val result = TestFinancialDataConnector.getFinancialTransactions(regime = vatRegime)
          await(result) shouldBe successResponse
        }
      }

      "calling for a VAT user and a non-success response received, single error" should {

        "return a DesError model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq())(badRequestSingleError)
          val result = TestFinancialDataConnector.getFinancialTransactions(regime = vatRegime)
          await(result) shouldBe badRequestSingleError
        }
      }

      "calling for a VAT user and a non-success response received, multi error" should {

        "return a DesMultiError model" in {
          setupMockHttpGet(TestFinancialDataConnector.financialDataUrl(vatRegime), Seq())(badRequestMultiError)
          val result = TestFinancialDataConnector.getFinancialTransactions(regime = vatRegime)
          await(result) shouldBe badRequestMultiError
        }
      }

    }
  }
}
