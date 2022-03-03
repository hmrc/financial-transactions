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

case class DocumentDetails(
                            taxYear: String,
                            documentId: String,
                            documentDate: LocalDate,
                            documentText: String,
                            documentDueDate: LocalDate,
                            totalAmount: BigDecimal,
                            documentOutstandingAmount: BigDecimal,
                            accruingPenaltyLPP1: Option[String],
                            lpp1Amount: Option[String],
                            lpp1ID: Option[String],
                            lpp2Amount: Option[String],
                            accruingPenaltyLPP2: Option[String],
                            lpp2ID: Option[String]
                          )

object DocumentDetails {
  implicit val format: Format[DocumentDetails] = Json.format[DocumentDetails]
}
