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

import config.AppConfig
import models.API1811.{DocumentDetails, LineItemDetails}
import play.api.mvc.Request
import utils.LoggerUtil

object ChargeTypes extends LoggerUtil {

  val vatDebitSubTrans = "1174"
  val vatCreditSubTrans = "1177"
  val penaltyDebitSubTrans = "1090"
  val penaltyCreditSubTrans = "1094"
  val vatInterestSubTrans = "1175"
  val securityDepositSubTrans = "3160"
  val onAccountSubTrans = "0100"
  val vatInterestRepaymentsSubTrans = "1176"
  val vatInterestCreditSubTrans = "1179"
  val vatOverpaymentForTaxSubTrans = "1168"
  val vatOverpaymentForRPISubTrans = "1169"

  private val supportedChargeTypes: Map[(String, String), String] = Map(
    ("0060", onAccountSubTrans) -> "Payment on account",
    ("4620", vatInterestSubTrans) -> "VAT Return LPI",
    ("4622", vatInterestSubTrans) -> "VAT Return 1st LPP LPI",
    ("4624", vatInterestSubTrans) -> "VAT Return 2nd LPP LPI",
    ("4626", vatInterestSubTrans) -> "VAT Return POA LPI",
    ("4628", vatInterestSubTrans) -> "VAT Return POA 1st LPP LPI",
    ("4630", vatInterestSubTrans) -> "VAT Return POA 2nd LPP LPI",
    ("4632", vatInterestSubTrans) -> "VAT Return AA LPI",
    ("4634", vatInterestSubTrans) -> "VAT Return AA 1st LPP LPI",
    ("4636", vatInterestSubTrans) -> "VAT Return AA 2nd LPP LPI",
    ("4652", vatInterestSubTrans) -> "VAT Central Assessment LPI",
    ("4654", vatInterestSubTrans) -> "VAT CA 1st LPP LPI",
    ("4656", vatInterestSubTrans) -> "VAT CA 2nd LPP LPI",
    ("4658", vatInterestSubTrans) -> "VAT Officer's Assessment LPI",
    ("4660", vatInterestSubTrans) -> "VAT OA 1st LPP LPI",
    ("4662", vatInterestSubTrans) -> "VAT OA 2nd LPP LPI",
    ("4664", vatInterestSubTrans) -> "VAT Error Correction LPI",
    ("4666", vatInterestSubTrans) -> "VAT Error Correct 1st LPP LPI",
    ("4668", vatInterestSubTrans) -> "VAT Error Correct 2nd LPP LPI",
    ("4670", vatInterestSubTrans) -> "VAT Additional Assessment LPI",
    ("4672", vatInterestSubTrans) -> "VAT AA 1st LPP LPI",
    ("4674", vatInterestSubTrans) -> "VAT AA 2nd LPP LPI",
    ("4676", vatInterestSubTrans) -> "VAT Protective Assessment LPI",
    ("4678", vatInterestSubTrans) -> "VAT PA 1st LPP LPI",
    ("4680", vatInterestSubTrans) -> "VAT PA 2nd LPP LPI",
    ("4686", vatInterestSubTrans) -> "VAT POA Instalment LPI",
    ("4689", vatInterestSubTrans) -> "VAT AA Quarterly Instal LPI",
    ("4691", vatInterestSubTrans) -> "VAT AA Monthly Instal LPI",
    ("4699", penaltyDebitSubTrans) -> "VAT Deferral Penalty",
    ("4700", vatDebitSubTrans) -> "VAT Return Debit Charge",
    ("4700", vatCreditSubTrans) -> "VAT Return Credit Charge",
    ("4701", vatDebitSubTrans) -> "VAT POA Return Debit Charge",
    ("4701", vatCreditSubTrans) -> "VAT POA Return Credit Charge",
    ("4702", vatDebitSubTrans) -> "VAT AA Return Debit Charge",
    ("4702", vatCreditSubTrans) -> "VAT AA Return Credit Charge",
    ("4703", penaltyDebitSubTrans) -> "VAT Return 1st LPP",
    ("4704", penaltyDebitSubTrans) -> "VAT Return 2nd LPP",
    ("4705", vatInterestSubTrans) -> "VAT OA Default Interest",
    ("4706", vatInterestSubTrans) -> "VAT EC Default Interest",
    ("4707", vatInterestSubTrans) -> "VAT AA Default Interest",
    ("4708", vatInterestSubTrans) -> "VAT PA Default Interest",
    ("4709", vatInterestSubTrans) -> "VAT Repayment Supplement Rec",
    ("4710", vatInterestRepaymentsSubTrans) -> "VAT Repayment Supplement",
    ("4711", vatDebitSubTrans) -> "VAT Indirect Tax Revenue Rec",
    ("4715", vatInterestRepaymentsSubTrans) -> "VAT Statutory Interest",
    ("4716", penaltyDebitSubTrans) -> "VAT POA Return 1st LPP",
    ("4717", penaltyDebitSubTrans) -> "VAT POA Return 2nd LPP",
    ("4718", penaltyDebitSubTrans) -> "VAT AA Return Charge 1st LPP",
    ("4719", penaltyDebitSubTrans) -> "VAT AA Return Charge 2nd LPP",
    ("4720", vatDebitSubTrans) -> "VAT Central Assessment",
    ("4721", vatInterestSubTrans) -> "VAT Default Interest Debit",
    ("4721", vatInterestCreditSubTrans) -> "VAT Default Interest Credit",
    ("4722", vatInterestSubTrans) -> "VAT Further Interest Debit",
    ("4722", vatInterestCreditSubTrans) -> "VAT Further Interest Credit",
    ("4723", penaltyDebitSubTrans) -> "VAT Central Assessment 1st LPP",
    ("4724", penaltyDebitSubTrans) -> "VAT Central Assessment 2nd LPP",
    ("4725", vatInterestSubTrans) -> "VAT OA Further Interest",
    ("4726", vatInterestSubTrans) -> "VAT AA Further Interest",
    ("4727", vatInterestSubTrans) -> "VAT EC Further Interest",
    ("4728", vatInterestSubTrans) -> "VAT PA Further Interest",
    ("4730", vatDebitSubTrans) -> "VAT OA Debit Charge",
    ("4730", vatCreditSubTrans) -> "VAT OA Credit Charge",
    ("4731", vatDebitSubTrans) -> "VAT EC Debit Charge",
    ("4731", vatCreditSubTrans) -> "VAT EC Credit Charge",
    ("4732", vatDebitSubTrans) -> "VAT Additional Assessment",
    ("4733", vatDebitSubTrans) -> "VAT Protective Assessment",
    ("4735", penaltyDebitSubTrans) -> "VAT Miscellaneous Penalty",
    ("4740", securityDepositSubTrans) -> "VAT Security Deposit Request",
    ("4741", penaltyDebitSubTrans) -> "VAT OA 1st LPP",
    ("4742", penaltyDebitSubTrans) -> "VAT OA 2nd LPP",
    ("4743", penaltyDebitSubTrans) -> "VAT Error Correction 1st LPP",
    ("4744", penaltyDebitSubTrans) -> "VAT Error Correction 2nd LPP",
    ("4745", penaltyDebitSubTrans) -> "VAT Civil Evasion Penalty",
    ("4747", penaltyDebitSubTrans) -> "VAT Debit Default Surcharge",
    ("4747", penaltyCreditSubTrans) -> "VAT Credit Default Surcharge",
    ("4748", penaltyDebitSubTrans) -> "VAT Late Submission Pen",
    ("4749", vatInterestSubTrans) -> "VAT LSP Interest",
    ("4751", vatDebitSubTrans) -> "VAT Unrepayable Overpayment",
    ("4753", vatDebitSubTrans) -> "VAT POA Instalments",
    ("4755", penaltyDebitSubTrans) -> "VAT Inaccuracy Assessments pen",
    ("4756", vatDebitSubTrans) -> "VAT AA Quarterly Instalments",
    ("4757", vatDebitSubTrans) -> "VAT AA Monthly Instalment",
    ("4758", penaltyDebitSubTrans) -> "VAT AA 1st LPP",
    ("4759", penaltyDebitSubTrans) -> "VAT AA 2nd LPP",
    ("4760", penaltyDebitSubTrans) -> "VAT BNP of Reg Post 2010",
    ("4761", penaltyDebitSubTrans) -> "VAT PA 1st LPP",
    ("4762", penaltyDebitSubTrans) -> "VAT PA 2nd LPP",
    ("4763", penaltyDebitSubTrans) -> "VAT FTN Mat Change Pre 2010",
    ("4765", penaltyDebitSubTrans) -> "VAT Wrong Doing Penalty",
    ("4766", penaltyDebitSubTrans) -> "VAT FTN Mat Change Post 2010",
    ("4770", penaltyDebitSubTrans) -> "VAT Inaccuracies in EC Sales",
    ("4773", penaltyDebitSubTrans) -> "VAT Failure to Submit EC Sales",
    ("4775", penaltyDebitSubTrans) -> "VAT Carter Penalty",
    ("4776", penaltyDebitSubTrans) -> "VAT FTN Each Partner",
    ("4780", penaltyDebitSubTrans) -> "VAT OA Inaccuracies from 2009",
    ("4783", penaltyDebitSubTrans) -> "VAT Inaccuracy return replaced",
    ("4786", penaltyDebitSubTrans) -> "VAT BNP of Reg Pre 2010",
    ("4787", penaltyDebitSubTrans) -> "VAT Manual LPP",
    ("4788", vatInterestSubTrans) -> "VAT Manual LPP LPI",
    ("4790", penaltyDebitSubTrans) -> "VAT FTN RCSL",
    ("4793", penaltyDebitSubTrans) -> "VAT Failure to submit RCSL",
    ("4796", penaltyDebitSubTrans) -> "VAT MP pre 2009",
    ("4799", penaltyDebitSubTrans) -> "VAT MP (R) pre 2009",
    ("7700", vatDebitSubTrans) -> "VAT Return Debit Charge",
    ("7700", vatCreditSubTrans) -> "VAT Return Credit Charge",
    ("7701", vatDebitSubTrans) -> "VAT POA Return Debit Charge",
    ("7701", vatCreditSubTrans) -> "VAT POA Return Credit Charge",
    ("7702", vatDebitSubTrans) -> "VAT AA Return Debit Charge",
    ("7702", vatCreditSubTrans) -> "VAT AA Return Credit Charge",
    ("7704", vatDebitSubTrans) -> "VAT Migrated Liabilities debit",
    ("7705", vatCreditSubTrans) -> "VAT Migrated Credit",
    ("7710", vatInterestRepaymentsSubTrans) -> "VAT Repayment Supplement",
    ("7720", vatDebitSubTrans) -> "VAT Central Assessment",
    ("7721", vatInterestSubTrans) -> "VAT Default Interest Debit",
    ("7722", vatInterestSubTrans) -> "VAT Further Interest Debit",
    ("7730", vatDebitSubTrans) -> "VAT OA Debit Charge",
    ("7730", vatCreditSubTrans) -> "VAT OA Credit Charge",
    ("7731", vatDebitSubTrans) -> "VAT EC Debit Charge",
    ("7731", vatCreditSubTrans) -> "VAT EC Credit Charge",
    ("7735", penaltyDebitSubTrans) -> "VAT Miscellaneous Penalty",
    ("7745", penaltyDebitSubTrans) -> "VAT Civil Evasion Penalty",
    ("7747", penaltyDebitSubTrans) -> "VAT Debit Default Surcharge",
    ("7755", penaltyDebitSubTrans) -> "VAT Inaccuracy Assessments pen",
    ("7760", penaltyDebitSubTrans) -> "VAT BNP of Reg Post 2010",
    ("7765", penaltyDebitSubTrans) -> "VAT Wrong Doing Penalty",
    ("7766", penaltyDebitSubTrans) -> "VAT FTN Mat Change Post 2010",
    ("7770", penaltyDebitSubTrans) -> "VAT Inaccuracies in EC Sales",
    ("7773", penaltyDebitSubTrans) -> "VAT Failure to Submit EC Sales",
    ("7775", penaltyDebitSubTrans) -> "VAT Carter Penalty",
    ("7776", penaltyDebitSubTrans) -> "VAT FTN Each Partner",
    ("7780", penaltyDebitSubTrans) -> "VAT OA Inaccuracies from 2009",
    ("7783", penaltyDebitSubTrans) -> "VAT Inaccuracy return replaced",
    ("7786", penaltyDebitSubTrans) -> "VAT BNP of Reg Pre 2010",
    ("7796", penaltyDebitSubTrans) -> "VAT MP pre 2009",
    ("7799", penaltyDebitSubTrans) -> "VAT MP (R) pre 2009"
  )

  private val penaltyReformChargeTypes: Map[(String, String), String] = Map(
    ("4764", vatOverpaymentForTaxSubTrans) -> "VAT Overpayment for Tax",
    ("4764", vatOverpaymentForRPISubTrans) -> "VAT Overpayment for RPI",
    ("4769", vatInterestSubTrans) -> "VAT Overpayment for Tax LPI",
    ("6052", penaltyDebitSubTrans) -> "VAT Overpayments 1st LPP",
    ("6053", penaltyDebitSubTrans) -> "VAT Overpayments 2nd LPP",
    ("6054", vatInterestSubTrans) -> "VAT Overpayments 1st LPP LPI",
    ("6055", vatInterestSubTrans) -> "VAT Overpayments 2nd LPP LPI",
    ("6056", vatInterestRepaymentsSubTrans) -> "VAT Overpayment for Tax RPI",
    ("6057", vatInterestRepaymentsSubTrans) -> "VAT Overpayments 1st LPP RPI",
    ("6058", vatInterestRepaymentsSubTrans) -> "VAT Overpayments 2nd LPP RPI"
  )

  def supportedChargeTypesExt()(implicit appConfig: AppConfig, request: Request[_]): Map[(String, String), String] = {
    if (appConfig.features.penaltyReformChargeTypesEnabled.apply()) {
      infoLog("[ChargeTypes][supportedChargeTypesExt] penaltyReformChargeTypesEnabled is true. Using Penalty Reform charge types")
      supportedChargeTypes ++ penaltyReformChargeTypes
    } else {
      infoLog("[ChargeTypes][supportedChargeTypesExt] penaltyReformChargeTypesEnabled is false. NOT using Penalty Reform charge types")
      supportedChargeTypes
    }
  }

  def retrieveChargeType(mainTransaction: Option[String],
                         subTransaction: Option[String]
                        )(implicit appConfig: AppConfig, request: Request[_]): Option[String] =
    supportedChargeTypesExt().get((mainTransaction.getOrElse(""), subTransaction.getOrElse("")))

  def chargeTypeIsSupportedCheck(charge: LineItemDetails,
                                 chargeReferenceNumber: Option[String]
                                )(implicit appConfig: AppConfig, request: Request[_]): Boolean = {
    (charge.mainTransaction, charge.subTransaction) match {
      case (Some(mainTrans), Some(subTrans)) if supportedChargeTypesExt().contains((mainTrans, subTrans)) =>
        true
      case (Some(mainTrans), Some(subTrans)) =>
        warnLog(s"[ChargeTypes][chargeTypeIsSupportedCheck] charge type not supported: mainTrans: $mainTrans subTrans: $subTrans")
        false
      case _ =>
        warnLog("[ChargeTypes][chargeTypeIsSupportedCheck] - Insufficient transaction values provided for charge, " +
          s"reference: $chargeReferenceNumber, main: ${charge.mainTransaction.getOrElse("none")}, " +
          s"sub: ${charge.mainTransaction.getOrElse("none")}")
        false
    }
  }

  def removeInvalidCharges(transactions: Seq[DocumentDetails]
                          )(implicit appConfig: AppConfig, request: Request[_]): Seq[DocumentDetails] = {

    val filteredTransactions =
      transactions.filter { document =>
        val filteredCharges = document.lineItemDetails.filter { charge =>
          chargeTypeIsSupportedCheck(charge, document.chargeReferenceNumber)
        }
        document.lineItemDetails.length == filteredCharges.length
      }
    val removedTransactions = transactions.diff(filteredTransactions)

    if (removedTransactions.nonEmpty) {
      warnLog(s"[ChargeTypes][removeInvalidCharges]" +
        s"Charges removed: ${removedTransactions.length} Charge refs: ${removedTransactions.map(_.chargeReferenceNumber)}"
      )
    }
    filteredTransactions
  }
}
