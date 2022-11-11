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

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._

case class LineItemDetails(mainTransaction: Option[String],
                           subTransaction: Option[String],
                           periodFromDate: Option[LocalDate],
                           periodToDate: Option[LocalDate],
                           periodKey: Option[String],
                           netDueDate: Option[LocalDate],
                           amount: Option[BigDecimal],
                           ddCollectionInProgress: Option[Boolean],
                           clearingDate: Option[LocalDate],
                           clearingReason: Option[String],
                           clearingDocument: Option[String],
                           interestRate: Option[BigDecimal])

object LineItemDetails {

  implicit val reads: Reads[LineItemDetails] = (
    (JsPath \ "mainTransaction").readNullable[String] and
    (JsPath \ "subTransaction").readNullable[String] and
    (JsPath \ "periodFromDate").readNullable[LocalDate] and
    (JsPath \ "periodToDate").readNullable[LocalDate] and
    (JsPath \ "periodKey").readNullable[String] and
    (JsPath \ "netDueDate").readNullable[LocalDate] and
    (JsPath \ "amount").readNullable[BigDecimal] and
    (JsPath \ "ddCollectionInProgress").readNullable[Boolean] and
    (JsPath \ "clearingDate").readNullable[LocalDate] and
    (JsPath \ "clearingReason").readNullable[String] and
    (JsPath \ "clearingDocument").readNullable[String] and
    (JsPath \ "lineItemInterestDetails" \"currentInterestRate").readNullable[BigDecimal]
  ) (LineItemDetails.apply _)

  implicit val writes: Writes[LineItemDetails] = Writes { model =>
    JsObject(Json.obj(
      "dueDate" -> model.netDueDate,
      "amount" -> model.amount,
      "clearingDate" -> model.clearingDate,
      "clearingReason" -> model.clearingReason,
      "clearingSAPDocument" -> model.clearingDocument,
      "DDcollectionInProgress" -> model.ddCollectionInProgress
    ).fields.filterNot(_._2 == JsNull))
  }
}
