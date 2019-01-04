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

import java.time.LocalDate

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
                   returnReason: Option[String] = None,
                   promiseToPay: Option[String] = None)

object SubItem {
  implicit val format: Format[SubItem] = Json.format[SubItem]
}