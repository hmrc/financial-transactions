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

package config.featureSwitch

import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration

class FeaturesSpec extends PlaySpec with GuiceOneAppPerSuite with BeforeAndAfterEach {

  private val features = new Features()(app.injector.instanceOf[Configuration])

  override def beforeEach(): Unit = {
    super.beforeEach()
    features.useApi1811(false)
    features.includePenAndIntCharges(true)
    features.staticDate(true)
  }

  "A feature" should {

    "return its current state" in {
      features.useApi1811() mustBe false
      features.includePenAndIntCharges() mustBe true
      features.staticDate() mustBe true
    }

    "switch to a new state" in {
      features.useApi1811(true)
      features.includePenAndIntCharges(false)
      features.staticDate(false)
      features.useApi1811() mustBe true
      features.includePenAndIntCharges() mustBe false
      features.staticDate() mustBe false
    }
  }

}
