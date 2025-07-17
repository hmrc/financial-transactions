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

package connectors.API1812.httpParsers

import models.API1812.{Error, PenaltyDetails}
import models.hip_API1812.{HIPSuccessResponse, HIPSuccess, HIPPenaltyData, HIPLpp}
import models.hip_API1812.{HIPErrorResponse, HIPBusinessError, HIPTechnicalErrorResponse, HIPTechnicalError, HIPWrappedErrorResponse, HIPWrappedError}
import play.api.http.Status._
import play.api.libs.json._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.LoggerUtil

import scala.util.Try

object HIPPenaltyDetailsHttpParser extends LoggerUtil {

  type HIPPenaltyDetailsResponse = Either[Error, PenaltyDetails]

  implicit object HIPPenaltyDetailsReads extends HttpReads[HIPPenaltyDetailsResponse] {
    override def read(method: String, url: String, response: HttpResponse): HIPPenaltyDetailsResponse = {
      response.status match {
        case OK =>
          parseSuccessResponse(response.json)
        case NOT_FOUND if response.body.nonEmpty =>
          parseNotFoundResponse(response.json)
        case NOT_FOUND =>
          logger.debug("[HIPPenaltyDetailsReads][read] NOT_FOUND with empty body")
          Left(Error(NOT_FOUND, "No penalty details found"))
        case NO_CONTENT =>
          logger.info("[HIPPenaltyDetailsReads][read] Received no content from HIP call")
          Left(Error(NOT_FOUND, "No penalty details found"))
        case status @ (BAD_REQUEST | FORBIDDEN | CONFLICT | UNPROCESSABLE_ENTITY | INTERNAL_SERVER_ERROR | SERVICE_UNAVAILABLE) =>
          logger.error(s"[HIPPenaltyDetailsReads][read] Received $status when trying to call HIP PenaltyDetails - with body: ${response.body}")
          parseErrorResponse(response)
        case status =>
          logger.error(s"[HIPPenaltyDetailsReads][read] Received unexpected response from HIP PenaltyDetails, status code: $status and body: ${response.body}")
          Left(Error(status, response.body))
      }
    }
  }

  private def parseSuccessResponse(json: JsValue): Either[Error, PenaltyDetails] = {
    json.validate[HIPSuccessResponse].fold(
      errors => {
        logger.warn(s"[HIPPenaltyDetailsHttpParser][parseSuccessResponse] Error parsing HIP success response: $errors")
        Left(Error(BAD_REQUEST, "UNEXPECTED_JSON_FORMAT - The downstream service responded with json which did not match the expected format."))
      },
      hipResponse => {
        logger.debug(s"[HIPPenaltyDetailsHttpParser][parseSuccessResponse] Parsed HIP response: $hipResponse")
        val penaltyDetails = transformToPenaltyDetails(hipResponse)
        logger.debug(s"[HIPPenaltyDetailsHttpParser][parseSuccessResponse] Transformed to PenaltyDetails: $penaltyDetails")
        Right(penaltyDetails)
      }
    )
  }

  private def parseNotFoundResponse(json: JsValue): Either[Error, PenaltyDetails] = {
    Try {
      json.validate[HIPErrorResponse].asOpt match {
        case Some(errorResponse) if errorResponse.errors.code == "016" =>
          logger.debug("[HIPPenaltyDetailsHttpParser][parseNotFoundResponse] Invalid ID Number - treating as no data found")
          Left(Error(NOT_FOUND, "No penalty details found"))
        case _ =>
          logger.error(s"[HIPPenaltyDetailsHttpParser][parseNotFoundResponse] Unable to parse 404 body: $json")
          Left(Error(NOT_FOUND, json.toString))
      }
    }.recover { case ex =>
      logger.error(s"[HIPPenaltyDetailsHttpParser][parseNotFoundResponse] Error parsing 404 response: ${ex.getMessage}")
      Left(Error(NOT_FOUND, json.toString))
    }.get
  }

  private def parseErrorResponse(response: HttpResponse): Either[Error, PenaltyDetails] = {
    val json = Try(response.json).getOrElse(Json.obj())

    val technicalError = json.validate[HIPTechnicalErrorResponse].asOpt
    val businessError = json.validate[HIPErrorResponse].asOpt  
    val wrappedError = json.validate[HIPWrappedErrorResponse].asOpt

    (technicalError, businessError, wrappedError) match {
      case (Some(techError), _, _) =>
        logger.warn(s"[HIPPenaltyDetailsHttpParser][parseErrorResponse] Technical error: ${techError.error.code} - ${techError.error.message}")
        Left(Error(response.status, techError.error.message))
      case (_, Some(bizError), _) =>
        logger.warn(s"[HIPPenaltyDetailsHttpParser][parseErrorResponse] Business error: ${bizError.errors.code} - ${bizError.errors.text}")
        Left(Error(response.status, bizError.errors.text))
      case (_, _, Some(wrapError)) =>
        val errorMessage = wrapError.response.map(_.reason).mkString(", ")
        logger.warn(s"[HIPPenaltyDetailsHttpParser][parseErrorResponse] HIP wrapped errors: $errorMessage")
        Left(Error(response.status, errorMessage))
      case (None, None, None) =>
        logger.error("[HIPPenaltyDetailsHttpParser][parseErrorResponse] No recognizable error structure found")
        Left(Error(response.status, response.body))
    }
  }

  private def transformToPenaltyDetails(hipResponse: HIPSuccessResponse): PenaltyDetails = {
    val penaltyData = hipResponse.success.penaltyData

    val lppDetails = penaltyData.flatMap(_.lpp).flatMap(_.lppDetails)
    val breathingSpace = penaltyData.flatMap(_.breathingSpace)

    logger.debug(s"[HIPPenaltyDetailsHttpParser][transformToPenaltyDetails] LPP details: $lppDetails")
    logger.debug(s"[HIPPenaltyDetailsHttpParser][transformToPenaltyDetails] Breathing space: $breathingSpace")

    PenaltyDetails(
      LPPDetails = lppDetails,
      breathingSpace = breathingSpace
    )
  }
}
