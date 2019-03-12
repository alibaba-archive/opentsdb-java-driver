/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.opentsdb.driver.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.opentsdb.driver.core.Cluster;
import net.opentsdb.driver.core.Session;
import net.opentsdb.driver.core.conf.Configuration;
import net.opentsdb.driver.core.model.Aggregator;
import net.opentsdb.driver.core.model.DataPoint;
import net.opentsdb.driver.core.model.Filter;
import net.opentsdb.driver.core.model.FilterType;
import net.opentsdb.driver.core.model.MetricQuery;
import net.opentsdb.driver.core.model.Query;
/**
 * Query time range of data points of different metric from opentsdb 
 * use the default Configuration for detailed information see {@Configuration}
 * */
public class QueryMetrics {

    private static String host = "127.0.0.1";
    private static int port = 4242;
    private static long startTs = 1543593600;// 2018/12/1 0:0:0

    public static void main(String[] argvs) {
        Configuration configuration = new Configuration.Builder().build();
        Cluster cluster = new Cluster.Builder().withConfiguration(configuration).withAddress(host)
                .withPort(port).build();
        Session session = null;
        try {
            session = cluster.connect();

            List<Filter> filters = new ArrayList<>();
            HashMap<String, String> tags = new HashMap<>();
            tags.put("tagk1", "tagv1");
            tags.put("tagk2", "tagv2");
            Filter filter1 = new Filter.Builder().setType(FilterType.ilteral_or).setTagk("tagk1")
                    .setFilter("filter1").build();
            Filter filter2 = new Filter.Builder().setType(FilterType.wildcard).setTagk("tagk2")
                    .setFilter("filter2").build();
            filters.add(filter1);
            filters.add(filter2);
            MetricQuery metricQuery1 = new MetricQuery.Builder().metric("metric").downsample("5m-avg")
                    .aggregator(Aggregator.max).rate().explicittags(false).filters(filters).tags(tags)
                    .build();
            List<MetricQuery> metricQueries = new ArrayList<>();
            metricQueries.add(metricQuery1);
            Query query = new Query.Builder().start(startTs).sub(metricQueries).build();
            
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
