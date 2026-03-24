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

package connectors.httpParsers

import models.{Error, ErrorResponse, InvalidJsonResponse, MultiError, UnexpectedJsonFormat}
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.libs.json.Json
import play.shaded.ahc.io.netty.handler.codec.http.HttpResponseStatus
import uk.gov.hmrc.http.HttpResponse
import utils.LoggerUtil

import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex

trait ResponseHttpParsers extends LoggerUtil {

  type HttpGetResult[T] = Either[ErrorResponse, T]

  protected def handleErrorResponse(httpResponse: HttpResponse): Left[ErrorResponse, Nothing] = {
    val status = httpResponse.status
    val body   = Option(httpResponse.body).getOrElse("").trim
    logger.info(s"[ResponseHttpParsers][handleErrorResponse] status=$status, Body received: $body")
    val errorResponse = classifyBody(body) match {

      case EmptyBody =>
        ErrorResponse(status, Error("EMPTY_RESPONSE", "Empty response body"))

      case JsonBody =>
        parseJson(body, status)

      case XmlFault =>
        parseXml(body, status)

      case HtmlBody =>
        ErrorResponse(status, Error("GATEWAY_ERROR", "Received HTML response instead of expected JSON"))

      case Unknown =>
        ErrorResponse(status, Error("UNKNOWN_FORMAT", body.take(INTERNAL_SERVER_ERROR)))
    }

    Left(errorResponse)
  }

  private sealed trait BodyType
  private case object EmptyBody extends BodyType
  private case object JsonBody  extends BodyType
  private case object XmlFault  extends BodyType
  private case object HtmlBody  extends BodyType
  private case object Unknown   extends BodyType

  private def classifyBody(body: String): BodyType = body match {
    case b if b.isEmpty                       => EmptyBody
    case b if b.startsWith("{") || b.startsWith("[") => JsonBody
    case b if b.contains("<am:fault")         => XmlFault
    case b if b.toLowerCase.contains("<html") => HtmlBody
    case _                                    => Unknown
  }

  private def parseJson(body: String, status: Int): ErrorResponse =
    Try(Json.parse(body)) match {

      case Success(json) =>
        json.asOpt[MultiError]
          .orElse(json.asOpt[Error]) match {

          case Some(err) => ErrorResponse(status, err)

          case None =>
            logger.warn("[ResponseHttpParsers] Unexpected JSON format")
            UnexpectedJsonFormat
        }

      case Failure(ex) =>
        logger.warn(s"[ResponseHttpParsers] JSON parsing failed: ${ex.getMessage}")
        InvalidJsonResponse
    }

  private def parseXml(body: String, status: Int): ErrorResponse = {
    val message     = extractTag(body, "am:message")
    val description = extractTag(body, "am:description")

    val combined = List(message, description).flatten.mkString(" - ")

    val errorType = combined.toLowerCase match {
      case msg if msg.contains("timeout") => "TIMEOUT"
      case _                              => "BACKEND_FAULT"
    }

    ErrorResponse(status, Error(errorType, combined match {
      case "" => "XML fault received"
      case m  => m
    }))
  }

  private def extractTag(xml: String, tag: String): Option[String] = {
    val pattern: Regex = s"<$tag>(.*?)</$tag>".r
    pattern.findFirstMatchIn(xml).map(_.group(1))
  }
}
