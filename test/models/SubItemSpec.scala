/*
 * Copyright 2019 HM Revenue & Customs
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
import utils.ImplicitDateFormatter._

class SubItemSpec extends SpecBase {

  val subItemModel = SubItem(
    subItem = Some("000"),
    dueDate = Some("2018-2-14"),
    amount = Some(3400.00),
    clearingDate = Some("2018-2-17"),
    clearingReason = Some("A"),
    outgoingPaymentMethod = Some("B"),
    paymentLock = Some("C"),
    clearingLock = Some("D"),
    interestLock = Some("E"),
    dunningLock = Some("1"),
    returnFlag = Some(false),
    paymentReference = Some("F"),
    paymentAmount = Some(2000.00),
    paymentMethod = Some("G"),
    paymentLot = Some("H"),
    paymentLotItem = Some("112"),
    clearingSAPDocument = Some("3350000253"),
    statisticalDocument = Some("I"),
    returnReason = Some("J"),
    promiseToPay = Some("K")
  )

  val subItemJson: JsValue =
    Json.obj(
      "subItem" -> "000",
      "dueDate" -> "2018-02-14",
      "amount" -> 3400,
      "clearingDate" -> "2018-02-17",
      "clearingReason" -> "A",
      "outgoingPaymentMethod" -> "B",
      "paymentLock" -> "C",
      "clearingLock" -> "D",
      "interestLock" -> "E",
      "dunningLock" -> "1",
      "returnFlag" -> false,
      "paymentReference" -> "F",
      "paymentAmount" -> 2000,
      "paymentMethod" -> "G",
      "paymentLot" -> "H",
      "paymentLotItem" -> "112",
      "clearingSAPDocument" -> "3350000253",
      "statisticalDocument" -> "I",
      "returnReason" -> "J",
      "promiseToPay" -> "K"
    )

  "SubItem" should {

    "serialize to Json successfully" in {
      Json.toJson(subItemModel) shouldBe subItemJson
    }

    "deserialize to a SubItem model successfully" in {
      subItemJson.as[SubItem] shouldBe subItemModel
    }

  }

}
