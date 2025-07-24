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

package models

import base.SpecBase
import models.PenaltyDetailsQueryParameters.dateLimitKey

class PenaltyDetailsQueryParametersSpec extends SpecBase {

  "The PenaltyDetailsQueryParameters object" should {

    "have the correct key value for 'dateLimit'" in {
      dateLimitKey shouldBe "dateLimit"
    }
  }

  "The PenaltyDetailsQueryParameters.toSeqQueryParams method" should {

    "output the expected sequence of key-value pairs" which {

      "for no Query Parameters, has no values" in {
        val queryParams = PenaltyDetailsQueryParameters()
        queryParams.toSeqQueryParams shouldBe Seq()
      }

      "for the 'dateLimit' query param, has the expected key/value" in {
        val queryParams = PenaltyDetailsQueryParameters(dateLimit = Some("01"))
        queryParams.toSeqQueryParams shouldBe Seq(dateLimitKey -> "01")
      }
    }
  }
}
