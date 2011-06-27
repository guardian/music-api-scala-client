#Scala Music API Client

This project is a Scala wrapper around several public music web APIs.

##Dependencies

###SBT (0.7.x)

	val guardianSnapshot = "Guardian GitHub Snapshot" at "http://guardian.github.com/maven/repo-snapshots"
  	val musicApis = "com.gu.arts" %% "lastfm-api_2.8.1" % "0.1.X-SNAPSHOT"

##Example

Checkout the project, demo, to see how to call the companion objects for the case class wrappers.	

#Publishing

To publish to your local Maven/IVY repository, when in sbt (using ./sbt10) run the command
    reload
    clean
    compile

    publish-local

The newly created package will be available.  Update your dependent projects accordingly.

To publish to the Guardian Github repository, ensure you have cloned the [Github repository](https://github.com/guardian/guardian.github.com) into your home directory like so:

    cd ~ && git clone git@github.com:guardian/guardian.github.com.git

Then once again in sbt (using ./sbt10) run the command

    publish

This will copy the packages to the directory

    ~/guardian.github.com

Then change directories to here and add/commit accordingly.  You will receive an email from Github when the pages have been launched.  You will then be able to reference from your dependent projects.