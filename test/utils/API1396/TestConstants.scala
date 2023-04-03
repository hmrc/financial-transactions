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

package utils.API1396

import models.API1396.{DirectDebitDetail, DirectDebits}
import play.api.libs.json.{JsObject, Json}

object TestConstants {

  val fullDirectDebitDetail: DirectDebitDetail = DirectDebitDetail(
    directDebitInstructionNumber = "000000001234567898",
    directDebitPlanType = "VPP",
    dateCreated = "2018-04-08",
    accountHolderName = "A PERSON",
    sortCode = "0000000",
    accountNumber = "000000001"
  )

  val fullDirectDebitDetailJson: JsObject = Json.obj(
    "directDebitInstructionNumber" -> "000000001234567898",
    "directDebitPlanType" -> "VPP",
    "dateCreated" -> "2018-04-08",
    "accountHolderName" -> "A PERSON",
    "sortCode" -> "0000000",
    "accountNumber" -> "000000001"
  )

  val multipleDirectDebits: DirectDebits = DirectDebits(
    directDebitMandateFound = true,
    directDebitDetails = Some(Seq(fullDirectDebitDetail, fullDirectDebitDetail))
  )

  val multipleDirectDebitsJson: JsObject = Json.obj(
    "directDebitMandateFound" -> true,
    "directDebitDetails" -> Json.arr(fullDirectDebitDetailJson, fullDirectDebitDetailJson)
  )

  val singleDirectDebits: DirectDebits = DirectDebits(
    directDebitMandateFound = true,
    directDebitDetails = Some(Seq(fullDirectDebitDetail))
  )

  val singleDirectDebitsJson: JsObject = Json.obj(
    "directDebitMandateFound" -> true,
    "directDebitDetails" -> Json.arr(fullDirectDebitDetailJson)
  )

  val noDirectDebits: DirectDebits = DirectDebits(
    directDebitMandateFound = false
  )

  val noDirectDebitsJson: JsObject = Json.obj(
    "directDebitMandateFound" -> false
  )
}
