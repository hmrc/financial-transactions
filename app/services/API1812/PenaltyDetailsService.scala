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

package services.API1812

import com.google.inject.Inject
import config.featureSwitch.Features
import connectors.API1812.{HIPPenaltyDetailsConnector, PenaltyDetailsConnector}
import connectors.API1812.httpParsers.PenaltyDetailsHttpParser.PenaltyDetailsResponse
import models.{PenaltyDetailsQueryParameters, TaxRegime}
import uk.gov.hmrc.http.HeaderCarrier
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

class PenaltyDetailsService @Inject()(eisConnector: PenaltyDetailsConnector,
                                     hipConnector: HIPPenaltyDetailsConnector,
                                     features: Features) extends LoggerUtil {

  def getPenaltyDetails(regime: TaxRegime, queryParameters: PenaltyDetailsQueryParameters)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[PenaltyDetailsResponse] = {
    
    if (features.CallAPI1812HIP()) {
      logger.info("[PenaltyDetailsService][getPenaltyDetails] - Using HIP connector (feature flag enabled)")
      hipConnector.getPenaltyDetails(regime, queryParameters)
    } else {
      logger.info("[PenaltyDetailsService][getPenaltyDetails] - Using EIS connector (feature flag disabled)")
      eisConnector.getPenaltyDetails(regime, queryParameters)
    }
  }
}
