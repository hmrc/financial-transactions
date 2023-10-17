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

package utils.API1811

import base.SpecBase
import org.scalatest.BeforeAndAfterAll
import play.api.test.FakeRequest
import utils.API1811.TestConstants.{fullDocumentDetails, lineItemDetailsFull}

class ChargeTypesSpec extends SpecBase with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    mockAppConfig.features.penaltyReformChargeTypesEnabled.reset()
  }

  implicit val request: FakeRequest[_] = fakeRequest

  val testVatDebitSubTrans = "1174"
  val testVatCreditSubTrans = "1177"
  val testPenaltyDebitSubTrans = "1090"
  val testPenaltyCreditSubTrans = "1094"
  val testVatInterestSubTrans = "1175"
  val testSecurityDepositSubTrans = "3160"
  val testOnAccountSubTrans = "0100"
  val testVatInterestRepaymentsSubTrans = "1176"
  val testVatInterestCreditSubTrans = "1179"
  val testVatOverpaymentForTaxSubTrans = "1168"
  val testVatOverpaymentForRPISubTrans = "1169"

  val testSupportedChargeTypes: Map[(String, String), String]
  = Map(
    ("0060", testOnAccountSubTrans) -> "Payment on account",
    ("4620", testVatInterestSubTrans) -> "VAT Return LPI",
    ("4622", testVatInterestSubTrans) -> "VAT Return 1st LPP LPI",
    ("4624", testVatInterestSubTrans) -> "VAT Return 2nd LPP LPI",
    ("4626", testVatInterestSubTrans) -> "VAT Return POA LPI",
    ("4628", testVatInterestSubTrans) -> "VAT Return POA 1st LPP LPI",
    ("4630", testVatInterestSubTrans) -> "VAT Return POA 2nd LPP LPI",
    ("4632", testVatInterestSubTrans) -> "VAT Return AA LPI",
    ("4634", testVatInterestSubTrans) -> "VAT Return AA 1st LPP LPI",
    ("4636", testVatInterestSubTrans) -> "VAT Return AA 2nd LPP LPI",
    ("4652", testVatInterestSubTrans) -> "VAT Central Assessment LPI",
    ("4654", testVatInterestSubTrans) -> "VAT CA 1st LPP LPI",
    ("4656", testVatInterestSubTrans) -> "VAT CA 2nd LPP LPI",
    ("4658", testVatInterestSubTrans) -> "VAT Officer's Assessment LPI",
    ("4660", testVatInterestSubTrans) -> "VAT OA 1st LPP LPI",
    ("4662", testVatInterestSubTrans) -> "VAT OA 2nd LPP LPI",
    ("4664", testVatInterestSubTrans) -> "VAT Error Correction LPI",
    ("4666", testVatInterestSubTrans) -> "VAT Error Correct 1st LPP LPI",
    ("4668", testVatInterestSubTrans) -> "VAT Error Correct 2nd LPP LPI",
    ("4670", testVatInterestSubTrans) -> "VAT Additional Assessment LPI",
    ("4672", testVatInterestSubTrans) -> "VAT AA 1st LPP LPI",
    ("4674", testVatInterestSubTrans) -> "VAT AA 2nd LPP LPI",
    ("4676", testVatInterestSubTrans) -> "VAT Protective Assessment LPI",
    ("4678", testVatInterestSubTrans) -> "VAT PA 1st LPP LPI",
    ("4680", testVatInterestSubTrans) -> "VAT PA 2nd LPP LPI",
    ("4686", testVatInterestSubTrans) -> "VAT POA Instalment LPI",
    ("4689", testVatInterestSubTrans) -> "VAT AA Quarterly Instal LPI",
    ("4691", testVatInterestSubTrans) -> "VAT AA Monthly Instal LPI",
    ("4699", testPenaltyDebitSubTrans) -> "VAT Deferral Penalty",
    ("4700", testVatDebitSubTrans) -> "VAT Return Debit Charge",
    ("4700", testVatCreditSubTrans) -> "VAT Return Credit Charge",
    ("4701", testVatDebitSubTrans) -> "VAT POA Return Debit Charge",
    ("4701", testVatCreditSubTrans) -> "VAT POA Return Credit Charge",
    ("4702", testVatDebitSubTrans) -> "VAT AA Return Debit Charge",
    ("4702", testVatCreditSubTrans) -> "VAT AA Return Credit Charge",
    ("4703", testPenaltyDebitSubTrans) -> "VAT Return 1st LPP",
    ("4704", testPenaltyDebitSubTrans) -> "VAT Return 2nd LPP",
    ("4705", testVatInterestSubTrans) -> "VAT OA Default Interest",
    ("4706", testVatInterestSubTrans) -> "VAT EC Default Interest",
    ("4707", testVatInterestSubTrans) -> "VAT AA Default Interest",
    ("4708", testVatInterestSubTrans) -> "VAT PA Default Interest",
    ("4709", testVatInterestSubTrans) -> "VAT Repayment Supplement Rec",
    ("4710", testVatInterestRepaymentsSubTrans) -> "VAT Repayment Supplement",
    ("4711", testVatDebitSubTrans) -> "VAT Indirect Tax Revenue Rec",
    ("4715", testVatInterestRepaymentsSubTrans) -> "VAT Statutory Interest",
    ("4716", testPenaltyDebitSubTrans) -> "VAT POA Return 1st LPP",
    ("4717", testPenaltyDebitSubTrans) -> "VAT POA Return 2nd LPP",
    ("4718", testPenaltyDebitSubTrans) -> "VAT AA Return Charge 1st LPP",
    ("4719", testPenaltyDebitSubTrans) -> "VAT AA Return Charge 2nd LPP",
    ("4720", testVatDebitSubTrans) -> "VAT Central Assessment",
    ("4721", testVatInterestSubTrans) -> "VAT Default Interest Debit",
    ("4721", testVatInterestCreditSubTrans) -> "VAT Default Interest Credit",
    ("4722", testVatInterestSubTrans) -> "VAT Further Interest Debit",
    ("4722", testVatInterestCreditSubTrans) -> "VAT Further Interest Credit",
    ("4723", testPenaltyDebitSubTrans) -> "VAT Central Assessment 1st LPP",
    ("4724", testPenaltyDebitSubTrans) -> "VAT Central Assessment 2nd LPP",
    ("4725", testVatInterestSubTrans) -> "VAT OA Further Interest",
    ("4726", testVatInterestSubTrans) -> "VAT AA Further Interest",
    ("4727", testVatInterestSubTrans) -> "VAT EC Further Interest",
    ("4728", testVatInterestSubTrans) -> "VAT PA Further Interest",
    ("4730", testVatDebitSubTrans) -> "VAT OA Debit Charge",
    ("4730", testVatCreditSubTrans) -> "VAT OA Credit Charge",
    ("4731", testVatDebitSubTrans) -> "VAT EC Debit Charge",
    ("4731", testVatCreditSubTrans) -> "VAT EC Credit Charge",
    ("4732", testVatDebitSubTrans) -> "VAT Additional Assessment",
    ("4733", testVatDebitSubTrans) -> "VAT Protective Assessment",
    ("4735", testPenaltyDebitSubTrans) -> "VAT Miscellaneous Penalty",
    ("4740", testSecurityDepositSubTrans) -> "VAT Security Deposit Request",
    ("4741", testPenaltyDebitSubTrans) -> "VAT OA 1st LPP",
    ("4742", testPenaltyDebitSubTrans) -> "VAT OA 2nd LPP",
    ("4743", testPenaltyDebitSubTrans) -> "VAT Error Correction 1st LPP",
    ("4744", testPenaltyDebitSubTrans) -> "VAT Error Correction 2nd LPP",
    ("4745", testPenaltyDebitSubTrans) -> "VAT Civil Evasion Penalty",
    ("4747", testPenaltyDebitSubTrans) -> "VAT Debit Default Surcharge",
    ("4747", testPenaltyCreditSubTrans) -> "VAT Credit Default Surcharge",
    ("4748", testPenaltyDebitSubTrans) -> "VAT Late Submission Pen",
    ("4749", testVatInterestSubTrans) -> "VAT LSP Interest",
    ("4751", testVatDebitSubTrans) -> "VAT Unrepayable Overpayment",
    ("4753", testVatDebitSubTrans) -> "VAT POA Instalments",
    ("4755", testPenaltyDebitSubTrans) -> "VAT Inaccuracy Assessments pen",
    ("4756", testVatDebitSubTrans) -> "VAT AA Quarterly Instalments",
    ("4757", testVatDebitSubTrans) -> "VAT AA Monthly Instalment",
    ("4758", testPenaltyDebitSubTrans) -> "VAT AA 1st LPP",
    ("4759", testPenaltyDebitSubTrans) -> "VAT AA 2nd LPP",
    ("4760", testPenaltyDebitSubTrans) -> "VAT BNP of Reg Post 2010",
    ("4761", testPenaltyDebitSubTrans) -> "VAT PA 1st LPP",
    ("4762", testPenaltyDebitSubTrans) -> "VAT PA 2nd LPP",
    ("4763", testPenaltyDebitSubTrans) -> "VAT FTN Mat Change Pre 2010",
    ("4765", testPenaltyDebitSubTrans) -> "VAT Wrong Doing Penalty",
    ("4766", testPenaltyDebitSubTrans) -> "VAT FTN Mat Change Post 2010",
    ("4770", testPenaltyDebitSubTrans) -> "VAT Inaccuracies in EC Sales",
    ("4773", testPenaltyDebitSubTrans) -> "VAT Failure to Submit EC Sales",
    ("4775", testPenaltyDebitSubTrans) -> "VAT Carter Penalty",
    ("4776", testPenaltyDebitSubTrans) -> "VAT FTN Each Partner",
    ("4780", testPenaltyDebitSubTrans) -> "VAT OA Inaccuracies from 2009",
    ("4783", testPenaltyDebitSubTrans) -> "VAT Inaccuracy return replaced",
    ("4786", testPenaltyDebitSubTrans) -> "VAT BNP of Reg Pre 2010",
    ("4787", testPenaltyDebitSubTrans) -> "VAT Manual LPP",
    ("4788", testVatInterestSubTrans) -> "VAT Manual LPP LPI",
    ("4790", testPenaltyDebitSubTrans) -> "VAT FTN RCSL",
    ("4793", testPenaltyDebitSubTrans) -> "VAT Failure to submit RCSL",
    ("4796", testPenaltyDebitSubTrans) -> "VAT MP pre 2009",
    ("4799", testPenaltyDebitSubTrans) -> "VAT MP (R) pre 2009",
    ("7700", testVatDebitSubTrans) -> "VAT Return Debit Charge",
    ("7700", testVatCreditSubTrans) -> "VAT Return Credit Charge",
    ("7701", testVatDebitSubTrans) -> "VAT POA Return Debit Charge",
    ("7701", testVatCreditSubTrans) -> "VAT POA Return Credit Charge",
    ("7702", testVatDebitSubTrans) -> "VAT AA Return Debit Charge",
    ("7702", testVatCreditSubTrans) -> "VAT AA Return Credit Charge",
    ("7704", testVatDebitSubTrans) -> "VAT Migrated Liabilities debit",
    ("7705", testVatCreditSubTrans) -> "VAT Migrated Credit",
    ("7710", testVatInterestRepaymentsSubTrans) -> "VAT Repayment Supplement",
    ("7720", testVatDebitSubTrans) -> "VAT Central Assessment",
    ("7721", testVatInterestSubTrans) -> "VAT Default Interest Debit",
    ("7722", testVatInterestSubTrans) -> "VAT Further Interest Debit",
    ("7730", testVatDebitSubTrans) -> "VAT OA Debit Charge",
    ("7730", testVatCreditSubTrans) -> "VAT OA Credit Charge",
    ("7731", testVatDebitSubTrans) -> "VAT EC Debit Charge",
    ("7731", testVatCreditSubTrans) -> "VAT EC Credit Charge",
    ("7735", testPenaltyDebitSubTrans) -> "VAT Miscellaneous Penalty",
    ("7745", testPenaltyDebitSubTrans) -> "VAT Civil Evasion Penalty",
    ("7747", testPenaltyDebitSubTrans) -> "VAT Debit Default Surcharge",
    ("7755", testPenaltyDebitSubTrans) -> "VAT Inaccuracy Assessments pen",
    ("7760", testPenaltyDebitSubTrans) -> "VAT BNP of Reg Post 2010",
    ("7765", testPenaltyDebitSubTrans) -> "VAT Wrong Doing Penalty",
    ("7766", testPenaltyDebitSubTrans) -> "VAT FTN Mat Change Post 2010",
    ("7770", testPenaltyDebitSubTrans) -> "VAT Inaccuracies in EC Sales",
    ("7773", testPenaltyDebitSubTrans) -> "VAT Failure to Submit EC Sales",
    ("7775", testPenaltyDebitSubTrans) -> "VAT Carter Penalty",
    ("7776", testPenaltyDebitSubTrans) -> "VAT FTN Each Partner",
    ("7780", testPenaltyDebitSubTrans) -> "VAT OA Inaccuracies from 2009",
    ("7783", testPenaltyDebitSubTrans) -> "VAT Inaccuracy return replaced",
    ("7786", testPenaltyDebitSubTrans) -> "VAT BNP of Reg Pre 2010",
    ("7796", testPenaltyDebitSubTrans) -> "VAT MP pre 2009",
    ("7799", testPenaltyDebitSubTrans) -> "VAT MP (R) pre 2009"
  )

  val testPenaltyReformChargeTypes: Map[(String, String), String] = Map(
    ("4764", testVatOverpaymentForTaxSubTrans) -> "VAT Overpayment for Tax",
    ("4764", testVatOverpaymentForRPISubTrans) -> "VAT Overpayment for RPI",
    ("4769", testVatInterestSubTrans) -> "VAT Overpayment for Tax LPI",
    ("6052", testPenaltyDebitSubTrans) -> "VAT Overpayments 1st LPP",
    ("6053", testPenaltyDebitSubTrans) -> "VAT Overpayments 2nd LPP",
    ("6054", testVatInterestSubTrans) -> "VAT Overpayments 1st LPP LPI",
    ("6055", testVatInterestSubTrans) -> "VAT Overpayments 2nd LPP LPI",
    ("6056", testVatInterestRepaymentsSubTrans) -> "VAT Overpayment for Tax RPI",
    ("6057", testVatInterestRepaymentsSubTrans) -> "VAT Overpayments 1st LPP RPI",
    ("6058", testVatInterestRepaymentsSubTrans) -> "VAT Overpayments 2nd LPP RPI",
  )

  "The retrieveChargeType function" should {

    "return the corresponding charge type" when {

      "the transaction IDs are recognised" in {
        ChargeTypes.retrieveChargeType(Some("4700"), Some("1174")) shouldBe Some("VAT Return Debit Charge")
      }

      "a migrated charge transaction ID is mapped to an existing one" in {
        ChargeTypes.retrieveChargeType(Some("7700"), Some("1174")) shouldBe Some("VAT Return Debit Charge")
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


  "chargeTypeIsSupportedCheck" should {

    "return false" when {

      "the main transaction value is not supported" in {
        val lineItems = Seq(lineItemDetailsFull.copy(mainTransaction = Some("1111")))
        ChargeTypes.chargeTypeIsSupportedCheck(lineItems.head, fullDocumentDetails.chargeReferenceNumber) shouldBe false
      }

      "the sub transaction value is not supported" in {
        val lineItems = Seq(lineItemDetailsFull.copy(subTransaction = Some("1111")))
        ChargeTypes.chargeTypeIsSupportedCheck(lineItems.head, fullDocumentDetails.chargeReferenceNumber) shouldBe false
      }

      "the main transaction value is not present" in {
        val lineItems = Seq(lineItemDetailsFull.copy(mainTransaction = None))
        ChargeTypes.chargeTypeIsSupportedCheck(lineItems.head, fullDocumentDetails.chargeReferenceNumber) shouldBe false
      }

      "the sub transaction value is not present" in {
        val lineItems = Seq(lineItemDetailsFull.copy(subTransaction = None))
        ChargeTypes.chargeTypeIsSupportedCheck(lineItems.head, fullDocumentDetails.chargeReferenceNumber) shouldBe false
      }
    }

    "return true" when {

      "the charge type is supported" in {
        val lineItems = Seq(lineItemDetailsFull)
        ChargeTypes.chargeTypeIsSupportedCheck(lineItems.head, fullDocumentDetails.chargeReferenceNumber) shouldBe true
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

    "not filter out any charges" when {

      "The charge type is supported" in {

        val lineItems = Seq(lineItemDetailsFull)
        val dDetails = Seq(fullDocumentDetails.copy(lineItemDetails = lineItems))
        ChargeTypes.removeInvalidCharges(dDetails) shouldBe Seq(fullDocumentDetails)
      }

      "There are multiple supported charge types" in {

        val lineItems = Seq(
          lineItemDetailsFull,
          lineItemDetailsFull.copy(mainTransaction = Some("4757"), subTransaction = Some("1174")),
          lineItemDetailsFull.copy(mainTransaction = Some("4702"), subTransaction = Some("1177"))
        )
        val dDetails = Seq(fullDocumentDetails.copy(lineItemDetails = lineItems))
        ChargeTypes.removeInvalidCharges(dDetails) shouldBe Seq(fullDocumentDetails.copy(lineItemDetails = lineItems))
      }
    }
  }

  "supportedChargeTypesExt" when {

    "penaltyReformChargeTypesEnabled is true" must {

      "have 145 charge types" in {

        mockAppConfig.features.penaltyReformChargeTypesEnabled.apply(true)

        val expectedResult = 145
        val actualResult = ChargeTypes.supportedChargeTypesExt().size

        expectedResult shouldBe actualResult
      }

      "contain the penalty reform charge types" in {

        mockAppConfig.features.penaltyReformChargeTypesEnabled.apply(true)

        val expectedResult = testSupportedChargeTypes ++ testPenaltyReformChargeTypes
        val actualResult = ChargeTypes.supportedChargeTypesExt()

        expectedResult shouldBe actualResult
      }
    }

    "penaltyReformChargeTypesEnabled is false" must {

      "have 135 charge types" in {

        mockAppConfig.features.penaltyReformChargeTypesEnabled.apply(false)

        val expectedResult = 135
        val actualResult = ChargeTypes.supportedChargeTypesExt().size

        expectedResult shouldBe actualResult
      }

      "contain the penalty reform charge types" in {

        mockAppConfig.features.penaltyReformChargeTypesEnabled.apply(false)

        val expectedResult = testSupportedChargeTypes
        val actualResult = ChargeTypes.supportedChargeTypesExt()

        expectedResult shouldBe actualResult
      }
    }
  }
}
