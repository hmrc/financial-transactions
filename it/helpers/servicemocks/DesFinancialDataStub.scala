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

package helpers.servicemocks

import binders.FinancialTransactionsBinders
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WiremockHelper._
import models.{FinancialDataQueryParameters, TaxRegime}
import play.api.libs.json.JsValue

object DesFinancialDataStub {

  private def financialDataUrl(regime: TaxRegime, queryParameters: FinancialDataQueryParameters): String = {
    if (queryParameters.hasQueryParameters) {
      s"/enterprise/financial-data/${regime.idType}/${regime.id}/${regime.regimeType}" +
        s"?${FinancialTransactionsBinders.financialDataQueryBinder.unbind("", queryParameters)}"
    }
    else {
      s"/enterprise/financial-data/${regime.idType}/${regime.id}/${regime.regimeType}"
    }
  }

  def stubGetFinancialData(regime: TaxRegime, queryParams: FinancialDataQueryParameters)(status: Int, response: JsValue): StubMapping =
    stubGet(financialDataUrl(regime, queryParams), status, response.toString())

  def verifyGetDesBusinessDetails(regime: TaxRegime, queryParams: FinancialDataQueryParameters): Unit =
    verifyGet(financialDataUrl(regime, queryParams))

}
