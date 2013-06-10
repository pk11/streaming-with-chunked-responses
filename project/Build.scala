import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {
    
    val appName         = "stream-demo"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "com.lambdaworks" % "lettuce" % "2.3.3"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
    )

}
