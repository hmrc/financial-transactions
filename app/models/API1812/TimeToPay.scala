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

package models.API1812

import play.api.libs.json.{Json, Reads}

import java.time.LocalDate

case class TimeToPay(TTPStartDate: Option[LocalDate],
                     TTPEndDate: Option[LocalDate])

object TimeToPay {
  implicit val reads: Reads[TimeToPay] = Json.reads[TimeToPay]
}

case class HipTimeToPay(ttpStartDate: Option[LocalDate],
                        ttpEndDate: Option[LocalDate])

object HipTimeToPay {
  implicit val reads: Reads[HipTimeToPay] = Json.reads[HipTimeToPay]
}