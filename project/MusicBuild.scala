import sbt._

object MusicBuild extends Build {

  lazy val root = Project("root", file(".")) aggregate(lastfm, demo)
  lazy val lastfm = Project("lastfm-api", file("lastfm-api"))
  lazy val demo = Project("demo", file("demo")) dependsOn (lastfm)

}