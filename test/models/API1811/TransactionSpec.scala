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

package models.API1811

import base.SpecBase
import play.api.libs.json.Json
import utils.API1811.TestConstants.{fullTransaction, fullTransactionJsonEIS, fullTransactionJsonEISOutput}

class TransactionSpec extends SpecBase {

  "Transaction json reads" should {

    "parse JSON to a Transaction model successfully" in {
      fullTransactionJsonEIS.as[Transaction] shouldBe fullTransaction
    }
  }

  "Transaction json writes" should {

    "write a Transaction model to maximum JSON successfully" when {

      "all fields are present" in {
        Json.toJson(fullTransaction) shouldBe fullTransactionJsonEISOutput
      }
    }

    "not include a chargeType field in the output JSON" should {

      "main transaction is not present" in {
        Json.toJson(fullTransaction.copy(mainTransaction = None)) shouldBe Json.obj()
      }

      "sub transaction is not present" in {
        Json.toJson(fullTransaction.copy(subTransaction = None)) shouldBe Json.obj()
      }

      "main transaction and sub transaction are not present" in {
        Json.toJson(fullTransaction.copy(mainTransaction = None, subTransaction = None)) shouldBe Json.obj()
      }
    }
  }
}
