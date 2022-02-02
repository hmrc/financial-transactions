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

import play.api.libs.json.{Format, Json}

case class Transaction(chargeType: Option[String] = None,
                       mainType: Option[String] = None,
                       periodKey: Option[String] = None,
                       periodKeyDescription: Option[String] = None,
                       taxPeriodFrom: Option[LocalDate] = None,
                       taxPeriodTo: Option[LocalDate] = None,
                       businessPartner: Option[String] = None,
                       contractAccountCategory: Option[String] = None,
                       contractAccount: Option[String] = None,
                       contractObjectType: Option[String] = None,
                       contractObject: Option[String] = None,
                       sapDocumentNumber: Option[String] = None,
                       sapDocumentNumberItem: Option[String] = None,
                       chargeReference: Option[String] = None,
                       mainTransaction: Option[String] = None,
                       subTransaction: Option[String] = None,
                       originalAmount: Option[BigDecimal] = None,
                       outstandingAmount: Option[BigDecimal] = None,
                       clearedAmount: Option[BigDecimal] = None,
                       accruedInterest: Option[BigDecimal] = None,
                       items: Option[Seq[SubItem]] = None)

object Transaction {
  implicit val format: Format[Transaction] = Json.format[Transaction]
}
