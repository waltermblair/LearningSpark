/**
  * Created by walter on 2/24/17.
  */

import org.apache.commons.math3.analysis.function.Max
import org.apache.spark.sql.SparkSession

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object TrimS {
  // case class must be defined outside of scope
  case class Read(name1: String, seq: String, name2: String, phred: String)

  def main(args: Array[String]) {
    // Create SparkSession https://databricks.com/blog/2016/08/15/how-to-use-sparksession-in-apache-spark-2-0.html
    // https://spark.apache.org/docs/2.0.0/api/java/org/apache/spark/sql/SparkSession.html
    val spark = SparkSession
      .builder()
      .appName("Scala Trimmer")
      .master("local")
      .getOrCreate()

    import spark.implicits._

    //http://alvinalexander.com/scala/scala-mutable-arrays-adding-elements-to-arrays
    var data = ArrayBuffer[Read]()

    //http://stackoverflow.com/questions/30863747/reading-a-text-file-in-scala-one-line-after-the-other-not-iterated
    for(line <- Source.fromFile("SRAdump.txt").getLines().grouped(4)) {
      //noinspection ZeroIndexToHead
      data += Read(line(0), line(1), line(2), line(3))
    }

    // converting mutable ArrayBuffer to RDD, then to DF, then to DS
    val ds = spark.sparkContext.makeRDD(data).toDF().as[Read]
    //// Find index of the bad phred score and trim with that num
    //// http://stackoverflow.com/questions/15259250/in-scala-how-to-get-a-slice-of-a-list-from-nth-element-to-the-end-of-the-list-w
    val badScoresArray = Array('!', '\"', '#', '$', '%', '&')
    // Does using regex condition perform better?
    //val badScoresRegex = """[!,",#,%,&]""".r
    ds
      .select("seq", "phred")
      //.map(row => if(badScoresRegex.findFirstIn(row.getString(1)) != None) leadingTrim(row.getString(0), row.getString(1), badScoresArray) else row.getString(0))
      .map(row => (row.getString(0), leadingTrim(row.getString(0), row.getString(1), badScoresArray), trailingTrim(row.getString(0), row.getString(1), badScoresArray)))
      .select($"_1".alias("original"), $"_2".alias("leading trim"), $"_3".alias("trailing trim"))
      .show(100, false)

    // write out to parquet (can send straight to next step)
    //ds.write.parquet("trimmedParquet")

    // configure spark-csv to write out to csv http://stackoverflow.com/questions/31937958/how-to-export-data-from-spark-sql-to-csv

  }

  def leadingTrim(seq: String, phred: String, badScoresArray: Array[Char]): String = {
    var highestBadScoreIndex = 0
    for (score <- badScoresArray) {
      highestBadScoreIndex = math.max(highestBadScoreIndex, phred.lastIndexOf(score))
    }
    return " " * highestBadScoreIndex + seq.drop(highestBadScoreIndex+1)
  }

  def trailingTrim(seq: String, phred: String, badScoresArray: Array[Char]): String = {
    var lowestBadScoreIndex = seq.length
    var currIndex = -1
    for (score <- badScoresArray) {
      currIndex = phred.indexOf(score)
      lowestBadScoreIndex = if(currIndex > 0) math.min(lowestBadScoreIndex, currIndex) else lowestBadScoreIndex
    }
    return seq.slice(0, lowestBadScoreIndex)
  }
}
