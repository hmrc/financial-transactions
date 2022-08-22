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

package utils

import config.AppConfig

object ChargeTypes {

  private[utils] val establishedChargeTypes: Set[String] = Set(
    "VAT Unrepayable Overpayment",
    "VAT Repayment Supplement Rec",
    "VAT Indirect Tax Revenue Rec",
    "VAT Default Interest",
    "VAT Further Interest",
    "VAT Return Debit Charge",
    "VAT Return Credit Charge",
    "VAT OA Credit Charge",
    "VAT OA Debit Charge",
    "VAT OA Default Interest",
    "VAT OA Further Interest",
    "VAT Debit Default Surcharge",
    "VAT Credit Default Surcharge",
    "VAT Central Assessment",
    "VAT EC Credit Charge",
    "VAT EC Debit Charge",
    "VAT Repayment Supplement",
    "VAT AA Default Interest",
    "VAT AA Further Interest",
    "VAT Additional Assessment",
    "VAT AA Quarterly Instalments",
    "VAT AA Monthly Instalment",
    "VAT AA Return Debit Charge",
    "VAT AA Return Credit Charge",
    "VAT BNP of Reg Pre 2010",
    "VAT BNP of Reg Post 2010",
    "VAT FTN Mat Change Pre 2010",
    "VAT FTN Mat Change Post 2010",
    "VAT FTN Each Partner",
    "VAT Miscellaneous Penalty",
    "VAT MP pre 2009",
    "VAT MP (R) pre 2009",
    "VAT Civil Evasion Penalty",
    "VAT OA Inaccuracies from 2009",
    "VAT Inaccuracy Assessments pen",
    "VAT Inaccuracy return replaced",
    "VAT Wrong Doing Penalty",
    "VAT Carter Penalty",
    "VAT FTN RCSL",
    "VAT Failure to submit RCSL",
    "VAT Inaccuracies in EC Sales",
    "VAT EC Default Interest",
    "VAT EC Further Interest",
    "VAT Security Deposit Request",
    "VAT Protective Assessment",
    "VAT PA Default Interest",
    "VAT Failure to Submit EC Sales",
    "VAT Statutory Interest",
    "VAT PA Further Interest",
    "Credit Return Offset",
    "Payment on account",
    "VAT POA Return Debit Charge",
    "VAT POA Return Credit Charge",
    "VAT POA Instalments",
    "Unallocated payment",
    "Refund"
  ).map(_.toUpperCase)

  private[utils] val penaltiesAndInterestChargeTypes: Set[String] = Set(
    "VAT Protective Assessment LPI",
    "VAT Return 1st LPP",
    "VAT Return LPI",
    "VAT Return 1st LPP LPI",
    "VAT Return 2nd LPP LPI",
    "VAT Central Assessment LPI",
    "VAT CA 1st LPP LPI",
    "VAT CA 2nd LPP LPI",
    "VAT Officer's Assessment LPI",
    "VAT OA 1st LPP LPI",
    "VAT OA 2nd LPP LPI",
    "VAT PA 1st LPP LPI",
    "VAT PA 2nd LPP LPI",
    "VAT PA 1st LPP",
    "VAT PA 2nd LPP",
    "VAT AA 1st LPP",
    "VAT AA 2nd LPP",
    "VAT Additional Assessment LPI",
    "VAT AA 1st LPP LPI",
    "VAT AA 2nd LPP LPI",
    "VAT AA Quarterly Instal LPI",
    "VAT AA Monthly Instal LPI",
    "VAT AA Return Charge 1st LPP",
    "VAT AA Return Charge 2nd LPP"
  ).map(_.toUpperCase)

  def validChargeTypes(appConfig: AppConfig): Set[String] =
   establishedChargeTypes ++ (if(appConfig.features.includePenAndIntCharges()) penaltiesAndInterestChargeTypes else Set())
}
