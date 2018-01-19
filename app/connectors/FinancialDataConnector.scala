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

package connectors

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import config.MicroserviceAppConfig
import connectors.httpParsers.FinancialTransactionsHttpParser._
import models.{FinancialTransactions, TaxRegime}
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialDataConnector @Inject()(val http: HttpClient, val appConfig: MicroserviceAppConfig) {

  private[connectors] def financialDataUrl(regime: TaxRegime) =
    s"${appConfig.desUrl}/enterprise/financial-data/${regime.idType}/${regime.id}/${regime.regimeType}"

  def getFinancialTransactions(regime: TaxRegime,
                               fromDate: Option[LocalDate],
                               toDate: Option[LocalDate],
                               onlyOpenItems: Option[Boolean],
                               includeLocks: Option[Boolean],
                               calculateAccruedInterest: Option[Boolean],
                               customerPaymentInformation: Option[Boolean]
                              )(implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[FinancialTransactions]] = {

    val url = financialDataUrl(regime)
    val desHC = headerCarrier.copy(authorization = Some(Authorization(s"Bearer ${appConfig.desToken}")))
      .withExtraHeaders("Environment" -> appConfig.desEnvironment)
    val queryParams = Seq(
      fromDate.map("dateFrom" -> _.toString),
      toDate.map("dateTo" -> _.toString),
      onlyOpenItems.map("onlyOpenItems" -> _.toString),
      includeLocks.map("includeLocks" -> _.toString),
      calculateAccruedInterest.map("calculateAccruedInterest" -> _.toString),
      customerPaymentInformation.map("customerPaymentInformation" -> _.toString)
    ).flatten

    Logger.debug(s"[FinancialDataConnector][getFinancialTransactions] - Calling GET $url \nHeaders: $desHC\n QueryParams: $queryParams")
    http.GET(url, queryParams)(FinancialTransactionsReads, desHC, ec).map {
      case financialTransactions@Right(_) => financialTransactions
      case httpError@Left(error) =>
        Logger.warn("[FinancialDataConnector][getFinancialTransactions] received error: " + error.message)
        httpError
    }
  }
}
