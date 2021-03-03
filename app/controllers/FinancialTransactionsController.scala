/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers

import javax.inject.{Inject, Singleton}

import config.RegimeKeys
import controllers.actions.AuthAction
import models._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.FinancialTransactionsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class FinancialTransactionsController @Inject()(val authenticate: AuthAction,
                                                val financialTransactionsService: FinancialTransactionsService,
                                                cc: ControllerComponents) extends BackendController(cc) {

  def getFinancialTransactions(idType: String,
                               idValue: String,
                               queryParams: FinancialDataQueryParameters): Action[AnyContent] =
    authenticate.async {
      implicit authorisedUser =>
        idType.toUpperCase match {
          case RegimeKeys.VAT => retrieveFinancialTransactions(VatRegime(idValue), queryParams)
          case RegimeKeys.IT => retrieveFinancialTransactions(IncomeTaxRegime(idValue), queryParams)
          case _ =>
            Logger.warn(s"[FinancialTransactionsController][getFinancialTransactions] " +
              "Invalid Tax Regime '$idType' received in request.")
            Future.successful(BadRequest(Json.toJson(InvalidTaxRegime)))
        }
    }

  private def retrieveFinancialTransactions(regime: TaxRegime, queryParams: FinancialDataQueryParameters)
                                           (implicit hc: HeaderCarrier) = {
    Logger.debug(s"[FinancialTransactionsController][retrieveFinancialTransactions] " +
      "Calling FinancialTransactionsService.getFinancialTransactions")
    financialTransactionsService.getFinancialTransactions(regime, queryParams).map {
      case _@Right(financialTransactions) => Ok(Json.toJson(financialTransactions))
      case _@Left(error) => error.error match {
        case singleError: Error => Status(error.status)(Json.toJson(singleError))
        case multiError: MultiError => Status(error.status)(Json.toJson(multiError))
      }
    }
  }

  def checkDirectDebitExists(vrn: String): Action[AnyContent] =
    authenticate.async {
      implicit authorisedUser =>
        Logger.debug(s"[FinancialTransactionsController][checkDirectDebitExists] " +
          "Calling FinancialTransactionsService.checkDirectDebitExists")
        financialTransactionsService.checkDirectDebitExists(vrn).map {
          case _@Right(directDebitExists) =>
            Ok(Json.toJson(directDebitExists))
          case _@Left(error) => error.error match {
            case singleError: Error => Status(error.status)(Json.toJson(singleError))
            case multiError: MultiError => Status(error.status)(Json.toJson(multiError))
          }
        }
    }
}
