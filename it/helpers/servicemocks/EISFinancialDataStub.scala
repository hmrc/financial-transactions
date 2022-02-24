/*
 * Copyright 2019 HM Revenue & Customs
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

import binders.FinancialTransactionsBinders
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WiremockHelper.stubGet
import models.{FinancialRequestQueryParameters, TaxRegime}
import play.api.libs.json.JsValue

object EISFinancialDataStub {

  private def financialDataUrl(regime: TaxRegime, requestQueryParams: FinancialRequestQueryParameters): String =
    if(requestQueryParams.hasQueryParameters) {
      s"/penalty/financial-data/${regime.idType}/${regime.id}/${regime.regimeType}" +
        s"?${FinancialTransactionsBinders.financialDataQueryBinder.unbind("", requestQueryParams)}" +
        "&includeLocks=true&calculateAccruedInterest=true&removePOA=true&customerPaymentInformation=true"
    } else {
      s"/penalty/financial-data/${regime.idType}/${regime.id}/${regime.regimeType}" +
        "?onlyOpenItems=false&includeLocks=true&calculateAccruedInterest=true&removePOA=true&customerPaymentInformation=true"
    }

  def stubGetFinancialData(regime: TaxRegime, queryParams: FinancialRequestQueryParameters)
                          (status: Int, response: JsValue): StubMapping =
    stubGet(financialDataUrl(regime, queryParams), status, response.toString())
}
