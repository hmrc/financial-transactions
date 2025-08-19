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
import connectors.API1811.httpParsers.FinancialTransactionsHttpHIPParser.{
  FinancialTransactionsFailure,
  FinancialTransactionsMalformed,
  FinancialTransactionsNoContent
}
import connectors.API1811.{FinancialDataConnector, FinancialDataHIPConnector}
import models.API1811.{BusinessError, Error, FinancialTransactions, FinancialTransactionsHIP, TechnicalError}
import models.{FinancialRequestQueryParameters, TaxRegime, VatRegime}
import org.mockito.Mockito.{mock, when}
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.API1811.TestConstants.{fullDocumentDetails, fullFinancialTransactions, fullFinancialTransactionsHIP, lineItemDetailsFull}

import java.time.LocalDate
import scala.concurrent.Future

class FinancialTransactionsServiceSpec extends SpecBase {

  implicit val request: FakeRequest[_]                         = fakeRequest
  val mockFinancialDataConnector: FinancialDataConnector       = mock(classOf[FinancialDataConnector])
  val mockFinancialDataHIPConnector: FinancialDataHIPConnector = mock(classOf[FinancialDataHIPConnector])

  object Service extends FinancialTransactionsService(mockFinancialDataConnector, mockFinancialDataHIPConnector, ec)

  def setupMockGetFinancialData(regime: TaxRegime, queryParams: FinancialRequestQueryParameters)(
      response: Either[Error, FinancialTransactions]): Unit =
    when(mockFinancialDataConnector.getFinancialData(regime, queryParams))
      .thenReturn(Future.successful(response))

  def setupHipMockGetFinancialData(regime: TaxRegime, queryParams: FinancialRequestQueryParameters)(
      response: Either[FinancialTransactionsFailure, FinancialTransactionsHIP]): Unit =
    when(mockFinancialDataHIPConnector.getFinancialDataHIP(regime, queryParams))
      .thenReturn(Future.successful(response))

  "getFinancialTransactions" when {
    val regime: TaxRegime = VatRegime("123456")
    val queryParams: FinancialRequestQueryParameters = FinancialRequestQueryParameters(
      fromDate = Some(LocalDate.parse("2017-04-06")),
      toDate = Some(LocalDate.parse("2018-04-05")),
      onlyOpenItems = Some(false)
    )

    "HIP feature switch is enabled" when {
      "the connector returns a success response" when {
        "only valid charge types are returned" should {
          "return the financial data mapped from a FinancialTransactionsHIP to a FinancialTransactions model" in {
            mockAppConfig.features.enable1811HIPCall(true)
            setupHipMockGetFinancialData(regime, queryParams)(Right(fullFinancialTransactionsHIP))
            val actual = await(Service.getFinancialTransactions(regime, queryParams))

            actual shouldBe Right(fullFinancialTransactions)
          }
        }

        "invalid charge types are returned" should {
          "return a success response with the bad transactions filtered" in {
            mockAppConfig.features.enable1811HIPCall(true)
            val lineItems       = Seq(lineItemDetailsFull.copy(mainTransaction = Some("1111")))
            val documentDetails = Seq(fullDocumentDetails.copy(lineItemDetails = lineItems))

            val successWithInvalidDataResponse = Right(FinancialTransactionsHIP("processingDate", FinancialTransactions(documentDetails)))
            setupHipMockGetFinancialData(regime, queryParams)(successWithInvalidDataResponse)
            val actual = await(Service.getFinancialTransactions(regime, queryParams))

            actual shouldBe Right(fullFinancialTransactions.copy(documentDetails = Seq()))
          }
        }
      }

      "the connector returns a failure response" when {
        "failure due to no data found, should return a FinancialTransactionsNoContent response" in {
          mockAppConfig.features.enable1811HIPCall(true)
          val connectorResponse = Left(FinancialTransactionsNoContent)
          setupHipMockGetFinancialData(regime, queryParams)(connectorResponse)
          val actual = await(Service.getFinancialTransactions(regime, queryParams))

          actual shouldBe Left(Error(Status.NOT_FOUND, "404: Financial data not found for VRN 123456"))
        }

        "failure is a BusinessError, should return a BAD_REQUEST response" in {
          mockAppConfig.features.enable1811HIPCall(true)
          val connectorResponse = Left(BusinessError("processingDate", "015", "Bad Request result"))
          setupHipMockGetFinancialData(regime, queryParams)(connectorResponse)
          val actual = await(Service.getFinancialTransactions(regime, queryParams))

          actual shouldBe Left(Error(Status.BAD_REQUEST, "015: Bad Request result"))
        }

        "failure is a TechnicalError, should return an INTERNAL_SERVER_ERROR response" in {
          mockAppConfig.features.enable1811HIPCall(true)
          val connectorResponse = Left(TechnicalError("002", "Invalid Tax Regime", "logId"))
          setupHipMockGetFinancialData(regime, queryParams)(connectorResponse)
          val actual = await(Service.getFinancialTransactions(regime, queryParams))

          actual shouldBe Left(Error(Status.INTERNAL_SERVER_ERROR, "002: Invalid Tax Regime"))
        }

        "failure is a different kind of error, should resort to returning an INTERNAL_SERVER_ERROR response" in {
          mockAppConfig.features.enable1811HIPCall(true)
          val connectorResponse = Left(FinancialTransactionsMalformed)
          setupHipMockGetFinancialData(regime, queryParams)(connectorResponse)
          val actual = await(Service.getFinancialTransactions(regime, queryParams))

          actual shouldBe Left(Error(Status.INTERNAL_SERVER_ERROR, "Unexpected HIP failure occurred"))
        }
      }
    }

    "HIP feature switch is disabled" when {
      "the connector returns a success response" when {
        "only valid charge types are returned" should {
          "return the same response" in {
            mockAppConfig.features.enable1811HIPCall(false)
            val successResponse = Right(fullFinancialTransactions)
            setupMockGetFinancialData(regime, queryParams)(successResponse)
            val actual = await(Service.getFinancialTransactions(regime, queryParams))

            actual shouldBe successResponse
          }
        }

        "invalid charge types are returned" should {
          "return a success response with the bad transactions filtered" in {
            mockAppConfig.features.enable1811HIPCall(false)
            val lineItems       = Seq(lineItemDetailsFull.copy(mainTransaction = Some("1111")))
            val documentDetails = Seq(fullDocumentDetails.copy(lineItemDetails = lineItems))

            val successWithInvalidDataResponse = Right(FinancialTransactions(documentDetails))
            setupMockGetFinancialData(regime, queryParams)(successWithInvalidDataResponse)
            val actual = await(Service.getFinancialTransactions(regime, queryParams))

            actual shouldBe Right(fullFinancialTransactions.copy(documentDetails = Seq()))
          }
        }
      }

      "the connector returns a failure response" when {
        "return the same response" in {
          mockAppConfig.features.enable1811HIPCall(false)
          val failureResponse = Left(Error(Status.INTERNAL_SERVER_ERROR, "error"))
          setupMockGetFinancialData(regime, queryParams)(failureResponse)
          val actual = await(Service.getFinancialTransactions(regime, queryParams))

          actual shouldBe Left(Error(Status.INTERNAL_SERVER_ERROR, "error"))
        }
      }
    }
  }

}
