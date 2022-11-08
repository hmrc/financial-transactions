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

package controllers

import javax.inject.{Inject, Singleton}
import config.{MicroserviceAppConfig, RegimeKeys}
import controllers.actions.AuthAction
import models.API1166._
import models._
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.API1166.FinancialTransactionsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialTransactionsController @Inject()(authenticate: AuthAction,
                                                financialTransactionsService: FinancialTransactionsService,
                                                api1811Service: services.API1811.FinancialTransactionsService,
                                                cc: ControllerComponents)
                                               (implicit appConfig: MicroserviceAppConfig,
                                                ec: ExecutionContext) extends BackendController(cc) with LoggerUtil {

  def getFinancialTransactions(idType: String,
                               idValue: String,
                               queryParams: FinancialRequestQueryParameters): Action[AnyContent] =
    authenticate.async {
      implicit authorisedUser =>
      if(appConfig.features.useApi1811()) {
        idType.toUpperCase match {
          case RegimeKeys.VAT => retrieveFinancialTransactionsAPI1811(VatRegime(idValue), queryParams)
          case regime =>
            logger.warn(s"[FinancialTransactionsController][getFinancialTransactions] " +
              s"Invalid Tax Regime '$regime' received in request.")
            Future.successful(BadRequest(Json.toJson(InvalidTaxRegime)))
        }
      } else {
        idType.toUpperCase match {
          case RegimeKeys.VAT => retrieveFinancialTransactions(VatRegime(idValue), queryParams)
          case regime =>
            logger.warn(s"[FinancialTransactionsController][getFinancialTransactions] " +
              s"Invalid Tax Regime '$regime' received in request.")
            Future.successful(BadRequest(Json.toJson(InvalidTaxRegime)))
        }
      }
    }

  private def retrieveFinancialTransactions(regime: TaxRegime, queryParams: FinancialRequestQueryParameters)
                                           (implicit hc: HeaderCarrier) = {
    logger.debug(s"[FinancialTransactionsController][retrieveFinancialTransactions] " +
      "Calling FinancialTransactionsService.getFinancialTransactions")

    financialTransactionsService.getFinancialTransactions(regime, queryParams).map {
      case _@Right(financialTransactions) => Ok(Json.toJson(financialTransactions))
      case _@Left(error) => error.error match {
        case singleError: Error => Status(error.status)(Json.toJson(singleError))
        case multiError: MultiError => Status(error.status)(Json.toJson(multiError))
      }
    }
  }

  private def retrieveFinancialTransactionsAPI1811(regime: TaxRegime, queryParams: FinancialRequestQueryParameters)
                                                  (implicit hc: HeaderCarrier) = {
    logger.debug(s"[FinancialTransactionsController][retrieveFinancialTransactionsAPI1811] " +
      "Calling API1811.FinancialTransactionsService.getFinancialTransactions")

    api1811Service.getFinancialTransactions(regime, queryParams).map {
      case Right(financialTransactions) => Ok("") // TODO put the transactions back in once Writes exists again
      case Left(error) => Status(error.code)(Json.toJson(error))
    }
  }

  def checkDirectDebitExists(vrn: String): Action[AnyContent] =
    authenticate.async {
      implicit authorisedUser =>
        logger.debug(s"[FinancialTransactionsController][checkDirectDebitExists] " +
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
