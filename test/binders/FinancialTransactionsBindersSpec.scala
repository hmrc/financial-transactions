/*
 * Copyright 2021 HM Revenue & Customs
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
import models.FinancialDataQueryParameters
import utils.ImplicitDateFormatter._
import FinancialDataQueryParameters._

class FinancialTransactionsBindersSpec extends SpecBase {

  "The FinancialTransactionsBinder.financialDataQueryBinder.bind method" should {

    "if no QueryParameters are passed" should {

      val queryParams: Map[String, Seq[String]] = Map("" -> Seq())

      "return an empty FinancialDataQueryParameters instance" in {

        val expected = Some(Right(FinancialDataQueryParameters()))
        val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

        actual shouldBe expected
      }

    }

    "if a dateFrom query parameter is passed" which {

      "is formatted correctly" should {

        val queryParams: Map[String, Seq[String]] = Map(dateFromKey -> Seq("2018-01-01"))

        "return an FinancialDataQueryParameters instance with correct parameters" in {

          val expected = Some(Right(FinancialDataQueryParameters(Some("2018-01-01"))))
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

        "return an FinancialDataQueryParameters instance with correct parameters" in {

          val expected = Some(Right(FinancialDataQueryParameters(None, Some("2018-01-01"))))
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

        "return an FinancialDataQueryParameters instance with correct parameters" in {

          val expected = Some(Right(FinancialDataQueryParameters(None, None, Some(true))))
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

    "if a includeLocks query parameter is passed" which {

      "is formatted correctly" should {

        val queryParams: Map[String, Seq[String]] = Map(includeLocksKey -> Seq("true"))

        "return an FinancialDataQueryParameters instance with correct parameters" in {

          val expected = Some(Right(FinancialDataQueryParameters(None, None, None, Some(true))))
          val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }

      "is formatted incorrectly" should {

        val queryParams: Map[String, Seq[String]] = Map(includeLocksKey -> Seq("banana"))

        "return a bad request error message with details of the error" in {

          val expected = Some(Left(s"Failed to bind '$includeLocksKey=banana' valid values are 'true' or 'false'."))
          val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }
    }

    "if a calculateAccruedInterest query parameter is passed" which {

      "is formatted correctly" should {

        val queryParams: Map[String, Seq[String]] = Map(calculateAccruedInterestKey -> Seq("true"))

        "return an FinancialDataQueryParameters instance with correct parameters" in {

          val expected = Some(Right(FinancialDataQueryParameters(None, None, None, None, Some(true))))
          val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }

      "is formatted incorrectly" should {

        val queryParams: Map[String, Seq[String]] = Map(calculateAccruedInterestKey -> Seq("banana"))

        "return a bad request error message with details of the error" in {

          val expected = Some(Left(s"Failed to bind '$calculateAccruedInterestKey=banana' valid values are 'true' or 'false'."))
          val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }
    }

    "if a customerPaymentInformation query parameter is passed" which {

      "is formatted correctly" should {

        val queryParams: Map[String, Seq[String]] = Map(customerPaymentInformationKey -> Seq("true"))

        "return an FinancialDataQueryParameters instance with correct parameters" in {

          val expected = Some(Right(FinancialDataQueryParameters(None, None, None, None, None, Some(true))))
          val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }

      "is formatted incorrectly" should {

        val queryParams: Map[String, Seq[String]] = Map(customerPaymentInformationKey -> Seq("banana"))

        "return a bad request error message with details of the error" in {

          val expected = Some(Left(s"Failed to bind '$customerPaymentInformationKey=banana' valid values are 'true' or 'false'."))
          val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }
    }

    "if a all query parameters are passed" which {

      "are formatted correctly" should {

        val queryParams: Map[String, Seq[String]] = Map(
          dateFromKey -> Seq("2018-01-01"),
          dateToKey -> Seq("2018-01-01"),
          onlyOpenItemsKey -> Seq("true"),
          includeLocksKey -> Seq("true"),
          calculateAccruedInterestKey -> Seq("true"),
          customerPaymentInformationKey -> Seq("true")
        )

        "return an FinancialDataQueryParameters instance with correct parameters" in {

          val expected = Some(Right(FinancialDataQueryParameters(
            fromDate = Some("2018-01-01"),
            toDate = Some("2018-01-01"),
            onlyOpenItems = Some(true),
            includeLocks = Some(true),
            calculateAccruedInterest = Some(true),
            customerPaymentInformation = Some(true)
          )))

          val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }

      "if all are incorrectly formatted" should {

        val queryParams: Map[String, Seq[String]] = Map(
          dateFromKey -> Seq("banana"),
          dateToKey -> Seq("banana2"),
          onlyOpenItemsKey -> Seq("banana3"),
          includeLocksKey -> Seq("banana4"),
          calculateAccruedInterestKey -> Seq("banana5"),
          customerPaymentInformationKey -> Seq("banana6")
        )

        "return a bad request error message with details of the error" in {

          val expected = Some(Left(
            s"Failed to bind '$dateFromKey=banana' valid date format should be 'YYYY-MM-DD'., " +
              s"Failed to bind '$dateToKey=banana2' valid date format should be 'YYYY-MM-DD'., " +
              s"Failed to bind '$onlyOpenItemsKey=banana3' valid values are 'true' or 'false'., " +
              s"Failed to bind '$includeLocksKey=banana4' valid values are 'true' or 'false'., " +
              s"Failed to bind '$calculateAccruedInterestKey=banana5' valid values are 'true' or 'false'., " +
              s"Failed to bind '$customerPaymentInformationKey=banana6' valid values are 'true' or 'false'."))

          val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }
    }

    "if an unrecognised query parameter is passed" should {

      val queryParams: Map[String, Seq[String]] = Map("unrecognisedDateParam" -> Seq("2018-01-01"))

      "ignore it and return an empty FinancialDataQueryParameters instance" in {

        val expected = Some(Right(FinancialDataQueryParameters()))
        val actual = FinancialTransactionsBinders.financialDataQueryBinder.bind("", queryParams)

        actual shouldBe expected
      }
    }
  }
}
