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

package audit

import javax.inject.{Inject, Singleton}
import audit.models.{AuditModel, ExtendedAuditModel}
import config.MicroserviceAppConfig
import org.joda.time.DateTime
import play.api.http.HeaderNames.REFERER
import play.api.libs.json.{JodaReads, JodaWrites, JsObject, JsValue, Json, Reads, Writes}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions
import uk.gov.hmrc.play.audit.http.connector.AuditResult.{Disabled, Failure, Success}
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector, AuditResult}
import uk.gov.hmrc.play.audit.model.{DataEvent, ExtendedDataEvent}
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuditingService @Inject()(appConfig: MicroserviceAppConfig, auditConnector: AuditConnector) extends LoggerUtil {

  implicit val dateTimeJsReader: Reads[DateTime] = JodaReads.jodaDateReads("yyyyMMddHHmmss")
  implicit val dateTimeWriter: Writes[DateTime] = JodaWrites.jodaDateWrites("dd/MM/yyyy HH:mm:ss")

  implicit val dataEventWrites: Writes[DataEvent] = Json.writes[DataEvent]
  implicit val extendedDataEventWrites: Writes[ExtendedDataEvent] = Json.writes[ExtendedDataEvent]

  def audit(auditModel: AuditModel)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AuditResult] = {
    val dataEvent = toDataEvent(appConfig.appName, auditModel, path)
    logger.debug(s"Splunk Audit Event:\n\n${Json.toJson(dataEvent)}")
    auditConnector.sendEvent(dataEvent).map {
      case Success =>
        logger.debug("Splunk Audit Successful")
        Success
      case Failure(err, _) =>
        logger.debug(s"Splunk Audit models.Error, message: $err")
        Failure(err)
      case Disabled =>
        logger.debug(s"Auditing Disabled")
        Disabled
    }
  }

  def audit(auditModel: ExtendedAuditModel)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AuditResult] = {
    val extendedDataEvent = toDataEvent(appConfig.appName, auditModel, path)
    logger.debug(s"Splunk Audit Event:\n\n${Json.toJson(extendedDataEvent)}")
    auditConnector.sendExtendedEvent(extendedDataEvent).map {
      case Success =>
        logger.debug("Splunk Audit Successful")
        Success
      case Failure(err, _) =>
        logger.debug(s"Splunk Audit models.Error, message: $err")
        Failure(err)
      case Disabled =>
        logger.debug(s"Auditing Disabled")
        Disabled
    }
  }

  def toDataEvent(appName: String, auditModel: AuditModel, path: String)(implicit hc: HeaderCarrier): DataEvent =
    DataEvent(
      auditSource = appName,
      auditType = auditModel.auditType,
      tags = AuditExtensions.auditHeaderCarrier(hc).toAuditTags(auditModel.transactionName, path),
      detail = AuditExtensions.auditHeaderCarrier(hc).toAuditDetails(auditModel.detail: _*)
    )

  def toDataEvent(appName: String, auditModel: ExtendedAuditModel, path: String)
                 (implicit hc: HeaderCarrier): ExtendedDataEvent = {

    val details: JsValue =
      Json.toJson(AuditExtensions.auditHeaderCarrier(hc).toAuditDetails()).as[JsObject].deepMerge(auditModel.detail.as[JsObject])

    ExtendedDataEvent(
      auditSource = appName,
      auditType = auditModel.auditType,
      tags = AuditExtensions.auditHeaderCarrier(hc).toAuditTags(auditModel.transactionName, path),
      detail = details
    )
  }

  private def path(implicit hc: HeaderCarrier) = hc.extraHeaders.find(_._1 == REFERER).map(_._2).getOrElse("-")
}
