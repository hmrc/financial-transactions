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

import config.AppConfig
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json._

case class FinancialTransactions(documentDetails: Seq[DocumentDetails],
                                 financialDetails: Seq[Transaction])

object FinancialTransactions {
  implicit val reads: Reads[FinancialTransactions] = (
    (JsPath \ "documentDetails").read[Seq[DocumentDetails]] and
    (JsPath \ "financialDetails").read[Seq[Transaction]]
  )(FinancialTransactions.apply _)

  implicit def writes(implicit appConfig: AppConfig): Writes[FinancialTransactions] = (
    (JsPath \ "documentDetails").write[Seq[DocumentDetails]] and
    (JsPath \ "financialTransactions").write[Seq[Transaction]]
  )(unlift(FinancialTransactions.unapply))
}
