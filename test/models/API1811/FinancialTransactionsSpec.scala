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

package models.API1811

import base.SpecBase
import org.scalatest.BeforeAndAfterAll
import play.api.libs.json.Json
import utils.API1811.TestConstants._

import java.time.LocalDate

class FinancialTransactionsSpec extends SpecBase with BeforeAndAfterAll {

  override def beforeAll(): Unit = mockAppConfig.features.staticDate(true)

  "FinancialTransactions" should {

    "read from JSON" when {

      "maximum fields are present" in {
        fullFinancialTransactionsJsonEIS.as[FinancialTransactions] shouldBe fullFinancialTransactions
      }
    }

    "write to JSON" when {

      "maximum fields are present" in {
        Json.toJson(fullFinancialTransactions) shouldBe fullFinancialTransactionsOutputJson
      }

      "there are multiple document details objects" in {
        val model = FinancialTransactions(Seq(fullDocumentDetails, fullDocumentDetails))
        val expectedOutput = Json.obj(
          "financialTransactions" -> Json.arr(fullDocumentDetailsOutputJson, fullDocumentDetailsOutputJson),
          "hasOverdueChargeAndNoTTP" -> true
        )

        Json.toJson(model) shouldBe expectedOutput
      }
    }
  }

  "The hasOverdueChargeAndNoTTP function" should {

    def generateDocDetails(dueDate: String, lockDetails: Seq[LineItemLockDetails]): DocumentDetails =
      emptyDocumentDetails.copy(
        lineItemDetails = Seq(emptyLineItem.copy(
          netDueDate = Some(LocalDate.parse(dueDate)),
          lineItemLockDetails = lockDetails
        ))
      )

    "return true" when {

      "there is an overdue charge and no payment locks" in {
        val docDetails = generateDocDetails("2018-01-01", Seq())
        FinancialTransactions.hasOverdueChargeAndNoTTP(Seq(docDetails)) shouldBe true
      }

      "there is an overdue charge and irrelevant payment lock values" in {
        val docDetails = generateDocDetails("2018-01-01", Seq(LineItemLockDetails("Some lock reason")))
        FinancialTransactions.hasOverdueChargeAndNoTTP(Seq(docDetails)) shouldBe true
      }
    }

    "return false" when {

      "there is an overdue charge and a TTP payment lock value" in {
        val docDetails = generateDocDetails("2018-01-01", Seq(LineItemLockDetails("Collected via TTP")))
        FinancialTransactions.hasOverdueChargeAndNoTTP(Seq(docDetails)) shouldBe false
      }

      "there is an overdue charge and there are multiple TTP payment lock values" in {
        val docDetails = generateDocDetails(
          "2018-01-01", Seq(LineItemLockDetails("Collected via TTP"), LineItemLockDetails("Collected via TTP"))
        )
        FinancialTransactions.hasOverdueChargeAndNoTTP(Seq(docDetails)) shouldBe false
      }

      "there is an overdue charge without a TTP lock and a due charge with a TTP lock" in {
        val docDetails1 = generateDocDetails("2018-01-01", Seq())
        val docDetails2 = generateDocDetails("2019-01-01", Seq(LineItemLockDetails("Collected via TTP")))
        FinancialTransactions.hasOverdueChargeAndNoTTP(Seq(docDetails1, docDetails2)) shouldBe false
      }

      "there are no overdue charges and a TTP payment lock value" in {
        val docDetails = generateDocDetails("2019-01-01", Seq(LineItemLockDetails("Collected via TTP")))
        FinancialTransactions.hasOverdueChargeAndNoTTP(Seq(docDetails)) shouldBe false
      }

      "there are no overdue charges and irrelevant payment lock values" in {
        val docDetails = generateDocDetails("2019-01-01", Seq(LineItemLockDetails("Some lock reason")))
        FinancialTransactions.hasOverdueChargeAndNoTTP(Seq(docDetails)) shouldBe false
      }

      "there are no overdue charges and no payment locks" in {
        val docDetails = generateDocDetails("2019-01-01", Seq())
        FinancialTransactions.hasOverdueChargeAndNoTTP(Seq(docDetails)) shouldBe false
      }

      "the charges have no due dates" in {
        FinancialTransactions.hasOverdueChargeAndNoTTP(Seq(emptyDocumentDetails)) shouldBe false
      }
    }
  }
}
