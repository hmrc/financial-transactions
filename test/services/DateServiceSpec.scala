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

package services

import base.SpecBase

import java.time.LocalDate

class DateServiceSpec extends SpecBase {

  "The date service" when {

    "the static date feature is enabled" should {

      "return the static date specified in config" in {
        mockAppConfig.features.staticDate(true)

        val result = DateService.now(mockAppConfig)
        val expected = LocalDate.parse("2018-05-01")

        result shouldEqual expected
      }

    }

    "the static date feature is disabled" should {

      "return today's date" in {
        mockAppConfig.features.staticDate(false)

        val result = DateService.now(mockAppConfig)
        val expected = LocalDate.now()

        result shouldEqual expected
      }

    }

  }

}
