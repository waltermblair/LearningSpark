$ sbt package  
$ spark-submit --class TrimS --master sparkmaster /absolute/path/to/Scalatrim/target/scala-2.11/learningspark_2.11-1.0.jar /absolute/path/to/Scalatrim/reads/fastqSmall.csv
