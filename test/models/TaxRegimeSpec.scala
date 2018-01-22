/*
 * Copyright 2017 HM Revenue & Customs
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
import play.api.libs.json.{JsValue, Json}

class TaxRegimeSpec extends SpecBase {

  "The IncomeTaxRegime" should {

    val incomeTaxRegime = IncomeTaxRegime("AA111111A")

    "have the correct regimeId type of 'NINO'" in {
      incomeTaxRegime.idType shouldBe "NINO"
    }

    "have the correct regimeId value of 'AA111111A'" in {
      incomeTaxRegime.id shouldBe "AA111111A"
    }

    "have the correct regimeType of 'ITSA'" in {
      incomeTaxRegime.regimeType shouldBe "ITSA"
    }
  }

  "The VatRegime" should {

    val vatRegime = VatRegime("12345678")

    "have the correct regimeId type of 'VRN'" in {
      vatRegime.idType shouldBe "VRN"
    }

    "have the correct regimeId value of '12345678'" in {
      vatRegime.id shouldBe "12345678"
    }

    "have the correct regimeType of 'VATC'" in {
      vatRegime.regimeType shouldBe "VATC"
    }
  }
}
