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

package utils.API1811

import models.API1811._
import play.api.libs.json.{JsObject, Json}

object TestConstantsHIP {

  val businessErrorJson : JsObject = Json.obj(
    "processingDate" -> "2024-05-10",
    "code" -> "BUS_ERR_001",
    "text" -> "Business error occurred"
  )

  val businessErrorModel: BusinessError = BusinessError(
    processingDate = "2024-05-10",
    code = "BUS_ERR_001",
    text = "Business error occurred"
  )

  val technicalErrorJson: JsObject = Json.obj(
    "code" -> "TECH_ERR_500",
    "message" -> "A technical error occurred",
    "logId" -> "log-12345"
  )

  val technicalErrorModel: TechnicalError = TechnicalError(
    code = "TECH_ERR_500",
    message = "A technical error occurred",
    logId = "log-12345"
  )

  val fullFinancialTransactionsHIPJson: JsObject = fullFTJson(TestConstants.fullDocumentDetailsJson)

  def fullFTJson(documentDetails: JsObject): JsObject = Json.obj(
    "success" -> Json.obj(
      "processingDate" -> "2024-05-10",
      "financialData" -> Json.obj(
        "documentDetails" -> Json.arr(documentDetails)
      )
    )
  )

  val fullFinancialTransactionsHIP: FinancialTransactionsHIP = FinancialTransactionsHIP(
    processingDate = "2024-05-10",
    financialData = FinancialTransactions(
      documentDetails = Seq(TestConstants.fullDocumentDetails)
    )
  )
}