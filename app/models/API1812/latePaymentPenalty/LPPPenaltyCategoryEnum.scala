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

package models.API1812.latePaymentPenalty

import play.api.libs.json.{Format, JsError, JsResult, JsString, JsSuccess, JsValue}

object LPPPenaltyCategoryEnum extends Enumeration {

  val firstPenalty :LPPPenaltyCategoryEnum.Value = Value("LPP1")
  val secondPenalty :LPPPenaltyCategoryEnum.Value = Value("LPP2")

  implicit val format: Format[LPPPenaltyCategoryEnum.Value] = new Format[LPPPenaltyCategoryEnum.Value] {
    override def writes(o: LPPPenaltyCategoryEnum.Value): JsValue = {
      JsString(o.toString.toUpperCase)
    }

    override def reads(json: JsValue): JsResult[LPPPenaltyCategoryEnum.Value] = {
      json.as[String].toUpperCase match {
        case "LPP1" => JsSuccess(firstPenalty)
        case "LPP2" => JsSuccess(secondPenalty)
        case e => JsError(s"$e not recognised")
      }
    }
  }

}
