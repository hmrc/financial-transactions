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
import connectors.API1812.PenaltyDetailsConnector
import connectors.API1812.httpParsers.PenaltyDetailsHttpParser.PenaltyDetailsResponse
import models.API1812.Error
import models.{PenaltyDetailsQueryParameters, TaxRegime}
import play.api.http.Status
import uk.gov.hmrc.http.HeaderCarrier
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

class PenaltyDetailsService @Inject()(connector: PenaltyDetailsConnector) extends LoggerUtil {

  def getPenaltyDetails(regime: TaxRegime, queryParameters: PenaltyDetailsQueryParameters)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[PenaltyDetailsResponse] =
    connector.getPenaltyDetails(regime, queryParameters).map {
      case Right(details) if details.LPPDetails.isEmpty => Left(Error(Status.NOT_FOUND, "No LPP data was found"))
      case either => either
    }
}
