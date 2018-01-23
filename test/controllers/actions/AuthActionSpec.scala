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

package controllers.actions

import base.SpecBase
import play.api.mvc.{Action, AnyContent, Controller}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class AuthActionSpec extends SpecBase {

  class Harness(authAction: AuthAction) extends Controller {
    def someAction(): Action[AnyContent] = authAction { _ => Ok }
  }

  "Auth Action" when {
    "the user hasn't logged in" must {
      "redirect the user to log in " in {
        val authAction = new AuthActionImpl(new FakeFailingAuthConnector(new MissingBearerToken))
        val controller = new Harness(authAction)
        val result = controller.someAction()(fakeRequest)
        status(result) shouldBe UNAUTHORIZED
      }
    }

    "the user's session has expired" must {
      "redirect the user to log in " in {
        val authAction = new AuthActionImpl(new FakeFailingAuthConnector(new BearerTokenExpired))
        val controller = new Harness(authAction)
        val result = controller.someAction()(fakeRequest)
        status(result) shouldBe UNAUTHORIZED
      }
    }

    "the user doesn't have sufficient enrolments" must {
      "redirect the user to the unauthorised page" in {
        val authAction = new AuthActionImpl(new FakeFailingAuthConnector(new InsufficientEnrolments))
        val controller = new Harness(authAction)
        val result = controller.someAction()(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "the user doesn't have sufficient confidence level" must {
      "redirect the user to the unauthorised page" in {
        val authAction = new AuthActionImpl(new FakeFailingAuthConnector(new InsufficientConfidenceLevel))
        val controller = new Harness(authAction)
        val result = controller.someAction()(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "the user used an unaccepted auth provider" must {
      "redirect the user to the unauthorised page" in {
        val authAction = new AuthActionImpl(new FakeFailingAuthConnector(new UnsupportedAuthProvider))
        val controller = new Harness(authAction)
        val result = controller.someAction()(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "the user has an unsupported affinity group" must {
      "redirect the user to the unauthorised page" in {
        val authAction = new AuthActionImpl(new FakeFailingAuthConnector(new UnsupportedAffinityGroup))
        val controller = new Harness(authAction)
        val result = controller.someAction()(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "the user has an unsupported credential role" must {
      "redirect the user to the unauthorised page" in {
        val authAction = new AuthActionImpl(new FakeFailingAuthConnector(new UnsupportedCredentialRole))
        val controller = new Harness(authAction)
        val result = controller.someAction()(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }
  }
}

class FakeFailingAuthConnector(exceptionToReturn: Throwable) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.failed(exceptionToReturn)
}
