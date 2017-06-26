$ sbt package  
$ spark-submit --class TrimS --master sparkmaster --conf spark.eventLog.enabled=true /absolute/path/to/Scalatrim/target/scala-2.11/learningspark_2.11-1.0.jar /absolute/path/to/Scalatrim/reads/fastqSmall.fastq /path/to/Scalatrim/outputFile/fastqSmallTrimmed.fastq

Running locally within IntelliJ from local 1.6Gb fastq writes out and reads successful fastq and everything looks fine. I run out of memory running my local "cluster", but I think this is an issue with my configuration.

Next steps:

    Feed directly into ADAM
    Compare ADAM-direct versus write-out performance
    Write Java version
    Compare Scala versus Java performance
    Add more trims to get closer to Trimmomatic

