/*
 * Copyright 2025 HM Revenue & Customs
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

package helpers.servicemocks

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WiremockHelper.stubPost
import play.api.libs.json.{JsValue, Json}

object HIPFinancialDetailsStub {

  val financialDetailsUrl = "/etmp/RESTAdapter/cross-regime/taxpayer/financial-data/query"

  def stubGetFinancialDetails(status: Int, response: JsValue): StubMapping =
    stubPost(financialDetailsUrl, status, response.toString())

  def businessErrorResponse(code: String, text: String): JsValue = Json.obj(
    "errors" -> Json.obj(
      "processingDate" -> "2025-01-31T09:30:47Z",
      "code" -> code,
      "text" -> text
    )
  )

  def technicalErrorResponse(code: String, message: String, logId: String = "C0000AB8190C333200000002000007A6"): JsValue = Json.obj(
    "error" -> Json.obj(
      "code" -> code,
      "message" -> message,
      "logID" -> logId
    )
  )

  def hipWrappedErrorResponse(errorType: String, reason: String): JsValue = Json.obj(
    "origin" -> "HIP",
    "response" -> Json.obj(
      "failures" -> Json.arr(Json.obj(
        "type" -> errorType,
        "reason" -> reason
      ))
    )
  )

  def noDataSuccessResponse(): JsValue = Json.obj(
    "success" -> Json.obj(
      "processingDate" -> "2025-01-31T09:30:47Z"
    )
  )

}
