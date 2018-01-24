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

package binders

import java.time.LocalDate

import models.FinancialDataQueryParameters
import play.api.mvc.QueryStringBindable

import scala.util.{Failure, Success, Try}

object FinancialTransactionsBinders {

  implicit def financialDataQueryBinder(implicit boolBinder: QueryStringBindable[Boolean],
                                        stringBinder: QueryStringBindable[String]): QueryStringBindable[FinancialDataQueryParameters] = {
    new QueryStringBindable[FinancialDataQueryParameters] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, FinancialDataQueryParameters]] = {
        import FinancialDataQueryParameters._
        val bindFrom = dateBind(dateFromKey, params)
        val bindTo = dateBind(dateToKey, params)
        val bindOnlyOpenItems = boolBind(onlyOpenItemsKey, params)
        val bindIncludeLocks = boolBind(includeLocksKey, params)
        val bindCalculateAccruedInterest = boolBind(calculateAccruedInterestKey, params)
        val bindCustomerPaymentInformation = boolBind(customerPaymentInformationKey, params)

        val queryParams = Seq(bindFrom, bindTo, bindOnlyOpenItems, bindIncludeLocks, bindCalculateAccruedInterest, bindCustomerPaymentInformation)

        if (queryParams.exists(_.isInstanceOf[Left[String, _]])) {
          Some(Left(queryParams.collect { case _@Left(errorMessage) => errorMessage }.mkString))
        } else {
          (bindFrom, bindTo, bindOnlyOpenItems, bindIncludeLocks, bindCalculateAccruedInterest, bindCustomerPaymentInformation) match {
            case (Right(from), Right(to), Right(onlyOpenItems), Right(includeLocks), Right(calculateAccruedInterest), Right(customerPaymentInfo)) =>
              Some(Right(FinancialDataQueryParameters(from, to, onlyOpenItems, includeLocks, calculateAccruedInterest, customerPaymentInfo)))
          }
        }
      }

      override def unbind(key: String, params: FinancialDataQueryParameters): String = {
        import FinancialDataQueryParameters._
        params.fromDate.map(from => stringBinder.unbind(dateFromKey, from.toString)).getOrElse("") +
          params.toDate.map(to => "&" + stringBinder.unbind(dateToKey, to.toString)).getOrElse("") +
          params.onlyOpenItems.map("&" + boolBinder.unbind(onlyOpenItemsKey, _)).getOrElse("") +
          params.includeLocks.map("&" + boolBinder.unbind(includeLocksKey, _)).getOrElse("") +
          params.calculateAccruedInterest.map("&" + boolBinder.unbind(calculateAccruedInterestKey, _)).getOrElse("") +
          params.customerPaymentInformation.map("&" + boolBinder.unbind(customerPaymentInformationKey, _)).getOrElse("")
      }

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
