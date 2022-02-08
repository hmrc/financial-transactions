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

package binders

import base.SpecBase
import models.RequestQueryParameters._
import models.RequestQueryParameters
import utils.ImplicitDateFormatter._

class FinancialTransactionsBindersSpec extends SpecBase {

  "The FinancialTransactionsBinder.financialDataQueryBinder.bind method" should {

    "if no QueryParameters are passed" should {

      val queryParams: Map[String, Seq[String]] = Map("" -> Seq())

      "return an empty RequestQueryParameters instance" in {

        val expected = Some(Right(RequestQueryParameters()))
        val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

        actual shouldBe expected
      }

    }

    "if a dateFrom query parameter is passed" which {

      "is formatted correctly" should {

        val queryParams: Map[String, Seq[String]] = Map(dateFromKey -> Seq("2018-01-01"))

        "return an RequestQueryParameters instance with correct parameters" in {

          val expected = Some(Right(RequestQueryParameters(Some("2018-01-01"))))
          val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }

      "is formatted incorrectly" should {

        val queryParams: Map[String, Seq[String]] = Map(dateFromKey -> Seq("banana"))

        "return a bad request error message with details of the error" in {

          val expected = Some(Left(s"Failed to bind '$dateFromKey=banana' valid date format should be 'YYYY-MM-DD'."))
          val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }

    }

    "if a dateTo query parameter is passed" which {

      "is formatted correctly" should {

        val queryParams: Map[String, Seq[String]] = Map(dateToKey -> Seq("2018-01-01"))

        "return an RequestQueryParameters instance with correct parameters" in {

          val expected = Some(Right(RequestQueryParameters(None, Some("2018-01-01"))))
          val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }

      "is formatted incorrectly" should {

        val queryParams: Map[String, Seq[String]] = Map(dateToKey -> Seq("banana"))

        "return a bad request error message with details of the error" in {

          val expected = Some(Left(s"Failed to bind '$dateToKey=banana' valid date format should be 'YYYY-MM-DD'."))
          val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }

    }

    "if a onlyOpenItems query parameter is passed" which {

      "is formatted correctly" should {

        val queryParams: Map[String, Seq[String]] = Map(onlyOpenItemsKey -> Seq("true"))

        "return an RequestQueryParameters instance with correct parameters" in {

          val expected = Some(Right(RequestQueryParameters(None, None, Some(true))))
          val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }

      "is formatted incorrectly" should {

        val queryParams: Map[String, Seq[String]] = Map(onlyOpenItemsKey -> Seq("banana"))

        "return a bad request error message with details of the error" in {

          val expected = Some(Left(s"Failed to bind '$onlyOpenItemsKey=banana' valid values are 'true' or 'false'."))
          val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }
    }

    "if all query parameters are passed" which {

      "are formatted correctly" should {

        val queryParams: Map[String, Seq[String]] = Map(
          dateFromKey -> Seq("2018-01-01"),
          dateToKey -> Seq("2018-01-01"),
          onlyOpenItemsKey -> Seq("true")
        )

        "return an RequestQueryParameters instance with correct parameters" in {

          val expected = Some(Right(RequestQueryParameters(
            fromDate = Some("2018-01-01"),
            toDate = Some("2018-01-01"),
            onlyOpenItems = Some(true)
          )))

          val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }

      "if all are incorrectly formatted" should {

        val queryParams: Map[String, Seq[String]] = Map(
          dateFromKey -> Seq("banana"),
          dateToKey -> Seq("banana2"),
          onlyOpenItemsKey -> Seq("banana3")
        )

        "return a bad request error message with details of the error" in {

          val expected = Some(Left(
            s"Failed to bind '$dateFromKey=banana' valid date format should be 'YYYY-MM-DD'., " +
              s"Failed to bind '$dateToKey=banana2' valid date format should be 'YYYY-MM-DD'., " +
              s"Failed to bind '$onlyOpenItemsKey=banana3' valid values are 'true' or 'false'."
          ))

          val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }
    }

    "if an unrecognised query parameter is passed" should {

      val queryParams: Map[String, Seq[String]] = Map("unrecognisedDateParam" -> Seq("2018-01-01"))

      "ignore it and return an empty RequestQueryParameters instance" in {

        val expected = Some(Right(RequestQueryParameters()))
        val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

        actual shouldBe expected
      }
    }
  }
}
