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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class FinancialTransactions(idType: String,
                                 idNumber: String,
                                 regimeType: String,
                                 processingDate: LocalDateTime,
                                 financialTransactions: List[Transaction])

object FinancialTransactions {
  implicit val reads: Reads[FinancialTransactions] = Json.reads[FinancialTransactions]
  implicit val writes: Writes[FinancialTransactions] = (
    (__ \ "idType").write[String] and
      (__ \ "idNumber").write[String] and
      (__ \ "regimeType").write[String] and
      (__ \ "processingDate").write[LocalDateTime](LocalDateTimeDes.Writes) and
      (__ \ "financialTransactions").write[List[Transaction]]
    )(unlift(FinancialTransactions.unapply))
}

object LocalDateTimeDes {
  implicit object Writes extends Writes[LocalDateTime] {
    override def writes(dateTime: LocalDateTime): JsValue = JsString(dateTime format DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
  }
}