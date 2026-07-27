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

package services.API1811

import base.SpecBase
import connectors.API1811.FinancialDataHIPConnector
import models.API1811.{Error, FinancialData}
import models.{FinancialRequestQueryParameters, TaxRegime, VatRegime}
import org.mockito.Mockito.{mock, when}
import play.api.http.Status
import play.api.http.Status.NOT_FOUND
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.API1811.TestConstants.{fullDocumentDetails, fullFinancialData, lineItemDetailsFull}

import java.time.LocalDate
import scala.concurrent.Future

class FinancialDataServiceSpec extends SpecBase {

  implicit val request: FakeRequest[_]                         = fakeRequest
  val mockFinancialDataHIPConnector: FinancialDataHIPConnector = mock(classOf[FinancialDataHIPConnector])

  object Service extends FinancialTransactionsService(mockFinancialDataHIPConnector, ec)

  def setupHipMockGetFinancialData(regime: TaxRegime, queryParams: FinancialRequestQueryParameters)(response: Either[Error, FinancialData]): Unit =
    when(mockFinancialDataHIPConnector.getFinancialData(regime, queryParams))
      .thenReturn(Future.successful(response))

  "getFinancialTransactions" when {
    val regime: TaxRegime = VatRegime("123456")
    val queryParams: FinancialRequestQueryParameters = FinancialRequestQueryParameters(
      fromDate = Some(LocalDate.parse("2017-04-06")),
      toDate = Some(LocalDate.parse("2018-04-05")),
      onlyOpenItems = Some(false)
    )

    "the connector returns a success response" when {
      "only valid charge types are returned" should {
        "return the financial data mapped from a FinancialTransactionsHIP to a FinancialTransactions model" in {
          setupHipMockGetFinancialData(regime, queryParams)(Right(fullFinancialData))
          val actual = await(Service.getFinancialTransactions(regime, queryParams))

          actual shouldBe Right(fullFinancialData)
        }
      }

      "invalid charge types are returned" should {
        "return a success response with the bad transactions filtered" in {
          val lineItems       = Seq(lineItemDetailsFull.copy(mainTransaction = Some("1111")))
          val documentDetails = Seq(fullDocumentDetails.copy(lineItemDetails = lineItems))

          val successWithInvalidDataResponse = Right(FinancialData(documentDetails))
          setupHipMockGetFinancialData(regime, queryParams)(successWithInvalidDataResponse)
          val actual = await(Service.getFinancialTransactions(regime, queryParams))

          actual shouldBe Right(fullFinancialData.copy(documentDetails = Seq()))
        }
      }
    }

    "the connector returns a failure response" should {
      "return the error response from the connector" in {
        val connectorResponse = Left(Error(NOT_FOUND, "ID number did not match any penalty data"))
        setupHipMockGetFinancialData(regime, queryParams)(connectorResponse)
        val actual = await(Service.getFinancialTransactions(regime, queryParams))

        actual shouldBe Left(Error(Status.NOT_FOUND, "ID number did not match any penalty data"))
      }
    }
  }

}
