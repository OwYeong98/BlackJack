name := "myapp"

 version := "1.3.3"

 scalaVersion := "2.12.3"


// https://mvnrepository.com/artifact/org.scalafx/scalafx
libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.144-R12"

// https://mvnrepository.com/artifact/org.scalafx/scalafxml-core-sfx8 
libraryDependencies += "org.scalafx" %% "scalafxml-core-sfx8" % "0.4" 

// https://mvnrepository.com/artifact/mysql/mysql-connector-java
libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.13"

// Scala 2.10, 2.11, 2.12
libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc"               % "2.5.2",
  "com.h2database"  %  "h2"                        % "1.4.199",
  "ch.qos.logback"  %  "logback-classic"           % "1.2.3"
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

fork:= true

connectInput in run := true