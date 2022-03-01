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

package models.API1812

import models.API1812.latePaymentPenalty.LatePaymentPenalty
import models.API1812.lateSubmissionPenalty.LateSubmissionPenalty
import play.api.libs.json.{Format, JsResult, JsValue, Json}
import utils.JsonUtils


case class PenaltyDetails(lateSubmissionPenalty: Option[LateSubmissionPenalty],
                          latePaymentPenalty: Option[Seq[LatePaymentPenalty]])

object PenaltyDetails extends JsonUtils  {
  implicit val format: Format[PenaltyDetails] = new Format[PenaltyDetails] {
    override def reads(json: JsValue): JsResult[PenaltyDetails] = {
      for {
        latePaymentPenalty <- (json \ "latePaymentPenalty" \ "details").validateOpt[Seq[LatePaymentPenalty]]
        lateSubmissionPenalty <- (json \ "lateSubmissionPenalty").validateOpt[LateSubmissionPenalty]
      } yield {
        PenaltyDetails(lateSubmissionPenalty, latePaymentPenalty)
      }
    }

    override def writes(details: PenaltyDetails): JsValue = {
      jsonObjNoNulls(
        "lateSubmissionPenalty" -> details.lateSubmissionPenalty,
        if(details.latePaymentPenalty.isDefined) {
          "latePaymentPenalty" -> Json.obj("details" -> details.latePaymentPenalty)
        } else {
          "latePaymentPenalty" -> Json.obj()
        }
      )
    }
  }
}