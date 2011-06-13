import java.util.jar._

name := "lastfm-api"

organization := "com.gu.arts"

version := "0.1.1-SNAPSHOT"

libraryDependencies += "joda-time" % "joda-time" % "1.6.2"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.6.0"

libraryDependencies += "net.liftweb" %% "lift-json" % "2.2"

libraryDependencies += "net.databinder" %% "dispatch-http" % "0.8.1"

packageOptions <+= (version, name) map { (v, n) =>
  Package.ManifestAttributes(
    Attributes.Name.IMPLEMENTATION_VERSION -> v,
    Attributes.Name.IMPLEMENTATION_TITLE -> n,
    Attributes.Name.IMPLEMENTATION_VENDOR -> "guardian.co.uk"
  )
}

publishTo <<= (version) { version: String =>
    val publishType = if (version.endsWith("SNAPSHOT")) "snapshots" else "releases"
    Some(
        Resolver.file(
            "guardian github " + publishType,
            file(System.getProperty("user.home") + "/guardian.github.com/maven/repo-" + publishType)
        )
    )
}
