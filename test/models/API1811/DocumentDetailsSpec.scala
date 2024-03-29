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
import play.api.test.FakeRequest
import utils.API1811.TestConstants._

import java.time.LocalDate

class DocumentDetailsSpec extends SpecBase {

  val dateInFamiliarisationPeriod: LocalDate = mockAppConfig.familiarisationPeriodEndDate.minusDays(1)
  val dateAfterFamiliarisationPeriod: LocalDate = mockAppConfig.familiarisationPeriodEndDate.plusDays(1)
  val estimatedLPP1: DocumentPenaltyTotals = documentPenaltyTotals
  val estimatedLPP1Document: DocumentDetails = fullDocumentDetails.copy(
    documentPenaltyTotals = Some(Seq(estimatedLPP1)),
    lineItemDetails = Seq(lineItemDetailsFull.copy(netDueDate = Some(dateAfterFamiliarisationPeriod)))
  )
  val estimatedLPP2: DocumentPenaltyTotals = estimatedLPP1.copy(penaltyType = Some("LPP2"))
  val estimatedLPP2Document: DocumentDetails = estimatedLPP1Document.copy(documentPenaltyTotals = Some(Seq(estimatedLPP2)))

  implicit val request: FakeRequest[_] = fakeRequest

  "getAccruingPenalty" should {

    "return estimated LPP1 details" when {

      "the parent charge due date is after the familiarisation period end date" in {
        estimatedLPP1Document.getAccruingPenalty shouldBe Some(estimatedLPP1)
      }

      "the parent charge due date is on the familiarisation period end date" in {
        val model = estimatedLPP1Document.copy(
          lineItemDetails = Seq(lineItemDetailsFull.copy(netDueDate = Some(mockAppConfig.familiarisationPeriodEndDate)))
        )
        model.getAccruingPenalty shouldBe Some(estimatedLPP1)
      }

      "there is also a posted penalty in the document penalty totals" in {
        val model = estimatedLPP1Document.copy(
          documentPenaltyTotals = Some(Seq(documentPenaltyTotalsPosted, estimatedLPP1))
        )
        model.getAccruingPenalty shouldBe Some(estimatedLPP1)
      }
    }

    "return estimated LPP2 details" when {

      "the parent charge due date is after the familiarisation period end date" in {
        estimatedLPP2Document.getAccruingPenalty shouldBe Some(estimatedLPP2)
      }

      "the parent charge due date is before the familiarisation period end date" in {
        val model = estimatedLPP2Document.copy(
          lineItemDetails = Seq(lineItemDetailsFull.copy(netDueDate = Some(dateInFamiliarisationPeriod)))
        )
        model.getAccruingPenalty shouldBe Some(estimatedLPP2)
      }

      "the parent charge due date is on the familiarisation period end date" in {
        val model = estimatedLPP2Document.copy(
          lineItemDetails = Seq(lineItemDetailsFull.copy(netDueDate = Some(mockAppConfig.familiarisationPeriodEndDate)))
        )
        model.getAccruingPenalty shouldBe Some(estimatedLPP2)
      }

      "there is also a posted penalty in the document penalty totals" in {
        val model = estimatedLPP2Document.copy(
          documentPenaltyTotals = Some(Seq(documentPenaltyTotalsPosted, estimatedLPP2))
        )
        model.getAccruingPenalty shouldBe Some(estimatedLPP2)
      }
    }

    "return None" when {

      "there is an estimated LPP1 with a due date before the familiarisation period end date" in {
        val model = estimatedLPP1Document.copy(lineItemDetails =
          Seq(lineItemDetailsFull.copy(netDueDate = Some(dateInFamiliarisationPeriod)))
        )
        model.getAccruingPenalty shouldBe None
      }

      "there is only a posted penalty in the document penalty totals" in {
        val model = fullDocumentDetails.copy(documentPenaltyTotals = Some(Seq(documentPenaltyTotalsPosted)))
        model.getAccruingPenalty shouldBe None
      }

      "there are no penalties in the document penalty totals" in {
        val model = fullDocumentDetails.copy(documentPenaltyTotals = Some(Seq(emptyDocumentPenaltyTotal)))
        model.getAccruingPenalty shouldBe None
      }

      "there are no document penalty totals" in {
        val model = fullDocumentDetails.copy(documentPenaltyTotals = None)
        model.getAccruingPenalty shouldBe None
      }

      "there are no line items" in {
        val model = fullDocumentDetails.copy(lineItemDetails = Seq())
        model.getAccruingPenalty shouldBe None
      }

      "there is no due date" in {
        val model = fullDocumentDetails.copy(lineItemDetails = Seq(emptyLineItem))
        model.getAccruingPenalty shouldBe None
      }
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
          "accruingInterestAmount" -> 0.23
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
          "accruingInterestAmount" -> 0.23
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
          "accruingInterestAmount" -> 0.23
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
