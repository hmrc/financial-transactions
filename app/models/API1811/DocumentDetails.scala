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

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}

case class DocumentDetails(chargeReferenceNumber: Option[String],
                           documentTotalAmount: Option[BigDecimal],
                           documentOutstandingAmount: Option[BigDecimal],
                           lineItemDetails: Seq[LineItemDetails],
                           interestAccruingAmount: Option[BigDecimal],
                           penaltyType: Option[String],
                           penaltyStatus: Option[String],
                           penaltyAmount: Option[BigDecimal])

object DocumentDetails {

  val penaltyPath = "documentPenaltyTotals"

  implicit val reads: Reads[DocumentDetails] = (
    (JsPath \ "chargeReferenceNumber").readNullable[String] and
    (JsPath \ "documentTotalAmount").readNullable[BigDecimal] and
    (JsPath \ "documentOutstandingAmount").readNullable[BigDecimal] and
    (JsPath \ "lineItemDetails").read[Seq[LineItemDetails]] and
    (JsPath \ "documentInterestTotals" \ "interestAccruingAmount").readNullable[BigDecimal] and
    (JsPath \ penaltyPath)(0).\("penaltyType").readNullable[String] and
    (JsPath \ penaltyPath)(0).\("penaltyStatus").readNullable[String] and
    (JsPath \ penaltyPath)(0).\("penaltyAmount").readNullable[BigDecimal]
  )(DocumentDetails.apply _)

}
