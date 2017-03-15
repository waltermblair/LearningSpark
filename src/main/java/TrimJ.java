/**
 * Created by walter on 3/9/17.
 */

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.*;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.function.Function;
import java.util.Arrays;
import java.util.Collections;
import java.io.Serializable;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.*;

import java.util.ArrayList;

// http://spark.apache.org/docs/latest/sql-programming-guide.html#creating-datasets
public class TrimJ {
    public static void main(String[] args) throws FileNotFoundException {
        SparkSession spark = SparkSession
                .builder()
                .appName("Scala Trimmer")
                .master("local")
                .getOrCreate();

        ArrayList<Read> data = new ArrayList<Read>();

        Scanner s = new Scanner(new File("SRAdump.txt"));

        // http://spark.apache.org/docs/latest/sql-programming-guide.html#creating-datasets

        while(s.hasNext()) {
            Read read = new Read();
            read.setName1(s.nextLine());
            read.setSeq(s.nextLine());
            read.setName2(s.nextLine());
            read.setPhred(s.nextLine());
            data.add(read);
        }

        // https://spark.apache.org/docs/2.0.0/api/java/org/apache/spark/sql/Encoder.html
        Dataset<Read> ds = spark.createDataset(data, Encoders.bean(Read.class));
        char[] badScoresArray = {'!', '\"', '#', '$', '%', '&'};
        Dataset<String> dss = ds.map(row -> row.getName1(), Encoders.STRING());

    }
    public static String leadingTrim(String seq, String phred, char[] badScoresArray) {
        int highestBadScoreIndex = 0;
        for (char score : badScoresArray) {
            highestBadScoreIndex = Math.max(highestBadScoreIndex, phred.lastIndexOf(score));
        }
        String blanks = new String(new char[highestBadScoreIndex]).replace("\0", " ");
        return blanks + seq.substring(highestBadScoreIndex+1, seq.length());
    }

    public static String trailingTrim(String seq, String phred, char[] badScoresArray) {
        int lowestBadScoreIndex = seq.length();
        int currIndex = -1;
        for (char score : badScoresArray) {
            currIndex = phred.indexOf(score);
            lowestBadScoreIndex = ((currIndex > 0) ? Math.min(lowestBadScoreIndex, currIndex) : lowestBadScoreIndex);
        }
        return seq.substring(0, lowestBadScoreIndex);
    }
}

