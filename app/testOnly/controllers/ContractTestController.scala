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

package testOnly.controllers

import play.api.mvc.{Action, AnyContent, ControllerComponents}
import testOnly.connectors.ContractTest1811Connector
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ContractTestController @Inject()(connector: ContractTest1811Connector,
                                       cc: ControllerComponents)
                                      (implicit ec: ExecutionContext) extends BackendController(cc) {

  def call1811(url: String): Action[AnyContent] = Action.async {
    implicit request => connector.getFinancialData(url).map { result =>
      Status(result.status)(result.body)
    }
  }
}
