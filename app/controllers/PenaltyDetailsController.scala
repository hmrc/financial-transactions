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

package controllers

import config.RegimeKeys
import controllers.actions.AuthAction
import models.API1166.InvalidTaxRegime
import models.{PenaltyDetailsQueryParameters, VatRegime}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.API1812.PenaltyDetailsService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}
import javax.inject.{Inject, Singleton}

@Singleton
class PenaltyDetailsController @Inject()(authenticate: AuthAction,
                                         penaltyDetailsService: PenaltyDetailsService,
                                         cc: ControllerComponents)
                                        (implicit ec: ExecutionContext) extends BackendController(cc) with LoggerUtil {

  def getPenaltyDetails(idType: String,
                        idValue: String,
                        queryParams: PenaltyDetailsQueryParameters): Action[AnyContent] = authenticate.async {
    implicit authorisedUser =>
      idType.toUpperCase match {
        case RegimeKeys.VAT =>
          penaltyDetailsService.getPenaltyDetails(VatRegime(idValue), queryParams).map {
            case Right(penaltyDetails) => Ok(Json.toJson(penaltyDetails))
            case Left(error) => Status(error.code)(Json.toJson(error))
          }
        case regime =>
          logger.warn(s"[PenaltyDetailsController][getPenaltyDetails] " +
            s"Invalid Tax Regime '$regime' received in request.")
          Future.successful(BadRequest(Json.toJson(InvalidTaxRegime)))
      }
    }
}

