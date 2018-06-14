/*
 * Copyright 2017 HM Revenue & Customs
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

package models

import base.SpecBase
import play.api.libs.json.{JsValue, Json}

class DirectDebitsSpec extends SpecBase {

  val directDebitDetail = Seq(
    DirectDebitDetail(directDebitInstructionNumber = "000000001234567898", directDebitPlanType = "VPP",
      dateCreated = "2018-04-08", accountHolderName = "A PERSON", sortCode = "000000", accountNumber = "000000001"),
    DirectDebitDetail(directDebitInstructionNumber = "000000001234567899", directDebitPlanType = "VPP",
      dateCreated = "2018-04-09", accountHolderName = "ANOTHER PERSON", sortCode = "000001", accountNumber = "000000002"))

  val directDebitsModel: DirectDebits = DirectDebits(directDebitMandateFound = true, Some(directDebitDetail))

  val directDebitsModelJson: JsValue =
    Json.obj(
      "directDebitMandateFound" -> true,
      "directDebitDetails" -> Json.arr(
        Json.obj(
          "directDebitInstructionNumber" -> "000000001234567898",
          "directDebitPlanType" -> "VPP",
          "dateCreated" -> "2018-04-08",
          "accountHolderName" -> "A PERSON",
          "sortCode" -> "000000",
          "accountNumber" -> "000000001"
        ),
        Json.obj(
          "directDebitInstructionNumber" -> "000000001234567899",
          "directDebitPlanType" -> "VPP",
          "dateCreated" -> "2018-04-09",
          "accountHolderName" -> "ANOTHER PERSON",
          "sortCode" -> "000001",
          "accountNumber" -> "000000002"
        )
      )
    )

  "DirectDebits" should {

    "serialize to Json successfully" in {
      Json.toJson(directDebitsModel) shouldBe directDebitsModelJson
    }

    "deserialize to a Direct debit model successfully" in {
      directDebitsModelJson.as[DirectDebits] shouldBe directDebitsModel
    }

  }

}
