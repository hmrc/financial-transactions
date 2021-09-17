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

import java.net.URLEncoder
import java.time.LocalDate

import models.FinancialDataQueryParameters
import models.FinancialDataQueryParameters._
import play.api.mvc.QueryStringBindable

import scala.util.{Failure, Success, Try}

object FinancialTransactionsBinders {

  implicit def financialDataQueryBinder(implicit boolBinder: QueryStringBindable[Boolean],
                                        stringBinder: QueryStringBindable[String]): QueryStringBindable[FinancialDataQueryParameters] = {
    new QueryStringBindable[FinancialDataQueryParameters] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, FinancialDataQueryParameters]] = {
        val bindFrom = dateBind(dateFromKey, params)
        val bindTo = dateBind(dateToKey, params)
        val bindOnlyOpenItems = boolBind(onlyOpenItemsKey, params)
        val bindIncludeLocks = boolBind(includeLocksKey, params)
        val bindCalculateAccruedInterest = boolBind(calculateAccruedInterestKey, params)
        val bindCustomerPaymentInformation = boolBind(customerPaymentInformationKey, params)

        val queryParams = Seq(bindFrom, bindTo, bindOnlyOpenItems, bindIncludeLocks, bindCalculateAccruedInterest, bindCustomerPaymentInformation)

        queryParams.collect { case _@Left(errorMessage) => errorMessage } match {
          case x if x.nonEmpty => Some(Left(x.mkString(", "))) //Return the sequence of errors as a single concatenated string
          case _ => (bindFrom, bindTo, bindOnlyOpenItems, bindIncludeLocks, bindCalculateAccruedInterest, bindCustomerPaymentInformation) match {
            case (Right(from), Right(to), Right(onlyOpenItems), Right(includeLocks), Right(calculateAccruedInterest), Right(customerPaymentInfo)) =>
              Some(Right(FinancialDataQueryParameters(from, to, onlyOpenItems, includeLocks, calculateAccruedInterest, customerPaymentInfo)))
            case _ => throw new RuntimeException("Unexpected Runtime models.Error when Parsing/Binding Query Parameters")
          }
        }
      }

      override def unbind(key: String, params: FinancialDataQueryParameters): String = params.toSeqQueryParams.map {
        case (paramKey, paramValue) => s"$paramKey=${URLEncoder.encode(paramValue, "utf-8")}"
      }.mkString("&")

      private[binders] def dateBind(key: String, params: Map[String, Seq[String]]) = params.get(key) match {
        case Some(values) => Try(LocalDate.parse(values.head)) match {
          case Success(x) => Right(Some(x))
          case Failure(_) => Left(s"Failed to bind '$key=${values.head}' valid date format should be 'YYYY-MM-DD'.")
        }
        case _ => Right(None)
      }

      private[binders] def boolBind(key: String, params: Map[String, Seq[String]]) = params.get(key) match {
        case Some(values) => Try(values.head.toBoolean) match {
          case Success(x) => Right(Some(x))
          case Failure(_) => Left(s"Failed to bind '$key=${values.head}' valid values are 'true' or 'false'.")
        }
        case _ => Right(None)
      }
    }
  }
}
