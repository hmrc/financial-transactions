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

package connectors.API1811

import base.SpecBase
import mocks.MockHttp
import models.{FinancialRequestQueryParameters, VatRegime}
import models.API1811.Error
import play.api.http.Status.BAD_GATEWAY
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.RequestTimeoutException

import scala.concurrent.Future

class FinancialDataConnectorSpec extends SpecBase with MockHttp {

  val connector = new FinancialDataConnector(mockHttpGet)
  val vatRegime: VatRegime = VatRegime("555555555")
  val queryParams: FinancialRequestQueryParameters = FinancialRequestQueryParameters()

  "The FinancialDataConnector" should {

    "return a 502 error when there is a HTTP exception" in {
      val exception = new RequestTimeoutException("Request timed out!!!")
      setupMockHttpGet(connector.financialDataUrl(vatRegime), queryParams.queryParams1811)(Future.failed(exception))
      val result = connector.getFinancialData(vatRegime, queryParams)
      await(result) shouldBe Left(Error(BAD_GATEWAY, exception.message))
    }
  }
}
