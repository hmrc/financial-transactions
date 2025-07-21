/*
 * Copyright 2025 HM Revenue & Customs
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
import models.hip_API1812.{HIPErrorResponse, HIPBusinessError, HIPTechnicalErrorResponse, HIPTechnicalError, HIPWrappedErrorResponse, HIPWrappedError, HIPOriginResponse, HIPFailureResponse, HIPFailure}
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
          logger.info("[HIPPenaltyDetailsReads][read] NOT_FOUND with empty body")
          Left(Error(NOT_FOUND, "No penalty details found"))
        case NO_CONTENT =>
          logger.info("[HIPPenaltyDetailsReads][read] Received no content from HIP call")
          Left(Error(NOT_FOUND, "No penalty details found"))
        case UNPROCESSABLE_ENTITY =>
          parse422Response(response)
        case status @ (BAD_REQUEST | FORBIDDEN | CONFLICT | INTERNAL_SERVER_ERROR | SERVICE_UNAVAILABLE) =>
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
        Left(Error(INTERNAL_SERVER_ERROR, "UNEXPECTED_JSON_FORMAT - The downstream service responded with json which did not match the expected format."))
      },
      hipResponse => {
        logger.info(s"[HIPPenaltyDetailsHttpParser][parseSuccessResponse] Parsed HIP response: $hipResponse")
        val penaltyDetails = transformToPenaltyDetails(hipResponse)
        logger.info(s"[HIPPenaltyDetailsHttpParser][parseSuccessResponse] Transformed to PenaltyDetails: $penaltyDetails")
        Right(penaltyDetails)
      }
    )
  }

  private def parseNotFoundResponse(json: JsValue): Either[Error, PenaltyDetails] = {
    logger.error(s"[HIPPenaltyDetailsHttpParser][parseNotFoundResponse] 404 - URL not found: $json")
    Left(Error(NOT_FOUND, "URL not found"))
  }

  private def parse422Response(response: HttpResponse): Either[Error, PenaltyDetails] = {
    val json = Try(response.json).getOrElse(Json.obj())
    
    json.validate[HIPErrorResponse].asOpt match {
      case Some(errorResponse) if errorResponse.errors.code == "016" =>
        logger.info("[HIPPenaltyDetailsHttpParser][parse422Response] Invalid ID Number (016) - treating as no data found")
        Left(Error(NOT_FOUND, "No penalty details found"))
      case _ =>
        logger.error(s"[HIPPenaltyDetailsHttpParser][parse422Response] 422 error: ${response.body}")
        parseErrorResponse(response)
    }
  }

  private def parseErrorResponse(response: HttpResponse): Either[Error, PenaltyDetails] = {
    val json = Try(response.json).getOrElse(Json.obj())

    val technicalError = json.validate[HIPTechnicalErrorResponse].asOpt
    val businessError = json.validate[HIPErrorResponse].asOpt  
    val wrappedError = json.validate[HIPWrappedErrorResponse].asOpt
    val doubleWrappedError = json.validate[HIPOriginResponse].asOpt

    (technicalError, businessError, wrappedError, doubleWrappedError) match {
      case (Some(techError), _, _, _) =>
        logger.warn(s"[HIPPenaltyDetailsHttpParser][parseErrorResponse] Technical error: ${techError.error.code} - ${techError.error.message}")
        Left(Error(response.status, techError.error.message))
      case (_, Some(bizError), _, _) =>
        logger.warn(s"[HIPPenaltyDetailsHttpParser][parseErrorResponse] Business error: ${bizError.errors.code} - ${bizError.errors.text}")
        Left(Error(response.status, bizError.errors.text))
      case (_, _, Some(wrapError), _) =>
        val errorMessage = wrapError.response.map(_.reason).mkString(", ")
        logger.warn(s"[HIPPenaltyDetailsHttpParser][parseErrorResponse] HIP wrapped errors: $errorMessage")
        Left(Error(response.status, errorMessage))
      case (_, _, _, Some(doubleWrap)) =>
        val errorMessage = doubleWrap.response.failures.headOption.map(_.reason).getOrElse(response.body)
        logger.warn(s"[HIPPenaltyDetailsHttpParser][parseErrorResponse] Double-wrapped HIP errors: $errorMessage")
        Left(Error(response.status, errorMessage))
      case (None, None, None, None) =>
        logger.error("[HIPPenaltyDetailsHttpParser][parseErrorResponse] No recognizable error structure found")
        Left(Error(response.status, response.body))
    }
  }

  private def transformToPenaltyDetails(hipResponse: HIPSuccessResponse): PenaltyDetails = {
    val penaltyData = hipResponse.success.penaltyData

    val lppDetails = penaltyData.flatMap(_.lpp).flatMap(_.lppDetails)
    val breathingSpace = penaltyData.flatMap(_.breathingSpace)

    logger.info(s"[HIPPenaltyDetailsHttpParser][transformToPenaltyDetails] LPP details: $lppDetails")
    logger.info(s"[HIPPenaltyDetailsHttpParser][transformToPenaltyDetails] Breathing space: $breathingSpace")

    PenaltyDetails(
      LPPDetails = lppDetails,
      breathingSpace = breathingSpace
    )
  }
}
