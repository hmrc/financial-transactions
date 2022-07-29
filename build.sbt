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

import play.core.PlayVersion
import sbt.Tests.{Group, SubProcess}
import uk.gov.hmrc.DefaultBuildSettings._
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

val appName: String = "financial-transactions"

lazy val appDependencies: Seq[ModuleID] = compile ++ test()
lazy val plugins: Seq[Plugins] = Seq.empty
lazy val playSettings: Seq[Setting[_]] = Seq.empty

lazy val coverageSettings: Seq[Setting[_]] = {
  import scoverage.ScoverageKeys

  val excludedPackages = Seq(
    "<empty>",
    "com.kenshoo.play.metrics.*",
    "Reverse.*",
    "app.*",
    "prod.*",
    "config.*",
    "testOnlyDoNotUseInAppConf.*"
  )

  Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 95,
    ScoverageKeys.coverageHighlighting := true,
    ScoverageKeys.coverageFailOnMinimum := true
  )
}

val compile = Seq(
  ws,
  "uk.gov.hmrc" %% "bootstrap-backend-play-28" % "5.24.0",
  "com.typesafe.play" %% "play-json-joda" % "2.9.2"
)

def test(scope: String = "test,it"): Seq[ModuleID] = Seq(
  "org.scalatest" %% "scalatest" % "3.1.4" % scope,
  "org.pegdown" % "pegdown" % "1.6.0" % scope,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % scope,
  "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
  "org.mockito" % "mockito-core" % "3.2.0" % scope,
  "com.github.tomakehurst" % "wiremock-jre8" % "2.26.3" % scope,
  "org.jsoup" % "jsoup" % "1.12.1" % scope,
  "com.vladsch.flexmark" % "flexmark-all" % "0.36.8" % scope,
  "org.scalamock"           %% "scalamock-scalatest-support"  % "3.6.0" % scope,
  "org.scalatestplus"      %% "scalatestplus-mockito"    % "1.0.0-M2" % scope
)

def oneForkedJvmPerTest(tests: Seq[TestDefinition]) = {
  tests.map { test =>
    Group(test.name, Seq(test), SubProcess(ForkOptions().withRunJVMOptions(Vector(s"-Dtest.name=${test.name}"))))
  }
}

lazy val microservice = Project(appName, file("."))
  .enablePlugins(Seq(play.sbt.PlayScala, SbtDistributablesPlugin) ++ plugins: _*)
  .settings(playSettings: _*)
  .settings(scalaSettings: _*)
  .settings(publishingSettings: _*)
  .settings(majorVersion := 0)
  .settings(coverageSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    PlayKeys.playDefaultPort := 9085,
    scalaVersion := "2.12.16",
    libraryDependencies ++= appDependencies,
    retrieveManaged := true,
    routesImport += "binders.FinancialTransactionsBinders._",
    routesImport += "binders.PenaltyDetailsBinders._",
    routesGenerator := InjectedRoutesGenerator
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    Keys.fork in IntegrationTest := false,
    unmanagedSourceDirectories in IntegrationTest := (baseDirectory in IntegrationTest) (base => Seq(base / "it")).value,
    addTestReportOption(IntegrationTest, "int-test-reports"),
    testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value),
    parallelExecution in IntegrationTest := false
  )
