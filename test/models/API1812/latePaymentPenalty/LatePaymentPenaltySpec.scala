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

package models.API1812.latePaymentPenalty

import base.SpecBase
import play.api.libs.json.Json
import utils.TestConstantsAPI1812._

class LatePaymentPenaltySpec extends SpecBase {

  "LatePaymentPenalty" should {

    "parse from JSON" when {

      "optional fields are present" in {
        LPPJsonMax.as[LatePaymentPenalty] shouldBe LPPModelMax
      }

      "optional fields are missing" in {
        LPPJsonMin.as[LatePaymentPenalty] shouldBe LPPModelMin
      }
    }

    "serialize to JSON" when {

      "optional fields are present" in {
        Json.toJson(LPPModelMax) shouldBe LPPJsonMaxWritten
      }

      "optional fields are missing" in {
        Json.toJson(LPPJsonMin) shouldBe LPPJsonMinWritten
      }
    }
  }

  "hasTimeToPay" should {

    "return true" when {

      "user is well within time to pay" in {
        LPPModelMin.copy(timeToPay = inTTP).hasTimeToPay shouldBe true
      }

      "user is on the first day of time to pay" in {
        LPPModelMin.copy(timeToPay = firstDayTTP).hasTimeToPay shouldBe true
      }

      "user is on the last day of time to pay" in {
        LPPModelMin.copy(timeToPay = lastDayTTP).hasTimeToPay shouldBe true
      }

      "user is in the first of 2 time to pay periods" in {
        LPPModelMin.copy(timeToPay = firstOfTwoTTPS).hasTimeToPay shouldBe true
      }

    }

    "return false" when {

      "user's time to pay ended yesterday" in {
        LPPModelMin.copy(timeToPay = TTPEndYesterday).hasTimeToPay shouldBe false
      }

      "user's time to pay will begin tomorrow" in {
        LPPModelMin.copy(timeToPay = TTPBeginTomorrow).hasTimeToPay shouldBe false
      }

      "user has been out of time to pay for some time" in {
        LPPModelMin.copy(timeToPay = outOfTTP).hasTimeToPay shouldBe false
      }

      "user is due to go into time to pay in the future" in {
        LPPModelMin.copy(timeToPay = futureTTP).hasTimeToPay shouldBe false
      }

      "user has no time to pay data" in {
        LPPModelMin.hasTimeToPay shouldBe false
      }

      "user is between 2 time to pay periods" in {
        LPPModelMin.copy(timeToPay = betweenTTPS).hasTimeToPay shouldBe false
      }
    }
  }
}
