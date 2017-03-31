/**
 * Created by walter on 3/9/17.
 */

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Arrays;


// http://spark.apache.org/docs/latest/sql-programming-guide.html#creating-datasets
public class TrimJ implements Serializable {

    public void writeFastq(JavaRDD rdd, String outFilepath, SparkSession spark) throws AnalysisException {
        Dataset<Row> ds = spark.createDataFrame(rdd, Read.class);
        ds.createTempView("ds");
        spark.sqlContext().sql("SELECT CONCAT(name1, '\n', seq, '\n', name2, '\n', phred, '') FROM ds")
                .write().text(outFilepath);
    }

    // http://stackoverflow.com/questions/31834825/iterate-through-a-java-rdd-by-row
    // https://github.com/yhemanth/spark-samples/blob/master/src/main/java/com/dsinpractice/spark/samples/core/WholeTextFiles.java
    public JavaRDD<Read> readFastq(String fastqFilepath, JavaSparkContext spark) {
        JavaPairRDD<String, String> fileNameContentsRDD = spark.wholeTextFiles(fastqFilepath);
        JavaRDD fastqRaw = fileNameContentsRDD
                .map(fileNameContent -> Arrays.toString(fileNameContent._2().split("\n@")));
        JavaRDD fastq = fastqRaw.map(
                reads_info -> {
                    // read_info below contains the entire file, not a block of 4 lines...
                    String[] read_info = reads_info.toString().split("\n");
                    System.out.println(Arrays.toString(read_info));
                    return new Read(read_info[0], read_info[1], read_info[2], read_info[3]);
                });
        fastq.saveAsTextFile("/home/wmblair/IdeaProjects/LearningSpark/outputFile/fastq");
        return fastq;
    }

    public JavaRDD trimFastq(JavaRDD<Read> rdd, SparkSession spark) {
        char[] badScoresArray = {'!', '\"', '#', '$', '%', '&'};
        return rdd
                .map(row -> {
                    return RowFactory.create(
                            row.getName1(),
                            trailingTrim(row.getSeq().toString(), row.getPhred().toString(), badScoresArray),
                            row.getName2(),
                            row.getPhred());
                });
    }

    public String leadingTrim(String seq, String phred, char[] badScoresArray) {
        int highestBadScoreIndex = 0;
        for (char score : badScoresArray) {
            highestBadScoreIndex = Math.max(highestBadScoreIndex, phred.lastIndexOf(score));
        }
        String blanks = new String(new char[highestBadScoreIndex]).replace("\0", " ");
        return blanks + seq.substring(highestBadScoreIndex+1, seq.length());
    }

    public String trailingTrim(String seq, String phred, char[] badScoresArray) {
        int lowestBadScoreIndex = seq.length();
        int currIndex = -1;
        for (char score : badScoresArray) {
            currIndex = phred.indexOf(score);
            lowestBadScoreIndex = ((currIndex > 0) ? Math.min(lowestBadScoreIndex, currIndex) : lowestBadScoreIndex);
        }
        return seq.substring(0, lowestBadScoreIndex);
    }
}

