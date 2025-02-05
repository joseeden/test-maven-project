package com.testmavenproject;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.elasticsearch.hadoop.mr.EsOutputFormat;
import org.elasticsearch.hadoop.util.WritableUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class AccessLogIndexIngestion {

    public static class AccessLogMapper extends Mapper {
        @Override
        protected void map(Object key, Object value, Context context) throws IOException, InterruptedException {

            String logEntry = value.toString();
            // Split on space
            String[] parts = logEntry.split(" ");
            Map<String, String> entry = new LinkedHashMap<>();

            // Combined LogFormat "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-agent}i\"" combined
            entry.put("ip", parts[0]);
            // Cleanup dateTime String
            entry.put("dateTime", parts[3].replace("[", ""));
            // Cleanup extra quote from HTTP Status
            entry.put("httpStatus", parts[5].replace("\"",  ""));
            entry.put("url", parts[6]);
            entry.put("responseCode", parts[8]);
            // Set size to 0 if not present
            entry.put("size", parts[9].replace("-", "0"));

            context.write(NullWritable.get(), WritableUtils.toWritable(entry));
        }
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        conf.setBoolean("mapred.map.tasks.speculative.execution", false);
        conf.setBoolean("mapred.reduce.tasks.speculative.execution", false);
        conf.set("es.nodes", "192.168.56.101:9200");  // Change to HTTPS in configuration
        conf.set("es.net.ssl", "true");  // Enable SSL
        conf.set("es.net.ssl.cert.allow.self.signed", "true");  // Allow self-signed certs, if applicable
        conf.set("es.net.http.auth.user", "your_username");  // Set the username
        conf.set("es.net.http.auth.pass", "your_password");  // Set the password
        conf.set("es.net.ssl.cert.allow.self.signed", "true");  // Allow self-signed certificates
        conf.set("es.net.ssl.truststore.location", "path_to_truststore");  // Specify the path to the truststore
        conf.set("es.net.ssl.truststore.password", "truststore_password");  // Truststore password
        conf.set("es.resource", "logs");

        Job job = Job.getInstance(conf);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(EsOutputFormat.class);
        job.setMapperClass(AccessLogMapper.class);
        job.setNumReduceTasks(0);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}


