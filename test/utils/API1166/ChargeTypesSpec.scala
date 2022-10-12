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

package utils

import base.SpecBase
import utils.API1166.ChargeTypes
import utils.API1166.ChargeTypes.{establishedChargeTypes, penaltiesAndInterestChargeTypes}

class ChargeTypesSpec extends SpecBase {

  "The validChargeTypes function" when {

    "the includePenAndIntCharges feature switch is off" should {

      "exclude the penalties and interest charge types involved with the penalty reform work package" in {
        mockAppConfig.features.includePenAndIntCharges(false)
        ChargeTypes.validChargeTypes(mockAppConfig) shouldBe establishedChargeTypes
      }
    }

    "the includePenAndIntCharges feature switch is on" should {

      "include the penalties and interest charge types involved with the penalty reform work package" in {
        mockAppConfig.features.includePenAndIntCharges(true)
        ChargeTypes.validChargeTypes(mockAppConfig) shouldBe establishedChargeTypes ++ penaltiesAndInterestChargeTypes
      }
    }
  }
}
