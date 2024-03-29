/*
 * Copyright 2023 HM Revenue & Customs
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

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._

case class DocumentPenaltyTotals(penaltyType:Option[String],
                                 penaltyStatus: Option[String],
                                 penaltyAmount: Option[BigDecimal])

object DocumentPenaltyTotals {

  implicit val reads: Reads[DocumentPenaltyTotals] = (
    (JsPath \ "penaltyType").readNullable[String] and
    (JsPath \ "penaltyStatus").readNullable[String] and
    (JsPath \ "penaltyAmount").readNullable[BigDecimal]
  ) (DocumentPenaltyTotals.apply _)
}
