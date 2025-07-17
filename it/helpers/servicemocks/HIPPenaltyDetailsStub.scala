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

package helpers.servicemocks

import binders.PenaltyDetailsBinders
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WiremockHelper.stubGet
import models.{PenaltyDetailsQueryParameters, TaxRegime}
import play.api.libs.json.JsValue

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
} 