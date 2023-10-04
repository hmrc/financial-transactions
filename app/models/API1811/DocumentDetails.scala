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

import config.AppConfig
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsNull, JsObject, JsPath, JsResultException, Json, JsonValidationError, Reads, Writes}
import play.api.mvc.Request
import utils.API1811.ChargeTypes

case class DocumentDetails(chargeReferenceNumber: Option[String],
                           documentTotalAmount: Option[BigDecimal],
                           documentOutstandingAmount: Option[BigDecimal],
                           documentClearedAmount: Option[BigDecimal],
                           lineItemDetails: Seq[LineItemDetails],
                           interestAccruingAmount: Option[BigDecimal],
                           documentPenaltyTotals: Option[Seq[DocumentPenaltyTotals]]) {

  def getAccruingPenalty(implicit appConfig: AppConfig): Option[DocumentPenaltyTotals] = {
    val dueDate = lineItemDetails.headOption.flatMap(_.netDueDate)
    (documentPenaltyTotals, dueDate) match {
      case (Some(totals), Some(due)) if due.isBefore(appConfig.familiarisationPeriodEndDate) =>
        totals.find(pen => pen.penaltyType.contains("LPP2") && pen.penaltyStatus.contains("ACCRUING"))
      case (Some(totals), Some(_)) =>
        totals.find(_.penaltyStatus.contains("ACCRUING"))
      case _ => None
    }
  }
}

object DocumentDetails {

  implicit val reads: Reads[DocumentDetails] = (
    (JsPath \ "chargeReferenceNumber").readNullable[String] and
    (JsPath \ "documentTotalAmount").readNullable[BigDecimal] and
    (JsPath \ "documentOutstandingAmount").readNullable[BigDecimal] and
    (JsPath \ "documentClearedAmount").readNullable[BigDecimal] and
    (JsPath \ "lineItemDetails").read[Seq[LineItemDetails]] and
    (JsPath \ "documentInterestTotals" \ "interestAccruingAmount").readNullable[BigDecimal] and
    (JsPath \ "documentPenaltyTotals").readNullable[Seq[DocumentPenaltyTotals]]
  )(DocumentDetails.apply _)

  implicit def writes(implicit appConfig: AppConfig, request: Request[_]): Writes[DocumentDetails] = Writes { model =>
    if (model.lineItemDetails.nonEmpty) {
      JsObject(Json.obj(
        "chargeType" -> ChargeTypes.retrieveChargeType(
          model.lineItemDetails.head.mainTransaction, model.lineItemDetails.head.subTransaction
        ),
        "periodKey" -> model.lineItemDetails.head.periodKey,
        "taxPeriodFrom" -> model.lineItemDetails.head.periodFromDate,
        "taxPeriodTo" -> model.lineItemDetails.head.periodToDate,
        "chargeReference" -> model.chargeReferenceNumber,
        "mainTransaction" -> model.lineItemDetails.head.mainTransaction,
        "subTransaction" -> model.lineItemDetails.head.subTransaction,
        "originalAmount" -> model.documentTotalAmount,
        "outstandingAmount" -> model.documentOutstandingAmount,
        "clearedAmount" -> model.documentClearedAmount,
        "items" -> model.lineItemDetails,
        "accruingInterestAmount" -> model.interestAccruingAmount,
        "accruingPenaltyAmount" -> model.getAccruingPenalty.map(_.penaltyAmount),
        "penaltyType" -> model.getAccruingPenalty.map(_.penaltyType)
      ).fields.filterNot(_._2 == JsNull))
    } else {
      throw JsResultException(Seq(
        (JsPath \ "lineItemDetails") -> Seq(JsonValidationError("Line item details must contain at least 1 item"))
      ))
    }
  }
}
