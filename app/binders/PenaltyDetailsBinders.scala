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

import models.PenaltyDetailsQueryParameters
import models.PenaltyDetailsQueryParameters.dateLimitKey
import play.api.mvc.QueryStringBindable

import java.net.URLEncoder
import scala.util.{Success, Try}

object PenaltyDetailsBinders {

  implicit def penaltyDetailsQueryBinder: QueryStringBindable[PenaltyDetailsQueryParameters] = {
    new QueryStringBindable[PenaltyDetailsQueryParameters] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, PenaltyDetailsQueryParameters]] = {
        val bindDateLimit = dateLimitBind(dateLimitKey, params)

        val queryParams = bindDateLimit
        val seqParams = Seq(bindDateLimit)

        queryParams match {
          case Right(dateLimit) =>
            Some(Right(PenaltyDetailsQueryParameters(dateLimit)))
          case _ =>
            Some(Left(seqParams.collect { case _@Left(errorMessage) => errorMessage }.mkString(", ")))
        }
      }

      override def unbind(key: String, params: PenaltyDetailsQueryParameters): String = params.toSeqQueryParams.map {
        case (paramKey, paramValue) => s"$paramKey=${URLEncoder.encode(paramValue, "utf-8")}"
      }.mkString("&")

      private[binders] def dateLimitBind(key: String, params: Map[String, Seq[String]]) = params.get(key) match {
        case Some(values) => Try(values.head.toInt) match {
          case Success(x) if x >= 0 & x <= 99 => Right(Some(x))
          case _ => Left(s"Failed to bind '$key=${values.headOption.getOrElse("")}' valid values are between 0 and 99 inclusive.")
        }
        case _ => Right(None)
      }
    }
  }
}
