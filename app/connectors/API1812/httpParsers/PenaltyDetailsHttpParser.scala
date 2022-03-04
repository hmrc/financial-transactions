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

package connectors.API1812.httpParsers

import models.API1812.{Error, PenaltyDetails}
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND, OK}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.LoggerUtil

object PenaltyDetailsHttpParser extends LoggerUtil {

  type PenaltyDetailsResponse = Either[Error, PenaltyDetails]

  implicit object PenaltyDetailsReads extends HttpReads[PenaltyDetailsResponse] {
    override def read(method: String, url: String, response: HttpResponse): PenaltyDetailsResponse = {
      response.status match {
        case OK =>
          response.json.validate[PenaltyDetails].fold(
            invalid => {
              logger.warn("[PenaltyDetailsReads][read] Json Error Parsing Successful EIS Response")
              logger.debug(s"[PenaltyDetailsReads][read] EIS Response: ${response.json}\nJson Errors: $invalid")
              Left(Error(BAD_REQUEST, "UNEXPECTED_JSON_FORMAT - The downstream service responded with json which did not match the expected format."))
            },
            valid => {
              logger.debug(s"[PenaltyDetailsReads][read] EIS Response: \n\n${response.json}")
              logger.debug(s"[PenaltyDetailsReads][read] Get Penalty Details Model: \n\n$valid")
              Right(valid)
            }
          )
        case NOT_FOUND =>
          logger.debug("[PenaltyDetailsReads][read] Error received: " + response)
          Left(Error(response.status,response.body))
        case _ =>
          logger.warn(s"[PenaltyDetailsReads][read] unexpected ${response.status} returned from EIS " +
            s"Status code:'${response.status}', Body: '${response.body}")
          Left(Error(response.status, response.body))
      }
    }
  }

}
