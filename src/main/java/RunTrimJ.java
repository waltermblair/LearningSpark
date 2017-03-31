import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.SparkSession;

import java.io.FileNotFoundException;

/**
 * Created by wmblair on 3/31/17.
 */
public class RunTrimJ {
    public static void main(String[] args) throws FileNotFoundException, AnalysisException {
        SparkSession spark = SparkSession
                .builder()
                .appName("Scala Trimmer")
                .master("local")
                .getOrCreate();

        String fastqFilepath = args[0];
        String outFilepath = args[1];
        TrimJ trim = new TrimJ();
        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
        trim.writeFastq(trim.trimFastq(trim.readFastq(fastqFilepath, jsc), spark), outFilepath, spark);
    }
}
