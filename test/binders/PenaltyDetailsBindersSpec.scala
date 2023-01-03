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

package binders

import base.SpecBase
import models.PenaltyDetailsQueryParameters
import models.PenaltyDetailsQueryParameters.dateLimitKey

class PenaltyDetailsBindersSpec extends SpecBase {

  "The PenaltyDetailsBinders.penaltyDetailsQueryBinder.bind method" when {

    "no query parameters are passed" should {

      "return an empty PenaltyDetailsQueryParameters instance" in {
        val queryParams: Map[String, Seq[String]] = Map("" -> Seq())
        val expected = Some(Right(PenaltyDetailsQueryParameters()))
        val actual = PenaltyDetailsBinders.penaltyDetailsQueryBinder.bind("", queryParams)

        actual shouldBe expected
      }
    }

    "a dateLimit query parameter is passed" should {

      "return a PenaltyDetailsQueryParameters instance with correct parameters" when {

        "the value is 0 (lower boundary)" in {
          val queryParams: Map[String, Seq[String]] = Map(dateLimitKey -> Seq("0"))
          val expected = Some(Right(PenaltyDetailsQueryParameters(Some(0))))
          val actual = PenaltyDetailsBinders.penaltyDetailsQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }

        "the value is 99 (upper boundary)" in {
          val queryParams: Map[String, Seq[String]] = Map(dateLimitKey -> Seq("99"))
          val expected = Some(Right(PenaltyDetailsQueryParameters(Some(99))))
          val actual = PenaltyDetailsBinders.penaltyDetailsQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }

        "the value is between 0 and 99" in {
          val queryParams: Map[String, Seq[String]] = Map(dateLimitKey -> Seq("47"))
          val expected = Some(Right(PenaltyDetailsQueryParameters(Some(47))))
          val actual = PenaltyDetailsBinders.penaltyDetailsQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }

      "return an error message with details of the error" when {

        "the value is negative" in {
          val queryParams: Map[String, Seq[String]] = Map(dateLimitKey -> Seq("-1"))
          val expected = Some(Left(s"Failed to bind '$dateLimitKey=-1' valid values are between 0 and 99 inclusive."))
          val actual = PenaltyDetailsBinders.penaltyDetailsQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }

        "the value is greater than 99" in {
          val queryParams: Map[String, Seq[String]] = Map(dateLimitKey -> Seq("100"))
          val expected = Some(Left(s"Failed to bind '$dateLimitKey=100' valid values are between 0 and 99 inclusive."))
          val actual = PenaltyDetailsBinders.penaltyDetailsQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }

        "the value is not numeric" in {
          val queryParams: Map[String, Seq[String]] = Map(dateLimitKey -> Seq("hello"))
          val expected = Some(Left(s"Failed to bind '$dateLimitKey=hello' valid values are between 0 and 99 inclusive."))
          val actual = PenaltyDetailsBinders.penaltyDetailsQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }
    }

    "if an unrecognised query parameter is passed" should {

      "ignore it and return an empty PenaltyDetailsQueryParameters instance" in {
        val queryParams: Map[String, Seq[String]] = Map("unrecognisedParam" -> Seq("2018-01-01"))
        val expected = Some(Right(PenaltyDetailsQueryParameters()))
        val actual = PenaltyDetailsBinders.penaltyDetailsQueryBinder.bind("", queryParams)

        actual shouldBe expected
      }
    }
  }

  "The PenaltyDetailsBinders.penaltyDetailsQueryBinder.unbind method" should {

    "unbind query parameters correctly" in {
      val queryParams = PenaltyDetailsQueryParameters(Some(1))
      val result = PenaltyDetailsBinders.penaltyDetailsQueryBinder.unbind("", queryParams)

      result shouldBe "dateLimit=1"
    }
  }
}
