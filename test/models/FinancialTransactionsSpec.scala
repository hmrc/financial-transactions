/*
 * Copyright 2017 HM Revenue & Customs
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

package models

import base.SpecBase
import play.api.libs.json.{JsValue, Json}
import utils.ImplicitDateFormatter._

class FinancialTransactionsSpec extends SpecBase {

  val financialTransactionsModel = FinancialTransactions(
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
  )

  val financialTransactionsJson: JsValue =
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

  "FinancialTransactions" should {

    "serialize to Json successfully" in {
      Json.toJson(financialTransactionsModel) shouldBe financialTransactionsJson
    }

    "deserialize to a Transaction model successfully" in {
      financialTransactionsJson.as[FinancialTransactions] shouldBe financialTransactionsModel
    }

  }

}
