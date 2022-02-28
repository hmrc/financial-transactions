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

package mocks.connectors

import connectors.API1166.FinancialDataConnector
import connectors.API1166.httpParsers.FinancialTransactionsHttpParser.HttpGetResult
import models.API1166.FinancialTransactions
import models.{DirectDebits, FinancialRequestQueryParameters, TaxRegime}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterEach, OptionValues}

import scala.concurrent.Future


trait Mock1166FinancialDataConnector extends AnyWordSpecLike with Matchers with OptionValues with BeforeAndAfterEach with MockitoSugar {

  val mockFinancialDataConnector: FinancialDataConnector = mock[FinancialDataConnector]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockFinancialDataConnector)
  }

  def setupMockGetFinancialData(regime: TaxRegime, queryParameters: FinancialRequestQueryParameters)
                               (response: HttpGetResult[FinancialTransactions]): OngoingStubbing[Future[HttpGetResult[FinancialTransactions]]] =
    when(
      mockFinancialDataConnector.getFinancialData(
        ArgumentMatchers.eq(regime),
        ArgumentMatchers.eq(queryParameters)
      )(ArgumentMatchers.any(), ArgumentMatchers.any())
    ).thenReturn(Future.successful(response))

  def setupMockCheckDirectDebitExists(vrn: String)
                               (response: HttpGetResult[DirectDebits]): OngoingStubbing[Future[HttpGetResult[DirectDebits]]] =
    when(
      mockFinancialDataConnector.checkDirectDebitExists(
        ArgumentMatchers.eq(vrn)
      )(ArgumentMatchers.any(), ArgumentMatchers.any())
    ).thenReturn(Future.successful(response))
}
