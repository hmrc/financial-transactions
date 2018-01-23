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

package services

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import connectors.FinancialDataConnector
import models.{DesErrors, FinancialDataQueryParameters, FinancialTransactions, TaxRegime}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialTransactionsService @Inject()(val financialDataConnector: FinancialDataConnector) {

  def getFinancialTransactions(regime: TaxRegime,
                               fromDate: Option[LocalDate] = None,
                               toDate: Option[LocalDate] = None,
                               onlyOpenItems: Option[Boolean] = None,
                               includeLocks: Option[Boolean] = None,
                               calculateAccruedInterest: Option[Boolean] = None,
                               customerPaymentInformation: Option[Boolean] = None
                              )(implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[Either[DesErrors, FinancialTransactions]] = {
    financialDataConnector.getFinancialData(
      regime,
      FinancialDataQueryParameters(
        fromDate,
        toDate,
        onlyOpenItems,
        includeLocks,
        calculateAccruedInterest,
        customerPaymentInformation
      )
    )
  }
}
