import BuildHelper._

inThisBuild(
  List(
    organization := "dev.zio",
    homepage     := Some(url("https://github.com/zio/zio-nio/")),
    licenses     := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer("jdegoes", "John De Goes", "john@degoes.net", url("http://degoes.net"))
    )
  )
)

addCommandAlias("fix", "; all compile:scalafix test:scalafix; all scalafmtSbt scalafmtAll")
addCommandAlias("check", "; scalafmtSbtCheck; scalafmtCheckAll; Compile / scalafix --check; Test / scalafix --check")
addCommandAlias("coverageReport", "clean coverage test coverageReport coverageAggregate")
addCommandAlias(
  "testDotty",
  ";zioNio/test;examples/test"
)

val zioVersion = "1.0.14"

lazy val zioNio = project
  .in(file("nio"))
  .settings(stdSettings("zio-nio"))
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio"                %% "zio"                     % zioVersion,
      "dev.zio"                %% "zio-streams"             % zioVersion,
      "org.scala-lang.modules" %% "scala-collection-compat" % "2.7.0",
      "dev.zio"                %% "zio-test"                % zioVersion % Test,
      "dev.zio"                %% "zio-test-sbt"            % zioVersion % Test
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )
  .settings(dottySettings)

lazy val docs = project
  .in(file("zio-nio-docs"))
  .settings(
    publish / skip := true,
    moduleName     := "zio-nio-docs",
    scalacOptions -= "-Yno-imports",
    scalacOptions -= "-Xfatal-warnings",
    ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(zioNio),
    ScalaUnidoc / unidoc / target              := (LocalRootProject / baseDirectory).value / "website" / "static" / "api",
    cleanFiles += (ScalaUnidoc / unidoc / target).value,
    docusaurusCreateSite     := docusaurusCreateSite.dependsOn(Compile / unidoc).value,
    docusaurusPublishGhpages := docusaurusPublishGhpages.dependsOn(Compile / unidoc).value
  )
  .dependsOn(zioNio)
  .enablePlugins(MdocPlugin, DocusaurusPlugin, ScalaUnidocPlugin)

lazy val examples = project
  .in(file("examples"))
  .settings(stdSettings("examples"))
  .settings(
    publish / skip := true,
    moduleName     := "examples"
  )
  .settings(dottySettings)
  .dependsOn(zioNio)
