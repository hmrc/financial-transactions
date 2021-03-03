/*
 * Copyright 2021 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

trait AppConfig {
  val desEnvironment: String
  val desToken: String
  val desUrl: String
}

@Singleton
class MicroserviceAppConfig @Inject()(val environment: Environment, val conf: Configuration, servicesConfig: ServicesConfig) extends AppConfig {

  private def loadConfig(key: String) = servicesConfig.getString(key)

  lazy val appName: String = loadConfig("appName")

  lazy val desEnvironment: String = servicesConfig.getString("microservice.services.des.environment")
  lazy val desToken: String = servicesConfig.getString("microservice.services.des.auth-token")
  lazy val desUrl: String = servicesConfig.getString("microservice.services.des.url")

}
