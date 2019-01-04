/*
 * Copyright 2019 HM Revenue & Customs
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

package connectors.httpParsers

import base.SpecBase
import connectors.httpParsers.FinancialTransactionsHttpParser.FinancialTransactionsReads
import models._
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import utils.ImplicitDateFormatter._

class FinancialTransactionsHttpParserSpec extends SpecBase {

  "The FinancialTransactionsHttpParser" when {

    "the http response status is 200 OK and matches expected Schema" should {

      val httpResponse = HttpResponse(Status.OK, responseJson = Some(
        Json.obj(
          "idType" -> "MTDBSA",
          "idNumber" -> "XQIT00000000001",
          "regimeType" -> "ITSA",
          "processingDate" -> "2017-03-07T22:55:56.987Z",
          "financialTransactions" -> Json.arr(
            Json.obj(
              "chargeType" -> "PAYE",
              "mainType" -> "2100",
              "periodKey" -> "13RL",
              "periodKeyDescription" -> "abcde",
              "taxPeriodFrom" -> "2017-04-06",
              "taxPeriodTo" -> "2018-04-05",
              "businessPartner" -> "6622334455",
              "contractAccountCategory" -> "02",
              "contractAccount" -> "X",
              "contractObjectType" -> "ABCD",
              "contractObject" -> "00000003000000002757",
              "sapDocumentNumber" -> "1040000872",
              "sapDocumentNumberItem" -> "XM00",
              "chargeReference" -> "XM002610011594",
              "mainTransaction" -> "1234",
              "subTransaction" -> "5678",
              "originalAmount" -> 3400,
              "outstandingAmount" -> 1400,
              "clearedAmount" -> 2000,
              "accruedInterest" -> 0.23,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2018-02-14",
                  "amount" -> 3400,
                  "clearingDate" -> "2018-02-17",
                  "clearingReason" -> "A",
                  "outgoingPaymentMethod" -> "B",
                  "paymentLock" -> "C",
                  "clearingLock" -> "D",
                  "interestLock" -> "E",
                  "dunningLock" -> "1",
                  "returnFlag" -> false,
                  "paymentReference" -> "F",
                  "paymentAmount" -> 2000,
                  "paymentMethod" -> "G",
                  "paymentLot" -> "H",
                  "paymentLotItem" -> "112",
                  "clearingSAPDocument" -> "3350000253",
                  "statisticalDocument" -> "I",
                  "returnReason" -> "J",
                  "promiseToPay" -> "K"
                )
              )
            )
          )
        )
      ))

      val expected = Right(FinancialTransactions(
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

      val result = FinancialTransactionsReads.read("", "", httpResponse)

      "return a FinancialTransactions instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 200 OK but the response is not as expected" should {

      val httpResponse = HttpResponse(Status.OK, responseJson = Some(Json.obj("foo" -> "bar")))

      val expected = Left(UnexpectedJsonFormat)

      val result = FinancialTransactionsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (single error)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "code" -> "CODE",
          "reason" -> "ERROR MESSAGE"
        ))
      )

      val expected = Left(ErrorResponse(
        Status.BAD_REQUEST,
        Error(
          code = "CODE",
          reason = "ERROR MESSAGE"
        )
      ))

      val result = FinancialTransactionsReads.read("", "", httpResponse)

      "return a Error instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (multiple errors)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "failures" -> Json.arr(
            Json.obj(
              "code" -> "ERROR CODE 1",
              "reason" -> "ERROR MESSAGE 1"
            ),
            Json.obj(
              "code" -> "ERROR CODE 2",
              "reason" -> "ERROR MESSAGE 2"
            )
          )
        ))
      )

      val expected = Left(ErrorResponse(
        Status.BAD_REQUEST,
        MultiError(
          failures = Seq(
            Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
            Error(code = "ERROR CODE 2", reason = "ERROR MESSAGE 2")
          )
        )
      ))

      val result = FinancialTransactionsReads.read("", "", httpResponse)

      "return a MultiError" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (Unexpected Json Returned)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST, responseJson = Some(Json.obj("foo" -> "bar")))

      val expected = Left(UnexpectedJsonFormat)

      val result = FinancialTransactionsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (Bad Json Returned)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST, responseString = Some("Banana"))

      val expected = Left(InvalidJsonResponse)

      val result = FinancialTransactionsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 500 ISE" should {

      val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR,
        responseJson = Some(Json.obj(
          "code" -> "code",
          "reason" -> "message"
        ))
      )

      val expected = Left(ErrorResponse(
        Status.INTERNAL_SERVER_ERROR,
        Error(
          code = "code",
          reason = "message"
        )
      ))

      val result = FinancialTransactionsReads.read("", "", httpResponse)

      "return an ISE" in {
        result shouldEqual expected
      }

    }

    "the http response status is unexpected" should {

      val httpResponse = HttpResponse(Status.SEE_OTHER)

      val expected = Left(UnexpectedResponse)

      val result = FinancialTransactionsReads.read("", "", httpResponse)

      "return an ISE" in {
        result shouldEqual expected
      }

    }

  }

}
