import sbt._

object MusicBuild extends Build {

  lazy val root = Project("music", file(".")) aggregate(apiBase, lastfm, musicMetric, demo) dependsOn(lastfm, musicMetric)
  lazy val apiBase = Project("music-api-base", file("api-base"))
  lazy val lastfm = Project("lastfm-api", file("lastfm-api")) dependsOn(apiBase)
  lazy val musicMetric = Project("musicmetric-api", file("musicmetric")) dependsOn(apiBase)
  lazy val demo = Project("demo", file("demo")) dependsOn (lastfm, musicMetric)

}