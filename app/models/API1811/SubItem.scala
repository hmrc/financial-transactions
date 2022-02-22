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

import java.time.LocalDate

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class SubItem(subItem: Option[String] = None,
                   dueDate: Option[LocalDate] = None,
                   amount: Option[BigDecimal] = None,
                   clearingDate: Option[LocalDate] = None,
                   clearingReason: Option[String] = None,
                   outgoingPaymentMethod: Option[String] = None,
                   paymentLock: Option[String] = None,
                   clearingLock: Option[String] = None,
                   interestLock: Option[String] = None,
                   dunningLock: Option[String] = None,
                   returnFlag: Option[Boolean] = None,
                   paymentReference: Option[String] = None,
                   paymentAmount: Option[BigDecimal] = None,
                   paymentMethod: Option[String] = None,
                   paymentLot: Option[String] = None,
                   paymentLotItem: Option[String] = None,
                   clearingSAPDocument: Option[String] = None,
                   statisticalDocument: Option[String] = None,
                   DDcollectionInProgress: Option[Boolean] = None,
                   returnReason: Option[String] = None,
                   promiseToPay: Option[String] = None)

object SubItem {
  implicit val reads: Reads[SubItem] = (
    (JsPath \ "subItem").readNullable[String] and
    (JsPath \ "dueDate").readNullable[LocalDate] and
    (JsPath \ "amount").readNullable[BigDecimal] and
    (JsPath \ "clearingDate").readNullable[LocalDate] and
    (JsPath \ "clearingReason").readNullable[String] and
    (JsPath \ "outgoingPaymentMethod").readNullable[String] and
    (JsPath \ "paymentLock").readNullable[String] and
    (JsPath \ "clearingLock").readNullable[String] and
    (JsPath \ "interestLock").readNullable[String] and
    (JsPath \ "dunningLock").readNullable[String] and
    (JsPath \ "returnFlag").readNullable[Boolean] and
    (JsPath \ "paymentReference").readNullable[String] and
    (JsPath \ "paymentAmount").readNullable[BigDecimal] and
    (JsPath \ "paymentMethod").readNullable[String] and
    (JsPath \ "paymentLot").readNullable[String] and
    (JsPath \ "paymentLotItem").readNullable[String] and
    (JsPath \ "clearingSAPDocument").readNullable[String] and
    (JsPath \ "statisticalDocument").readNullable[String] and
    (JsPath \ "DDCollectionInProgress").readNullable[Boolean] and
    (JsPath \ "returnReason").readNullable[String] and
    (JsPath \ "promisetoPay").readNullable[String]
  )(SubItem.apply _)

  implicit val writes: Writes[SubItem] = (
    (JsPath \ "subItem").writeNullable[String] and
    (JsPath \ "dueDate").writeNullable[LocalDate] and
    (JsPath \ "amount").writeNullable[BigDecimal] and
    (JsPath \ "clearingDate").writeNullable[LocalDate] and
    (JsPath \ "clearingReason").writeNullable[String] and
    (JsPath \ "outgoingPaymentMethod").writeNullable[String] and
    (JsPath \ "paymentLock").writeNullable[String] and
    (JsPath \ "clearingLock").writeNullable[String] and
    (JsPath \ "interestLock").writeNullable[String] and
    (JsPath \ "dunningLock").writeNullable[String] and
    (JsPath \ "returnFlag").writeNullable[Boolean] and
    (JsPath \ "paymentReference").writeNullable[String] and
    (JsPath \ "paymentAmount").writeNullable[BigDecimal] and
    (JsPath \ "paymentMethod").writeNullable[String] and
    (JsPath \ "paymentLot").writeNullable[String] and
    (JsPath \ "paymentLotItem").writeNullable[String] and
    (JsPath \ "clearingSAPDocument").writeNullable[String] and
    (JsPath \ "statisticalDocument").writeNullable[String] and
    (JsPath \ "DDcollectionInProgress").writeNullable[Boolean] and
    (JsPath \ "returnReason").writeNullable[String] and
    (JsPath \ "promiseToPay").writeNullable[String]
  )(unlift(SubItem.unapply))
}
