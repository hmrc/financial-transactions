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
import play.api.libs.json._
import services.DateService

case class FinancialTransactions(documentDetails: Seq[DocumentDetails])

object FinancialTransactions {

  def hasOverdueChargeAndNoTTP(documentDetails: Seq[DocumentDetails])(implicit appConfig: AppConfig): Boolean = {
    val currentDate = DateService.now
    val hasOverdueCharge =
      documentDetails.exists(_.lineItemDetails.exists(_.netDueDate.fold(false)(_.isBefore(currentDate))))
    val hasTTPLock =
      documentDetails.exists(_.lineItemDetails.exists(_.lineItemLockDetails.exists(_.lockType.contains("Collected via TTP"))))

    (hasOverdueCharge, hasTTPLock) match {
      case (true, false) => true
      case _ => false
    }
  }

  implicit val reads: Reads[FinancialTransactions] =
    (__ \ "getFinancialData" \ "financialDetails" \ "documentDetails").read[Seq[DocumentDetails]]
      .map(FinancialTransactions.apply)

  implicit def writes(implicit appConfig: AppConfig): Writes[FinancialTransactions] = Writes { model =>
    Json.obj(
      "financialTransactions" -> Json.toJsFieldJsValueWrapper(model.documentDetails),
      "hasOverdueChargeAndNoTTP" -> hasOverdueChargeAndNoTTP(model.documentDetails)
    )
  }
}
