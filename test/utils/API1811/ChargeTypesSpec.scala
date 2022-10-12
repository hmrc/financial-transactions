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
import TestConstants.fullTransaction

class ChargeTypesSpec extends SpecBase {

  "The removeInvalidCharges function" should {

    "filter out charges" when {

      "the main transaction value is not supported" in {
        val transactions = Seq(fullTransaction, fullTransaction.copy(mainTransaction = Some("1111")))
        ChargeTypes.removeInvalidCharges(transactions) shouldBe Seq(fullTransaction)
      }

      "the sub transaction value is not supported" in {
        val transactions = Seq(fullTransaction, fullTransaction.copy(subTransaction = Some("1111")))
        ChargeTypes.removeInvalidCharges(transactions) shouldBe Seq(fullTransaction)
      }

      "the main transaction value is not present" in {
        val transactions = Seq(fullTransaction, fullTransaction.copy(mainTransaction = None))
        ChargeTypes.removeInvalidCharges(transactions) shouldBe Seq(fullTransaction)
      }

      "the sub transaction value is not present" in {
        val transactions = Seq(fullTransaction, fullTransaction.copy(subTransaction = None))
        ChargeTypes.removeInvalidCharges(transactions) shouldBe Seq(fullTransaction)
      }
    }

    val vatReturnLPITransaction = fullTransaction.copy(mainTransaction = Some("4620"), subTransaction = Some("1175"))
    val transactions = Seq(fullTransaction, vatReturnLPITransaction)

    "filter out penalties and interest charges" when {

      "the includePenAndIntCharges feature switch is off" in {
        mockAppConfig.features.includePenAndIntCharges(false)
        ChargeTypes.removeInvalidCharges(transactions) shouldBe Seq(fullTransaction)
      }
    }

    "filter in penalties and interest charges" when {

      "the includePenAndIntCharges feature switch is on" in {
        mockAppConfig.features.includePenAndIntCharges(true)
        ChargeTypes.removeInvalidCharges(transactions) shouldBe transactions
      }
    }
  }
}
