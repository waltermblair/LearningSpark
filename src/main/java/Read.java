/**
 * Created by walter on 3/9/17.
 */
import java.util.Arrays;
import java.util.Collections;
import java.io.Serializable;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;

public class Read implements Serializable {
    private String name1;
    private String seq;
    private String name2;
    private String phred;

    public Read(String name1, String seq, String name2, String phred) {
        this.name1 = name1;
        this.seq = seq;
        this.name2 = name2;
        this.phred = phred;
    }

    public String getName1() {
        return name1;
    }

    public String getSeq() {
        return seq;
    }

    public String getName2() {
        return name2;
    }

    public String getPhred() {
        return phred;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public void setPhred(String phred) {
        this.phred = phred;
    }
}
