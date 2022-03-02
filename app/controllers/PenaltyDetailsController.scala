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



import config.{MicroserviceAppConfig, RegimeKeys}
import controllers.actions.AuthAction
import models.{PenaltyDetailsQueryParameters, TaxRegime, VatRegime}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.API1812.PenaltyDetailsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.LoggerUtil
import scala.concurrent.Future
import javax.inject.{Inject, Singleton}


@Singleton
class PenaltyDetailsController @Inject()(authenticate: AuthAction,
                                        penaltyDetailsService: PenaltyDetailsService,
                                        cc: ControllerComponents,
                                        implicit val appConfig: MicroserviceAppConfig) extends BackendController(cc) with LoggerUtil {

  def getPenaltyDetails(idValue: String,
                        queryParams: PenaltyDetailsQueryParameters): Action[AnyContent] =

    authenticate.async {
      implicit authorisedUser =>
      idValue.toUpperCase match {
        case RegimeKeys.idType => retrievePenaltyDetails(VatRegime(idValue), queryParams)
        case _ =>
          logger.warn(s"[PenaltyDetailsController][getPenaltyDetails] " +
            "Invalid Tax Regime '$$idType' received in request.")
          Future.successful(BadRequest(Json.toJson(NewInvalidTaxRegime)))
      }
    }


  private def retrievePenaltyDetails(regime: TaxRegime, queryParams: PenaltyDetailsQueryParameters)
                                                  (implicit hc: HeaderCarrier) = {
    logger.debug(s"[PenaltyDetailsController][retrievePenaltyDetailsAPI1812] " +
      "Calling API1812.PenaltyDetailsService.getPenaltyDetails")

    penaltyDetailsService.getPenaltyDetails(regime, queryParams).map {
      case Right(penaltyDetails) => Ok(Json.toJson(penaltyDetails))
      case Left(error) => Status(error.code)(Json.toJson(error))
    }
  }

}

