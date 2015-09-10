import AssemblyPlugin._
import com.github.retronym.SbtOneJar._


oneJarSettings

name := "storm_start_template"

version := "1.0"

scalaVersion := "2.10.4"

scalacOptions += "-Yresolve-term-conflict:package"

assemblyMergeStrategy in assembly := {
  case PathList("com",xs @ _*)=>MergeStrategy.first
  case PathList("io",xs @ _*)=>MergeStrategy.first

  case x if Assembly.isConfigFile(x) =>
    MergeStrategy.concat
  case PathList(ps @ _*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) =>
    MergeStrategy.rename
  case PathList("META-INF", xs @ _*) =>
    (xs map {_.toLowerCase}) match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
        MergeStrategy.discard
      case ps @ (x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard
      case "plexus" :: xs =>
        MergeStrategy.discard
      case "services" :: xs =>
        MergeStrategy.filterDistinctLines
      case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.first
    }
  case _ => MergeStrategy.first
}

mainClass in assembly := Some("cn.changhong.storm.wordcount.Start")

libraryDependencies += "org.apache.storm" % "storm-core" % "0.9.5" % "provided" withSources() withJavadoc()

libraryDependencies += "org.apache.storm" % "storm-kafka" % "0.9.5"  withSources() withJavadoc()

libraryDependencies += "org.apache.kafka" % "kafka_2.10" % "0.8.2.0" withSources() withJavadoc()

libraryDependencies += "net.liftweb" % "lift-json_2.10" % "3.0-M1" withSources() withJavadoc()

libraryDependencies += "com.typesafe.slick" % "slick-codegen_2.10" % "2.1.0" withSources() withJavadoc()

libraryDependencies += "com.typesafe.slick" % "slick_2.10" % "2.1.0" withSources() withJavadoc()

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.33"

libraryDependencies += "commons-dbcp" % "commons-dbcp" % "1.4" withSources() withJavadoc()

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.10.4" withSources() withJavadoc()

libraryDependencies += "com.twitter" % "finagle-redis_2.10" % "6.22.0" withSources() withJavadoc()

libraryDependencies += "redis.clients" % "jedis" % "2.6.1" withSources() withJavadoc()

libraryDependencies += "org.apache.commons" % "commons-pool2" % "2.0" withSources() withJavadoc()

libraryDependencies += "org.mongodb" % "casbah_2.10" % "2.8.0-RC2"

libraryDependencies += "nl.razko" %% "scraper" % "0.4.1" withSources() withJavadoc()

libraryDependencies += "io.netty" % "netty-all" % "4.0.31.Final" withSources() withJavadoc()

libraryDependencies += "org.apache.thrift" % "libthrift" % "0.9.1" withSources() withJavadoc()

fork := true

resolvers += "clojars" at "https://clojars.org/repo"

