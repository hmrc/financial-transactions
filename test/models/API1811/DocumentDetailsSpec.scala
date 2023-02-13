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

package models.API1811

import base.SpecBase
import play.api.libs.json._
import utils.API1811.TestConstants._

class DocumentDetailsSpec extends SpecBase {

  "getAccruingPenalty" should {

    "return only the penalty charge that has an accruing penaltyStatus" in {

      val model = fullDocumentDetails.copy(
        documentPenaltyTotals = Some(Seq(documentPenaltyTotalsPosted, documentPenaltyTotals)))

      model.getAccruingPenalty shouldBe Some(documentPenaltyTotals)
    }

    "return an empty DocumentPenaltyTotals when PenaltyStatus is not accruing" in {

      val model = fullDocumentDetails.copy(
        documentPenaltyTotals = Some(Seq(documentPenaltyTotalsPosted)))

      model.getAccruingPenalty shouldBe None
    }

    "return a none when DocumentPenaltyTotals is empty" in {

      val model = fullDocumentDetails.copy(
        documentPenaltyTotals = Some(Seq(emptyDocumentPenaltyTotal)))

      model.getAccruingPenalty shouldBe None
    }
  }

  "DocumentDetails" should {

    "read from JSON" when {

      "maximum fields are present" in {
        fullDocumentDetailsJson.as[DocumentDetails] shouldBe fullDocumentDetails
      }

      "minimum fields are present" in {
        Json.obj("lineItemDetails" -> Json.arr("")).as[DocumentDetails] shouldBe emptyDocumentDetails
      }

      "some correct fields are present but some are unrecognised" in {
        documentDetailsIncorrectFieldsJson.as[DocumentDetails] shouldBe fullDocumentDetailsIncorrectFields
      }
    }

    "write to JSON" when {

      "maximum fields are present" in {
        Json.toJson(fullDocumentDetails) shouldBe fullDocumentDetailsOutputJson
      }

      "minimum fields are present" in {
        val expectedOutput: JsObject = Json.obj("items" -> Json.arr(Json.obj()))
        Json.toJson(emptyDocumentDetails) shouldBe expectedOutput
      }

      "there is an accruing and a posted penalty charge" in {

        val model = fullDocumentDetails.copy(
          documentPenaltyTotals = Some(Seq(documentPenaltyTotalsPosted, documentPenaltyTotals)))
        val expectedOutput: JsObject = Json.obj(
          "chargeType" -> "VAT Return Debit Charge",
          "periodKey" -> "13RL",
          "taxPeriodFrom" -> "2017-04-06",
          "taxPeriodTo" -> "2018-04-05",
          "chargeReference" -> "XM002610011594",
          "mainTransaction" -> "4700",
          "subTransaction" -> "1174",
          "originalAmount" -> 45552768.79,
          "outstandingAmount" -> 297873.46,
          "clearedAmount" -> 45254895.33,
          "items" -> Json.arr(fullLineItemDetailsOutputJson),
          "accruingInterestAmount" -> 0.23,
          "interestRate" -> 3,
          "accruingPenaltyAmount" -> 10.01,
          "penaltyType" -> "LPP1"
        )
        Json.toJson(model) shouldBe expectedOutput
      }

      "the penaltyStatus is not accruing" in {

        val model = fullDocumentDetails.copy(
          documentPenaltyTotals = Some(Seq(documentPenaltyTotalsPosted)))
        val expectedOutput: JsObject = Json.obj(
          "chargeType" -> "VAT Return Debit Charge",
          "periodKey" -> "13RL",
          "taxPeriodFrom" -> "2017-04-06",
          "taxPeriodTo" -> "2018-04-05",
          "chargeReference" -> "XM002610011594",
          "mainTransaction" -> "4700",
          "subTransaction" -> "1174",
          "originalAmount" -> 45552768.79,
          "outstandingAmount" -> 297873.46,
          "clearedAmount" -> 45254895.33,
          "items" -> Json.arr(fullLineItemDetailsOutputJson),
          "accruingInterestAmount" -> 0.23,
          "interestRate" -> 3
        )
        Json.toJson(model) shouldBe expectedOutput
      }

      "the documentPenaltyTotals array is present but empty" in {

        val model = fullDocumentDetails.copy(
          documentPenaltyTotals = Some(Seq(emptyDocumentPenaltyTotal)))
        val expectedOutput: JsObject = Json.obj(
          "chargeType" -> "VAT Return Debit Charge",
          "periodKey" -> "13RL",
          "taxPeriodFrom" -> "2017-04-06",
          "taxPeriodTo" -> "2018-04-05",
          "chargeReference" -> "XM002610011594",
          "mainTransaction" -> "4700",
          "subTransaction" -> "1174",
          "originalAmount" -> 45552768.79,
          "outstandingAmount" -> 297873.46,
          "clearedAmount" -> 45254895.33,
          "items" -> Json.arr(fullLineItemDetailsOutputJson),
          "accruingInterestAmount" -> 0.23,
          "interestRate" -> 3
        )
        Json.toJson(model) shouldBe expectedOutput
      }

      "the documentPenaltyTotals field is None" in {

        val model = fullDocumentDetails.copy(documentPenaltyTotals = None)
        val expectedOutput: JsObject = Json.obj(
          "chargeType" -> "VAT Return Debit Charge",
          "periodKey" -> "13RL",
          "taxPeriodFrom" -> "2017-04-06",
          "taxPeriodTo" -> "2018-04-05",
          "chargeReference" -> "XM002610011594",
          "mainTransaction" -> "4700",
          "subTransaction" -> "1174",
          "originalAmount" -> 45552768.79,
          "outstandingAmount" -> 297873.46,
          "clearedAmount" -> 45254895.33,
          "items" -> Json.arr(fullLineItemDetailsOutputJson),
          "accruingInterestAmount" -> 0.23,
          "interestRate" -> 3
        )
        Json.toJson(model) shouldBe expectedOutput
      }

      "there are multiple line item details objects" in {
        val model = fullDocumentDetails.copy(lineItemDetails = Seq(lineItemDetailsFull, lineItemDetailsFull))
        val expectedOutput: JsObject = Json.obj(
          "chargeType" -> "VAT Return Debit Charge",
          "periodKey" -> "13RL",
          "taxPeriodFrom" -> "2017-04-06",
          "taxPeriodTo" -> "2018-04-05",
          "chargeReference" -> "XM002610011594",
          "mainTransaction" -> "4700",
          "subTransaction" -> "1174",
          "originalAmount" -> 45552768.79,
          "outstandingAmount" -> 297873.46,
          "clearedAmount" -> 45254895.33,
          "items" -> Json.arr(fullLineItemDetailsOutputJson, fullLineItemDetailsOutputJson),
          "accruingInterestAmount" -> 0.23,
          "interestRate" -> 3,
          "accruingPenaltyAmount" -> 10.01,
          "penaltyType" -> "LPP1"
        )
        Json.toJson(model) shouldBe expectedOutput
      }

      "main transaction is not present" in {
        val model = fullDocumentDetails.copy(lineItemDetails = Seq(lineItemDetailsFull.copy(mainTransaction = None)))
        val expectedOutput = Json.obj(
          "periodKey" -> "13RL",
          "taxPeriodFrom" -> "2017-04-06",
          "taxPeriodTo" -> "2018-04-05",
          "chargeReference" -> "XM002610011594",
          "subTransaction" -> "1174",
          "originalAmount" -> 45552768.79,
          "outstandingAmount" -> 297873.46,
          "clearedAmount" -> 45254895.33,
          "items" -> Json.arr(fullLineItemDetailsOutputJson),
          "accruingInterestAmount" -> 0.23,
          "interestRate" -> 3,
          "accruingPenaltyAmount" -> 10.01,
          "penaltyType" -> "LPP1"
        )
        Json.toJson(model) shouldBe expectedOutput
      }

      "sub transaction is not present" in {
        val model = fullDocumentDetails.copy(lineItemDetails = Seq(lineItemDetailsFull.copy(subTransaction = None)))
        val expectedOutput = Json.obj(
          "periodKey" -> "13RL",
          "taxPeriodFrom" -> "2017-04-06",
          "taxPeriodTo" -> "2018-04-05",
          "chargeReference" -> "XM002610011594",
          "mainTransaction" -> "4700",
          "originalAmount" -> 45552768.79,
          "outstandingAmount" -> 297873.46,
          "clearedAmount" -> 45254895.33,
          "items" -> Json.arr(fullLineItemDetailsOutputJson),
          "accruingInterestAmount" -> 0.23,
          "interestRate" -> 3,
          "accruingPenaltyAmount" -> 10.01,
          "penaltyType" -> "LPP1"
        )
        Json.toJson(model) shouldBe expectedOutput
      }

      "main transaction and sub transaction are not present" in {
        val model = fullDocumentDetails.copy(
          lineItemDetails = Seq(lineItemDetailsFull.copy(mainTransaction = None, subTransaction = None)))
        val expectedOutput = Json.obj(
          "periodKey" -> "13RL",
          "taxPeriodFrom" -> "2017-04-06",
          "taxPeriodTo" -> "2018-04-05",
          "chargeReference" -> "XM002610011594",
          "originalAmount" -> 45552768.79,
          "outstandingAmount" -> 297873.46,
          "clearedAmount" -> 45254895.33,
          "items" -> Json.arr(fullLineItemDetailsOutputJson),
          "accruingInterestAmount" -> 0.23,
          "interestRate" -> 3,
          "accruingPenaltyAmount" -> 10.01,
          "penaltyType" -> "LPP1"
        )
        Json.toJson(model) shouldBe expectedOutput
      }
    }

    "throw an exception" when {

      "there are no line item details present" in {
        val model = fullDocumentDetails.copy(lineItemDetails = Seq())
        val ex = intercept[JsResultException](Json.toJson(model))
        ex.errors.head._1 shouldBe JsPath \ "lineItemDetails"
        ex.errors.head._2 shouldBe List(JsonValidationError(List("Line item details must contain at least 1 item")))
      }
    }
  }
}
