name := "LearningSpark"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.0"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.1.0"

libraryDependencies += "org.apache.spark" %% "spark-hive" % "2.1.0"

// ADAM .....

//libraryDependencies += "org.bdgenomics.adam" % "adam-core_2.10" % "0.17.1"
//
//libraryDependencies += "org.bdgenomics.bdg-utils" % "bdg-utils-misc" % "0.1.2"
//
//libraryDependencies += "org.bdgenomics.bdg-formats" % "bdg-formats" % "0.6.1"
//
//libraryDependencies += "org.bdgenomics.utils" % "utils-misc_2.10" % "0.2.3"
//
//libraryDependencies += "org.bdgenomics.utils" % "utils-io_2.10" % "0.2.3"
//
//libraryDependencies += "org.bdgenomics.utils" % "utils-cli_2.10" % "0.2.3"
//
//libraryDependencies += "org.bdgenomics.utils" % "utils-metrics_2.10" % "0.2.3"
//
//libraryDependencies += "org.bdgenomics.adam" % "adam-apis_2.10" % "0.17.1"
//
//libraryDependencies += "org.bdgenomics.utils" % "utils-serialization_2.10" % "0.2.3"
//
//libraryDependencies += "org.bdgenomics.bdg-utils" % "bdg-utils-parquet" % "0.1.1"