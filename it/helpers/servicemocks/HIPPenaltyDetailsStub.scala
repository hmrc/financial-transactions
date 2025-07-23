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

import binders.PenaltyDetailsBinders
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WiremockHelper.stubGet
import models.{PenaltyDetailsQueryParameters, TaxRegime}
import play.api.libs.json.{JsValue, Json}

object HIPPenaltyDetailsStub {

  private def penaltyDetailsUrl(regime: TaxRegime, requestQueryParams: PenaltyDetailsQueryParameters): String = {
    val baseUrl = "/etmp/RESTAdapter/cross-regime/taxpayer/penalties"
    val regimeParams = s"taxRegime=${regime.regimeType}&idType=${regime.idType}&idNumber=${regime.id}"
    
    if(requestQueryParams.hasQueryParameters) {
      val additionalParams = PenaltyDetailsBinders.penaltyDetailsQueryBinder.unbind("", requestQueryParams)
      s"$baseUrl?$regimeParams&$additionalParams"
    } else {
      s"$baseUrl?$regimeParams"
    }
  }

  def stubGetPenaltyDetails(regime: TaxRegime, queryParams: PenaltyDetailsQueryParameters)
                          (status: Int, response: JsValue): StubMapping =
    stubGet(penaltyDetailsUrl(regime, queryParams), status, response.toString())

  def stubGetPenaltyDetailsWithHeaderValidation(regime: TaxRegime, queryParams: PenaltyDetailsQueryParameters)
                                              (status: Int, response: JsValue): StubMapping = {
    stubFor(get(urlEqualTo(penaltyDetailsUrl(regime, queryParams)))
      .withHeader("Authorization", matching("Bearer .*"))
      .withHeader("correlationid", matching("^[0-9a-fA-F]{8}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{12}$"))
      .withHeader("X-Originating-System", matching(".*"))
      .withHeader("X-Receipt-Date", matching("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z$"))
      .withHeader("X-Transmitting-System", matching(".*"))
      .willReturn(aResponse()
        .withStatus(status)
        .withHeader("correlationid", "12345678-1234-1234-1234-123456789012")
        .withHeader("Content-Type", "application/json")
        .withBody(response.toString())
      )
    )
  }

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

  def stubGetPenaltyDetailsEmptyBody(regime: TaxRegime, queryParams: PenaltyDetailsQueryParameters)
                                   (status: Int): StubMapping = {
    stubFor(get(urlEqualTo(penaltyDetailsUrl(regime, queryParams)))
      .willReturn(aResponse()
        .withStatus(status)
        .withHeader("correlationid", "12345678-1234-1234-1234-123456789012")
        .withHeader("Content-Type", "application/json")
        .withBody("")
      )
    )
  }
} 