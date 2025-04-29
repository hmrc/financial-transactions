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

import java.net.URLEncoder
import java.time.LocalDate
import models.FinancialRequestQueryParameters
import play.api.mvc.QueryStringBindable
import scala.util.{Failure, Success, Try}

object FinancialTransactionsBinders {

  private val dateFromKey = "dateFrom"
  private val dateToKey = "dateTo"
  private val onlyOpenItemsKey = "onlyOpenItems"

  implicit def financialDataQueryBinder: QueryStringBindable[FinancialRequestQueryParameters] = {
    new QueryStringBindable[FinancialRequestQueryParameters] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, FinancialRequestQueryParameters]] = {


        val bindFrom = dateBind(dateFromKey, params)
        val bindTo = dateBind(dateToKey, params)
        val bindOnlyOpenItems = boolBind(onlyOpenItemsKey, params)

        val queryParams = (bindFrom, bindTo, bindOnlyOpenItems)
        val seqParams = Seq(bindFrom, bindTo, bindOnlyOpenItems)

        queryParams match {
          case (Right(from), Right(to), Right(openItems)) =>
            Some(Right(FinancialRequestQueryParameters(from, to, openItems)))
          case _ =>
            Some(Left(seqParams.collect{ case _@Left(errorMessage) => errorMessage }.mkString(", ")))
        }
      }

      override def unbind(key: String, params: FinancialRequestQueryParameters): String = params.queryParams1166.map {
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
