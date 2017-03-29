/**
  * Created by walter on 2/24/17.
  */

import org.apache.avro.generic.GenericData.StringType
import org.apache.calcite.avatica.ColumnMetaData.StructType
import org.apache.commons.math3.analysis.function.Max
import org.apache.spark.SparkContext
import org.apache.spark.sql.{Dataset, SparkSession}

import scala.collection.mutable.ListBuffer
import scala.io.Source

//import scala.collection.mutable.ArrayBuffer
//import scala.io.Source

object TrimS {
  // case class must be defined outside of scope
  case class Read(name1: String, seq: String, name2: String, phred: String)

  def main(args: Array[String]) {
    // Create SparkSession https://databricks.com/blog/2016/08/15/how-to-use-sparksession-in-apache-spark-2-0.html
    // https://spark.apache.org/docs/2.0.0/api/java/org/apache/spark/sql/SparkSession.html
    val spark = SparkSession
      .builder()
      .appName("Scala Trimmer")
      .master("local")    // for testing in IntelliJ
      .getOrCreate()


    val fastqFilepath = args(0)
    val ds = allTrims(readFastq(fastqFilepath, spark), spark)
    writeFastq(ds, "fastqTrimS.fastq", spark)

    // write out to parquet (can send straight to next step)
    ds.write.parquet("outputParquet/trimmedParquet")

  }

  def writeFastq(ds: Dataset[Read], outFilepath: String, spark: SparkSession) = {
    import spark.implicits._
    ds.map(

    ).toDF().toJavaRDD.saveAsTextFile(outFilepath)
  }

  def readFastq(fastqFilepath: String, spark: SparkSession): Dataset[Read] = {
    import spark.implicits._
    val fastqRaw = spark.sparkContext.wholeTextFiles(fastqFilepath).flatMap(x => x._2.split("\n@"))
    val ds = fastqRaw.map(
      reads_info => {
        val read_info = reads_info.split("\n")
        val seqRegex = """[A, T, G, C]""".r
        var name1 = ""
        var seq = ""
        var name2 =""
        var phred = ""
        for(x <- read_info) {
          if(x.toString().startsWith("@SRR") || x.toString().startsWith("SRR")){
            name1 = x.toString()
          } else if(seqRegex.findFirstIn(x.toString().substring(0, 1)) != None){
            seq = x.toString()
          } else if(x.toString().startsWith("+SRR")){
            name2 = x.toString()
          } else{
            phred = x.toString()
          }
        }
        (name1, seq, name2, phred)
      }
    ).toDF("name1", "seq", "name2", "phred").as[Read]
    return ds
  }

  def allTrims(ds: Dataset[Read], spark: SparkSession): Dataset[Read] = {
    import spark.implicits._
    //// Find index of the bad phred score and trim with that num
    //// http://stackoverflow.com/questions/15259250/in-scala-how-to-get-a-slice-of-a-list-from-nth-element-to-the-end-of-the-list-w
    val badScoresArray = Array('!', '\"', '#', '$', '%', '&')
    // Does using regex condition perform better?
    val badScoresRegex = """[!,",#,%,&]""".r
    ds
      .select("seq", "phred")
      //.map(row => if(badScoresRegex.findFirstIn(row.getString(1)) != None) leadingTrim(row.getString(0), row.getString(1), badScoresArray) else row.getString(0))
      .map(row => (row.getString(0), leadingTrim(row.getString(0), row.getString(1), badScoresArray), trailingTrim(row.getString(0), row.getString(1), badScoresArray)))
      .select($"_1".alias("original"), $"_2".alias("leading trim"), $"_3".alias("trailing trim"))
      .show(100, false)
    return ds
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
