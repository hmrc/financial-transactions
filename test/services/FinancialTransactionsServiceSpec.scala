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

import base.SpecBase
import mocks.connectors.MockFinancialDataConnector
import models.{DesError, FinancialDataQueryParameters, VatRegime}

class FinancialTransactionsServiceSpec extends SpecBase with MockFinancialDataConnector {


  object TestFinancialTransactionService extends FinancialTransactionsService(mockFinancialDataConnector)

  lazy val regime = VatRegime("123456")

  "The FinancialTransactionService.getFinancialTransactions method" should {

    "Return an error when a DesError is returned from the Connector" in {

      val expected = Left(DesError("CODE","REASON"))
      setupMockGetFinancialData(regime, FinancialDataQueryParameters())(expected)
      val actual = TestFinancialTransactionService.getFinancialTransactions(regime)

      await(actual) shouldBe expected

    }

  }

}
