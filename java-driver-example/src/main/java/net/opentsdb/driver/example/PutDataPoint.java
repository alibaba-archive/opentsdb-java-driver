/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package net.opentsdb.driver.example;

import java.io.IOException;
import java.util.HashMap;
import net.opentsdb.driver.core.Cluster;
import net.opentsdb.driver.core.Session;
import net.opentsdb.driver.core.conf.Configuration;
import net.opentsdb.driver.core.model.DataPoint;

/**
 * Connect to an opentsdb server and put the datapoint to openetsdb we use default configuration
 * ,for detailed use see {@Configuration}
 */
public class PutDataPoint {
    private static String host = "127.0.0.1";
    private static int port = 4242;

    public static void main(String[] argvs) {
        Configuration configuration = new Configuration.Builder().build();
        Cluster cluster = new Cluster.Builder().withConfiguration(configuration).withAddress(host)
                .withPort(port).build();
        Session session = null;
        try {
            session = cluster.connect();


            Long ts = System.currentTimeMillis();
            HashMap<String, String> tags = new HashMap<>();
            tags.put("tagk1", "tagv1");
            tags.put("tagk2", "tagv2");
            DataPoint dataPoint = new DataPoint.Builder().setMetric("testMetric").setTimestamp(ts)
                    .setTags(tags).setValue(12333).build();

            session.put(dataPoint);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (cluster != null) {
                try {
                    cluster.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


}


