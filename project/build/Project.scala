import sbt._
import java.io.File

class Project(info: ProjectInfo) extends ParentProject(info) {

  val robb1eGithub = "Robb1e Github Releases" at "http://robb1e.github.com/maven/repo-releases"
  val robb1eLocal = "Robb1e local releases" at "file:///" + Path.userHome + "/robb1e.github.com/maven/repo-releases"
  
  lazy val lastfm = project("lastfm-api", "lastfm-api", new LastFm(_))
  lazy val demo = project("demo", "demo", lastfm)

  class LastFm(info: ProjectInfo) extends DefaultProject(info) with PublishSources {
    val joda = "joda-time" % "joda-time" % "1.6.2"
    val slf = "org.slf4j" % "slf4j-api" % "1.6.0"
    val liftjson = "net.liftweb" %% "lift-json" % "2.2"
    val dispatch_http = "net.databinder" %% "dispatch-http" % "0.8.1"   
  }
  
  val publishTo =
    if (projectVersion.value.toString.contains("-SNAPSHOT"))
      Resolver.file("robb1e github snapshots", new File(System.getProperty("user.home")
            + "/robb1e.github.com/maven/repo-snapshots"))
    else
      Resolver.file("robb1e github releases", new File(System.getProperty("user.home")
            + "/robb1e.github.com/maven/repo-releases"))
}

trait PublishSources extends BasicScalaProject with BasicPackagePaths {
  lazy val sourceArtifact = Artifact.sources(artifactID)
  override def managedStyle = ManagedStyle.Maven
  override def packageSrcJar = defaultJarPath("-sources.jar")
  override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageSrc)
}
