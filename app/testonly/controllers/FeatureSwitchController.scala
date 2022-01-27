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

package testonly.controllers

import config.AppConfig
import config.featureSwitch.FeatureSwitchModel
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject

class FeatureSwitchController @Inject()(appConfig: AppConfig, cc: ControllerComponents) extends BackendController(cc) {

  def get: Action[AnyContent] = Action { _ => result }

  def update: Action[FeatureSwitchModel] = Action(parse.json[FeatureSwitchModel]) { req =>
    appConfig.features.useApi1811(req.body.useApi1811)
    result
  }

  def result: Result = {
    Ok(Json.toJson(FeatureSwitchModel(
      useApi1811 = appConfig.features.useApi1811()
    )))
  }

}
