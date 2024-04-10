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
  val testVatLPISubTrans = "1175"
  val testSecurityDepositSubTrans = "3160"
  val testOnAccountSubTrans = "0100"
  val testVatRPISubTrans = "1176"
  val testVatInterestCreditSubTrans = "1179"
  val testVatOverpaymentForTaxSubTrans = "1168"
  val testVatOverpaymentForRPISubTrans = "1169"

  val testSupportedChargeTypes: Map[(String, String), String]
  = Map(
    ("0060", testOnAccountSubTrans) -> "Payment on account",
    ("4620", testVatLPISubTrans) -> "VAT Return LPI",
    ("4621", testVatRPISubTrans) -> "VAT Return RPI",
    ("4622", testVatLPISubTrans) -> "VAT Return 1st LPP LPI",
    ("4623", testVatRPISubTrans) -> "VAT Return 1st LPP RPI",
    ("4624", testVatLPISubTrans) -> "VAT Return 2nd LPP LPI",
    ("4625", testVatRPISubTrans) -> "VAT Return 2nd LPP RPI",
    ("4626", testVatLPISubTrans) -> "VAT Return POA LPI",
    ("4627", testVatRPISubTrans) -> "VAT Return POA RPI",
    ("4628", testVatLPISubTrans) -> "VAT Return POA 1st LPP LPI",
    ("4629", testVatRPISubTrans) -> "VAT Return POA 1st LPP RPI",
    ("4630", testVatLPISubTrans) -> "VAT Return POA 2nd LPP LPI",
    ("4631", testVatRPISubTrans) -> "VAT Return POA 2nd LPP RPI",
    ("4632", testVatLPISubTrans) -> "VAT Return AA LPI",
    ("4633", testVatRPISubTrans) -> "VAT Return AA RPI",
    ("4634", testVatLPISubTrans) -> "VAT Return AA 1st LPP LPI",
    ("4635", testVatRPISubTrans) -> "VAT Return AA 1st LPP RPI",
    ("4636", testVatLPISubTrans) -> "VAT Return AA 2nd LPP LPI",
    ("4637", testVatRPISubTrans) -> "VAT Return AA 2nd LPP RPI",
    ("4639", testVatRPISubTrans) -> "VAT Manual RPI",
    ("4652", testVatLPISubTrans) -> "VAT Central Assessment LPI",
    ("4653", testVatRPISubTrans) -> "VAT Central Assessment RPI",
    ("4654", testVatLPISubTrans) -> "VAT CA 1st LPP LPI",
    ("4655", testVatRPISubTrans) -> "VAT CA 1st LPP RPI",
    ("4656", testVatLPISubTrans) -> "VAT CA 2nd LPP LPI",
    ("4657", testVatRPISubTrans) -> "VAT CA 2nd LPP RPI",
    ("4658", testVatLPISubTrans) -> "VAT Officer's Assessment LPI",
    ("4659", testVatRPISubTrans) -> "VAT Officer’s Assessment RPI",
    ("4660", testVatLPISubTrans) -> "VAT OA 1st LPP LPI",
    ("4662", testVatLPISubTrans) -> "VAT OA 2nd LPP LPI",
    ("4664", testVatLPISubTrans) -> "VAT Error Correction LPI",
    ("4661", testVatRPISubTrans) -> "VAT OA 1st LPP RPI",
    ("4663", testVatRPISubTrans) -> "VAT OA 2nd LPP RPI",
    ("4665", testVatRPISubTrans) -> "VAT Error Correction RPI",
    ("4666", testVatLPISubTrans) -> "VAT Error Correct 1st LPP LPI",
    ("4667", testVatRPISubTrans) -> "VAT Error Correct 1st LPP RPI",
    ("4668", testVatLPISubTrans) -> "VAT Error Correct 2nd LPP LPI",
    ("4669", testVatRPISubTrans) -> "VAT Error Correct 2nd LPP RPI",
    ("4670", testVatLPISubTrans) -> "VAT Additional Assessment LPI",
    ("4671", testVatRPISubTrans) -> "VAT Additional Assessment RPI",
    ("4672", testVatLPISubTrans) -> "VAT AA 1st LPP LPI",
    ("4674", testVatLPISubTrans) -> "VAT AA 2nd LPP LPI",
    ("4676", testVatLPISubTrans) -> "VAT Protective Assessment LPI",
    ("4677", testVatRPISubTrans) -> "VAT Protective Assessment RPI",
    ("4678", testVatLPISubTrans) -> "VAT PA 1st LPP LPI",
    ("4680", testVatLPISubTrans) -> "VAT PA 2nd LPP LPI",
    ("4682", testVatLPISubTrans) -> "VAT Miscellaneous Penalty LPI",
    ("4684", testVatLPISubTrans) -> "VAT Civil Evasion Penalty LPI",
    ("4686", testVatLPISubTrans) -> "VAT POA Instalment LPI",
    ("4687", testVatLPISubTrans) -> "VAT Inaccuracy Assessments pen LPI",
    ("4689", testVatLPISubTrans) -> "VAT AA Quarterly Instal LPI",
    ("4691", testVatLPISubTrans) -> "VAT AA Monthly Instal LPI",
    ("4695", testVatLPISubTrans) -> "VAT Wrong Doing Penalty LPI",
    ("4697", testVatLPISubTrans) -> "VAT Carter Penalty LPI",
    ("4698", testVatRPISubTrans) -> "VAT Carter Penalty RPI",
    ("4699", testPenaltyDebitSubTrans) -> "VAT Deferral Penalty",
    ("4700", testVatDebitSubTrans) -> "VAT Return Debit Charge",
    ("4700", testVatCreditSubTrans) -> "VAT Return Credit Charge",
    ("4701", testVatDebitSubTrans) -> "VAT POA Return Debit Charge",
    ("4701", testVatCreditSubTrans) -> "VAT POA Return Credit Charge",
    ("4702", testVatDebitSubTrans) -> "VAT AA Return Debit Charge",
    ("4702", testVatCreditSubTrans) -> "VAT AA Return Credit Charge",
    ("4703", testPenaltyDebitSubTrans) -> "VAT Return 1st LPP",
    ("4704", testPenaltyDebitSubTrans) -> "VAT Return 2nd LPP",
    ("4705", testVatLPISubTrans) -> "VAT OA Default Interest",
    ("4706", testVatLPISubTrans) -> "VAT EC Default Interest",
    ("4707", testVatLPISubTrans) -> "VAT AA Default Interest",
    ("4708", testVatLPISubTrans) -> "VAT PA Default Interest",
    ("4709", testVatLPISubTrans) -> "VAT Repayment Supplement Rec",
    ("4710", testVatRPISubTrans) -> "VAT Repayment Supplement",
    ("4711", testVatDebitSubTrans) -> "VAT Indirect Tax Revenue Rec",
    ("4715", testVatRPISubTrans) -> "VAT Statutory Interest",
    ("4716", testPenaltyDebitSubTrans) -> "VAT POA Return 1st LPP",
    ("4717", testPenaltyDebitSubTrans) -> "VAT POA Return 2nd LPP",
    ("4718", testPenaltyDebitSubTrans) -> "VAT AA Return Charge 1st LPP",
    ("4719", testPenaltyDebitSubTrans) -> "VAT AA Return Charge 2nd LPP",
    ("4720", testVatDebitSubTrans) -> "VAT Central Assessment",
    ("4721", testVatLPISubTrans) -> "VAT Default Interest Debit",
    ("4721", testVatInterestCreditSubTrans) -> "VAT Default Interest Credit",
    ("4722", testVatLPISubTrans) -> "VAT Further Interest Debit",
    ("4722", testVatInterestCreditSubTrans) -> "VAT Further Interest Credit",
    ("4723", testPenaltyDebitSubTrans) -> "VAT Central Assessment 1st LPP",
    ("4724", testPenaltyDebitSubTrans) -> "VAT Central Assessment 2nd LPP",
    ("4725", testVatLPISubTrans) -> "VAT OA Further Interest",
    ("4726", testVatLPISubTrans) -> "VAT AA Further Interest",
    ("4727", testVatLPISubTrans) -> "VAT EC Further Interest",
    ("4728", testVatLPISubTrans) -> "VAT PA Further Interest",
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
    ("4749", testVatLPISubTrans) -> "VAT LSP Interest",
    ("4751", testVatDebitSubTrans) -> "VAT Unrepayable Overpayment",
    ("4752", testVatRPISubTrans) -> "VAT LSP Repayment Interest",
    ("4753", testVatDebitSubTrans) -> "VAT POA Instalments",
    ("4755", testPenaltyDebitSubTrans) -> "VAT Inaccuracy Assessments Pen",
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
    ("4767", testVatLPISubTrans) -> "VAT FTN Mat Chg Post 2010 LPI",
    ("4770", testPenaltyDebitSubTrans) -> "VAT Inaccuracies in EC Sales",
    ("4773", testPenaltyDebitSubTrans) -> "VAT Failure to Submit EC Sales",
    ("4774", testVatLPISubTrans) -> "VAT Fail to Sub EC Sales LPI",
    ("4775", testPenaltyDebitSubTrans) -> "VAT Carter Penalty",
    ("4776", testPenaltyDebitSubTrans) -> "VAT FTN Each Partner",
    ("4777", testVatLPISubTrans) -> "VAT FTN Each Partner LPI",
    ("4780", testPenaltyDebitSubTrans) -> "VAT OA Inaccuracies from 2009",
    ("4781", testVatLPISubTrans) -> "VAT OA Inaccur from 2009 LPI",
    ("4783", testPenaltyDebitSubTrans) -> "VAT Inaccuracy return replaced",
    ("4784", testVatLPISubTrans) -> "VAT Inaccuracy Return Replaced LPI",
    ("4786", testPenaltyDebitSubTrans) -> "VAT BNP of Reg Pre 2010",
    ("4787", testPenaltyDebitSubTrans) -> "VAT Manual LPP",
    ("4788", testVatLPISubTrans) -> "VAT Manual LPP LPI",
    ("4789", testVatRPISubTrans) -> "VAT Manual LPP RPI",
    ("4790", testPenaltyDebitSubTrans) -> "VAT FTN RCSL",
    ("4793", testPenaltyDebitSubTrans) -> "VAT Failure to submit RCSL",
    ("4794", testVatLPISubTrans) -> "VAT Failure to Submit RCSL LPI",
    ("4796", testPenaltyDebitSubTrans) -> "VAT MP pre 2009",
    ("4797", testVatRPISubTrans) -> "VAT RPI Recovery",
    ("4799", testPenaltyDebitSubTrans) -> "VAT MP (R) pre 2009",
    ("7700", testVatDebitSubTrans) -> "VAT Return Debit Charge",
    ("7700", testVatCreditSubTrans) -> "VAT Return Credit Charge",
    ("7701", testVatDebitSubTrans) -> "VAT POA Return Debit Charge",
    ("7701", testVatCreditSubTrans) -> "VAT POA Return Credit Charge",
    ("7702", testVatDebitSubTrans) -> "VAT AA Return Debit Charge",
    ("7702", testVatCreditSubTrans) -> "VAT AA Return Credit Charge",
    ("7704", testVatDebitSubTrans) -> "VAT Migrated Liabilities debit",
    ("7705", testVatCreditSubTrans) -> "VAT Migrated Credit",
    ("7710", testVatRPISubTrans) -> "VAT Repayment Supplement",
    ("7720", testVatDebitSubTrans) -> "VAT Central Assessment",
    ("7721", testVatLPISubTrans) -> "VAT Default Interest Debit",
    ("7722", testVatLPISubTrans) -> "VAT Further Interest Debit",
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
    ("4769", testVatLPISubTrans) -> "VAT Overpayment for Tax LPI",
    ("6052", testPenaltyDebitSubTrans) -> "VAT Overpayments 1st LPP",
    ("6053", testPenaltyDebitSubTrans) -> "VAT Overpayments 2nd LPP",
    ("6054", testVatLPISubTrans) -> "VAT Overpayments 1st LPP LPI",
    ("6055", testVatLPISubTrans) -> "VAT Overpayments 2nd LPP LPI",
    ("6056", testVatRPISubTrans) -> "VAT Overpayment for Tax RPI",
    ("6057", testVatRPISubTrans) -> "VAT Overpayments 1st LPP RPI",
    ("6058", testVatRPISubTrans) -> "VAT Overpayments 2nd LPP RPI",
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

      "each charge tye is supported" when {
        ChargeTypes.supportedChargeTypesExt().foreach { charge =>
          s"return true for $charge" in {
            val lineItems = Seq(lineItemDetailsFull.copy(mainTransaction = Some(charge._1._1), subTransaction = Some(charge._1._2)))
            ChargeTypes.chargeTypeIsSupportedCheck(lineItems.head, fullDocumentDetails.chargeReferenceNumber) shouldBe true
          }
        }
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

      "the charge type is supported" in {

        val lineItems = Seq(lineItemDetailsFull)
        val dDetails = Seq(fullDocumentDetails.copy(lineItemDetails = lineItems))
        ChargeTypes.removeInvalidCharges(dDetails) shouldBe Seq(fullDocumentDetails)
      }

      "there are multiple supported charge types" in {

        val lineItems = Seq(
          lineItemDetailsFull,
          lineItemDetailsFull.copy(mainTransaction = Some("4757"), subTransaction = Some("1174")),
          lineItemDetailsFull.copy(mainTransaction = Some("4702"), subTransaction = Some("1177"))
        )
        val dDetails = Seq(fullDocumentDetails.copy(lineItemDetails = lineItems))
        ChargeTypes.removeInvalidCharges(dDetails) shouldBe Seq(fullDocumentDetails.copy(lineItemDetails = lineItems))
      }

      "for each supported charge type" when {
        ChargeTypes.supportedChargeTypesExt().foreach { charge =>
          s"return true for $charge" in {
            val lineItems = Seq(lineItemDetailsFull.copy(mainTransaction = Some(charge._1._1), subTransaction = Some(charge._1._2)))
            val dDetails = Seq(fullDocumentDetails.copy(lineItemDetails = lineItems))
            ChargeTypes.removeInvalidCharges(dDetails) shouldBe Seq(fullDocumentDetails.copy(lineItemDetails = lineItems))
          }
        }
      }
    }
  }

  "supportedChargeTypesExt" when {

    "penaltyReformChargeTypesEnabled is true" must {

      "have 181 charge types" in {

        mockAppConfig.features.penaltyReformChargeTypesEnabled.apply(true)

        val expectedResult = 181

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

      "have 171 charge types" in {

        mockAppConfig.features.penaltyReformChargeTypesEnabled.apply(false)

        val expectedResult = 171

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
