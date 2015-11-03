import sbt._
import Keys._

object Dependency {
  val mysql = "mysql" % "mysql-connector-java" % "5.1.37"
  val joda = "joda-time" % "joda-time" % "2.9"
  val lang3 = "org.apache.commons" % "commons-lang3" % "3.4"
  val elsClient = "com.sksamuel.elastic4s" %% "elastic4s-core" % "1.7.4"
}