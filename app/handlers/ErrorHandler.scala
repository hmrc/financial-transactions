/*
 * Copyright 2017 HM Revenue & Customs
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

package handlers

import javax.inject.{Inject, Singleton}

import models.Error
import play.api.http.HttpErrorHandler
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND}
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import play.api.{Configuration, Logger}
import uk.gov.hmrc.auth.core.AuthorisationException
import uk.gov.hmrc.http._
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.config.{AppName, HttpAuditEvent}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Based on uk.gov.hmrc.play.bootstrap.http.JsonErrorHandler
  * Custom Implementation has been provided to ensure the Response Format is always of Error or MultiError format.
  */

@Singleton
class ErrorHandler @Inject()(val configuration: Configuration, auditConnector: AuditConnector)
  extends HttpErrorHandler with HttpAuditEvent with AppName {

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {

    implicit val headerCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    statusCode match {
      case play.mvc.Http.Status.NOT_FOUND =>
        auditConnector.sendEvent(dataEvent("ResourceNotFound", "Resource Endpoint Not Found", request))
        Future.successful(NotFound(Json.toJson(Error("NOT_FOUND", s"URI '${Some(request.path).get}' not found"))))
      case play.mvc.Http.Status.BAD_REQUEST =>
        auditConnector.sendEvent(dataEvent("ServerValidationError", "Request bad format exception", request))
        Future.successful(BadRequest(Json.toJson(Error("BAD_REQUEST", s"Bad Request. Message: '$message'"))))
      case _ =>
        auditConnector.sendEvent(dataEvent("ClientError", s"A client error occurred, status: $statusCode", request))
        Future.successful(Status(statusCode)(Json.toJson(Error(statusCode.toString, message))))
    }
  }

  override def onServerError(request: RequestHeader, ex: Throwable): Future[Result] = {
    implicit val headerCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    Logger.error(s"! Internal server error, for (${request.method}) [${request.uri}] -> ", ex)

    val code = ex match {
      case e: NotFoundException => "ResourceNotFound"
      case e: AuthorisationException => "ClientError"
      case jsError: JsValidationException => "ServerValidationError"
      case _ => "ServerInternalError"
    }

    auditConnector.sendEvent(dataEvent(code, "Unexpected error", request, Map("transactionFailureReason" -> ex.getMessage)))
    Future.successful(resolveError(ex))
  }

  private def resolveError(ex: Throwable): Result = {
    val errorResponse = ex match {
      case e: AuthorisationException => Error(play.mvc.Http.Status.UNAUTHORIZED.toString, e.getMessage)
      case e: HttpException => Error(e.responseCode.toString, e.getMessage)
      case e: Upstream4xxResponse => Error(e.reportAs.toString, e.getMessage)
      case e: Upstream5xxResponse => Error(e.reportAs.toString, e.getMessage)
      case e: Throwable => Error(INTERNAL_SERVER_ERROR.toString, e.getMessage)
    }

    new Status(errorResponse.code.toInt)(Json.toJson(errorResponse))
  }
}
