/*
 * Copyright 2021 HM Revenue & Customs
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

import play.api.libs.json._


case class DirectDebitDetail(
                              directDebitInstructionNumber: String,
                              directDebitPlanType: String,
                              dateCreated: String,
                              accountHolderName: String,
                              sortCode: String,
                              accountNumber: String

                            )

object DirectDebitDetail {
  implicit val format: Format[DirectDebitDetail] = Json.format[DirectDebitDetail]
}

case class DirectDebits(directDebitMandateFound: Boolean,
                        directDebitDetails: Option[Seq[DirectDebitDetail]] = None)

object DirectDebits {
  implicit val format: Format[DirectDebits] = Json.format[DirectDebits]
}