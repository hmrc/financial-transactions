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

package config

import config.featureSwitch.Features

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.time.LocalDate
import java.util.Base64

trait AppConfig {
  val features: Features
  val staticDateValue: String
  val familiarisationPeriodEndDate: LocalDate
}

@Singleton
class MicroserviceAppConfig @Inject()(val servicesConfig: ServicesConfig)(implicit val conf: Configuration) extends AppConfig {

  lazy val appName: String = servicesConfig.getString("appName")

  lazy val desEnvironment: String = servicesConfig.getString("microservice.services.des.environment")
  lazy val desToken: String = servicesConfig.getString("microservice.services.des.auth-token")
  lazy val desUrl: String = servicesConfig.getString("microservice.services.des.url")

  lazy val eisEnvironment: String = servicesConfig.getString("microservice.services.eis.environment")
  lazy val eisToken: String = servicesConfig.getString("microservice.services.eis.auth-token")
  lazy val eisUrl: String = servicesConfig.getString("microservice.services.eis.url")

  lazy val hipUrl: String = servicesConfig.getString("microservice.services.hip.url")
  lazy val clientIdV1: String = servicesConfig.getString("microservice.services.hip.client-id")
  lazy val secretV1: String   = servicesConfig.getString("microservice.services.hip.client-secret")
  lazy val hipToken: String = Base64.getEncoder.encodeToString(s"$clientIdV1:$secretV1".getBytes("UTF-8"))

  override lazy val staticDateValue: String = servicesConfig.getString("date-service.staticDateValue")
  override lazy val familiarisationPeriodEndDate: LocalDate =
    LocalDate.parse(servicesConfig.getString("date-service.familiarisationPeriodEndDate"))

  override val features = new Features
}
