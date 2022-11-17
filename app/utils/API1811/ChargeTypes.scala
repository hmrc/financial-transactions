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

import config.AppConfig
import models.API1811.DocumentDetails
import utils.LoggerUtil

object ChargeTypes extends LoggerUtil {

  val vatDebitSubTrans = "1174"
  val vatCreditSubTrans = "1177"
  val penaltyDebitSubTrans = "1090"
  val penaltyCreditSubTrans = "1094"
  val vatInterestSubTrans = "1175"
  val securityDepositSubTrans = "3160"
  val onAccountSubTrans = "0100"

  private val establishedChargeTypes: Map[(String, String), String] = Map(
    ("0060", onAccountSubTrans) -> "Payment on account",
    ("4700", vatDebitSubTrans) -> "VAT Return Debit Charge",
    ("4700", vatCreditSubTrans) -> "VAT Return Credit Charge",
    ("4701", vatDebitSubTrans) -> "VAT POA Return Debit Charge",
    ("4701", vatCreditSubTrans) -> "VAT POA Return Credit Charge",
    ("4702", vatDebitSubTrans) -> "VAT AA Return Debit Charge",
    ("4702", vatCreditSubTrans) -> "VAT AA Return Credit Charge",
    ("4705", vatInterestSubTrans) -> "VAT OA Default Interest",
    ("4706", vatInterestSubTrans) -> "VAT EC Default Interest",
    ("4707", vatInterestSubTrans) -> "VAT AA Default Interest",
    ("4708", vatInterestSubTrans) -> "VAT PA Default Interest",
    ("4709", vatInterestSubTrans) -> "VAT Repayment Supplement Rec",
    ("4710", "") -> "VAT Repayment Supplement",                                         // TODO
    ("4711", vatDebitSubTrans) -> "VAT Indirect Tax Revenue Rec",
    ("4715", "") -> "VAT Statutory Interest",                                           // TODO
    ("4720", vatDebitSubTrans) -> "VAT Central Assessment",
    ("4721", "") -> "VAT Default Interest",                                             // TODO
    ("4722", "") -> "VAT Further Interest",                                             // TODO
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
    ("4745", penaltyDebitSubTrans) -> "VAT Civil Evasion Penalty",
    ("4747", penaltyDebitSubTrans) -> "VAT Debit Default Surcharge",
    ("4747", penaltyCreditSubTrans) -> "VAT Credit Default Surcharge",
    ("4751", "") -> "VAT Unrepayable Overpayment",                                      // TODO
    ("4753", vatDebitSubTrans) -> "VAT POA Instalments",
    ("4755", penaltyDebitSubTrans) -> "VAT Inaccuracy Assessments pen",
    ("4756", vatDebitSubTrans) -> "VAT AA Quarterly Instalments",
    ("4757", vatDebitSubTrans) -> "VAT AA Monthly Instalment",
    ("4760", penaltyDebitSubTrans) -> "VAT BNP of Reg Post 2010",
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
    ("4790", penaltyDebitSubTrans) -> "VAT FTN RCSL",
    ("4793", penaltyDebitSubTrans) -> "VAT Failure to submit RCSL",
    ("4796", penaltyDebitSubTrans) -> "VAT MP pre 2009",
    ("4799", penaltyDebitSubTrans) -> "VAT MP (R) pre 2009",
    ("7704", vatDebitSubTrans) -> "VAT Migrated Liabilities debit",
    ("7705", vatCreditSubTrans) -> "VAT Migrated Credit"
  )

  private val penaltiesAndInterestChargeTypes: Map[(String, String), String] = Map(
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
    ("4703", penaltyDebitSubTrans) -> "VAT Return 1st LPP",
    ("4704", penaltyDebitSubTrans) -> "VAT Return 2nd LPP",
    ("4716", penaltyDebitSubTrans) -> "VAT POA Return 1st LPP",
    ("4717", penaltyDebitSubTrans) -> "VAT POA Return 2nd LPP",
    ("4718", penaltyDebitSubTrans) -> "VAT AA Return Charge 1st LPP",
    ("4719", penaltyDebitSubTrans) -> "VAT AA Return Charge 2nd LPP",
    ("4743", penaltyDebitSubTrans) -> "VAT Error Correction 1st LPP",
    ("4744", penaltyDebitSubTrans) -> "VAT Error Correction 2nd LPP",
    ("4748", penaltyDebitSubTrans) -> "VAT Late Submission Pen",
    ("4749", vatInterestSubTrans) -> "VAT LSP Interest",
    ("4758", penaltyDebitSubTrans) -> "VAT AA 1st LPP",
    ("4759", penaltyDebitSubTrans) -> "VAT AA 2nd LPP",
    ("4761", penaltyDebitSubTrans) -> "VAT PA 1st LPP",
    ("4762", penaltyDebitSubTrans) -> "VAT PA 2nd LPP",
    ("4787", penaltyDebitSubTrans) -> "VAT Manual LPP",
    ("4788", vatInterestSubTrans) -> "VAT Manual LPP LPI"
  )

  private[utils] def supportedChargeList(implicit appConfig: AppConfig): Map[(String, String), String] =
    if (appConfig.features.includePenAndIntCharges()) {
      establishedChargeTypes ++ penaltiesAndInterestChargeTypes
    } else {
      establishedChargeTypes
    }

  def retrieveChargeType(mainTransaction: Option[String], subTransaction: Option[String])
                        (implicit appConfig: AppConfig): Option[String] =
    supportedChargeList.get((mainTransaction.getOrElse(""), subTransaction.getOrElse("")))

  def removeInvalidCharges(transactions: Seq[DocumentDetails])(implicit appConfig: AppConfig): Seq[DocumentDetails] = {
    val supportedCharges = supportedChargeList
    transactions.filter { document =>
      val filtered = document.lineItemDetails.filter { charge =>
        (charge.mainTransaction, charge.subTransaction) match {
          case (Some(mainTrans), Some(subTrans)) =>
            supportedCharges.contains((mainTrans, subTrans))
          case _ =>
            logger.warn("[ChargeTypes][removeInvalidLineItems] - Insufficient transaction values provided for charge, " +
              s"reference: ${document.chargeReferenceNumber}, main: ${charge.mainTransaction.getOrElse("none")}, " +
              s"sub: ${charge.mainTransaction.getOrElse("none")}")
            false
        }
      }
      document.lineItemDetails.length == filtered.length
    }
  }
}
