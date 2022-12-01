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

package utils.API1811

import base.SpecBase
import TestConstants.{fullDocumentDetails, lineItemDetailsFull}

class ChargeTypesSpec extends SpecBase {

  "The supportedChargeList function" when {

    val vatReturnLPITransaction = ("4620", "1175")

    "the includePenAndIntCharges feature switch is off" should {

      "return a list of charge types excluding those associated with the penalties and interest work package" in {
        mockAppConfig.features.includePenAndIntCharges(false)
        val list = ChargeTypes.supportedChargeList
        list.size shouldBe 55
        list.get(vatReturnLPITransaction) shouldBe None
      }
    }

    "the includePenAndIntCharges feature switch is on" should {

      "return a list of all recognised charge types" in {
        mockAppConfig.features.includePenAndIntCharges(true)
        val list = ChargeTypes.supportedChargeList
        list.size shouldBe 101
        list.get(vatReturnLPITransaction) shouldBe Some("VAT Return LPI")
      }
    }
  }

  "The retrieveChargeType function" should {

    "return the corresponding charge type" when {

      "the transaction IDs are recognised" in {
        ChargeTypes.retrieveChargeType(Some("4700"), Some("1174")) shouldBe Some("VAT Return Debit Charge")
      }
    }

    "return None" when {

      "the transaction IDs are not recognised" in {
        ChargeTypes.retrieveChargeType(Some("1111"), Some("2222")) shouldBe None
      }

      "no main transaction is provided" in {
        ChargeTypes.retrieveChargeType(None, Some("1174")) shouldBe None
      }

      "no sub transaction is provided" in {
        ChargeTypes.retrieveChargeType(Some("4700"), None) shouldBe None
      }
    }
  }

  "The removeInvalidCharges function" should {

    "filter out charges" when {

      "the main transaction value is not supported" in {
        val lineItems = Seq(lineItemDetailsFull.copy(mainTransaction = Some("1111")))
        val dDetails = Seq(fullDocumentDetails.copy(lineItemDetails = lineItems))
        ChargeTypes.removeInvalidCharges(dDetails) shouldBe Seq()
      }

      "the sub transaction value is not supported" in {
        val lineItems = Seq(lineItemDetailsFull.copy(subTransaction = Some("1111")))
        val dDetails = Seq(fullDocumentDetails.copy(lineItemDetails = lineItems))
        ChargeTypes.removeInvalidCharges(dDetails) shouldBe Seq()
      }

      "the main transaction value is not present" in {
        val lineItems = Seq(lineItemDetailsFull.copy(mainTransaction = None))
        val dDetails = Seq(fullDocumentDetails.copy(lineItemDetails = lineItems))
        ChargeTypes.removeInvalidCharges(dDetails) shouldBe Seq()
      }

      "the sub transaction value is not present" in {
        val lineItems = Seq(lineItemDetailsFull.copy(subTransaction = None))
        val dDetails = Seq(fullDocumentDetails.copy(lineItemDetails = lineItems))
        ChargeTypes.removeInvalidCharges(dDetails) shouldBe Seq()
      }

      "there are multiple lineItemDetails and only one of them has invalid transaction values" in {
        val lineItems = Seq(lineItemDetailsFull, lineItemDetailsFull.copy(subTransaction = Some("1111")))
        val dDetails = Seq(fullDocumentDetails.copy(lineItemDetails = lineItems))
        ChargeTypes.removeInvalidCharges(dDetails) shouldBe Seq()
      }
    }

    val vatReturnLPITransaction = lineItemDetailsFull.copy(mainTransaction = Some("4620"), subTransaction = Some("1175"))
    val dDetails = Seq(fullDocumentDetails, fullDocumentDetails.copy(lineItemDetails = Seq(vatReturnLPITransaction)))

    "filter out penalties and interest charges" when {

      "the includePenAndIntCharges feature switch is off" in {
        mockAppConfig.features.includePenAndIntCharges(false)
        ChargeTypes.removeInvalidCharges(dDetails) shouldBe Seq(fullDocumentDetails)
      }
    }

    "filter in penalties and interest charges" when {

      "the includePenAndIntCharges feature switch is on" in {
        mockAppConfig.features.includePenAndIntCharges(true)
        ChargeTypes.removeInvalidCharges(dDetails) shouldBe dDetails
      }
    }
  }
}
