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
package net.opentsdb.driver.core;

import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.nio.reactor.IOReactorException;
import org.junit.After;
import org.junit.Test;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.opentsdb.driver.core.conf.Configuration;
import net.opentsdb.driver.core.exceptions.ErrorValueTypeException;
import net.opentsdb.driver.core.model.Aggregator;
import net.opentsdb.driver.core.model.DataPoint;
import net.opentsdb.driver.core.model.Filter;
import net.opentsdb.driver.core.model.FilterType;
import net.opentsdb.driver.core.model.MetricQuery;
import net.opentsdb.driver.core.model.PutResult;
import net.opentsdb.driver.core.model.Query;
import net.opentsdb.driver.core.model.QueryResult;

public class TestSession {
    private Cluster cluster;

    @After
    public void end() throws IOException {
        if (cluster != null) {
            cluster.close();
        }
    }
    
    @Test
    public void testSession() throws Exception {
        Configuration configuration = new Configuration.Builder().build();// default configuration
        cluster = new Cluster.Builder().withAddress("localhost").withPort(4242)
                .withConfiguration(configuration).build();
        Session session = cluster.connect();
  
        //put datapoint;
        Long ts = System.currentTimeMillis();
        String metric = "metric";
        HashMap<String, String> tags = new HashMap<>();
        tags.put("tagk1", "tagv1");
        tags.put("tagk2", "tagv2");
        DataPoint dataPoint = new DataPoint.Builder().setMetric(metric).setTimestamp(ts)
                .setTags(tags).setValue(12333).build();
        try {
            PutResult putResult = session.put(dataPoint);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("RetryOverException"));
            throw new Exception("put exception");
        } finally {
            try {
                session.close();
            } catch (IOException e) {
                throw new IOException("Session close make io exception");
            }
        }
        
        // version 
        try {
            session = cluster.connect();
            TsdbVsersion vsersion = session.version();
        } catch (Exception e) {
            throw new Exception("get tsdb version throw exception");
        } finally {
            try {
                session.close();
            } catch (IOException e) {
                throw new IOException("Session close make io exception");
            } 
        }
        
        //query 
        List<Filter> filters = new ArrayList<>();
        Filter filter = new Filter.Builder().setType(FilterType.wildcard).setTagk("tagk1")
                .setFilter("tag*").build();
        filters.add(filter);
        MetricQuery metricQuery = new MetricQuery.Builder().metric("metric").downsample("5m-avg")
                .aggregator(Aggregator.max).rate().explicittags(false).filters(filters).tags(tags)
                .build();
        List<MetricQuery> metricQueries = new ArrayList<>();
        metricQueries.add(metricQuery);
        Query query = new Query.Builder().start(ts).sub(metricQueries).build();
        try {
            session = cluster.connect();
            List<QueryResult> queryResult = session.query(query);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("query exception" + e.getMessage());
        } finally {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("Session close make io exception");
            } 
        }
        
        //delete
        MetricQuery metricQueryDelete = new MetricQuery.Builder().metric("metric").downsample("5m-avg")
                .aggregator(Aggregator.max).rate().explicittags(false).filters(filters).tags(tags)
                .build();
        List<MetricQuery> metricQueriesDelete = new ArrayList<>();
        metricQueriesDelete.add(metricQueryDelete);
        Query delete = new Query.Builder().start(ts).sub(metricQueriesDelete).build();
        try {
            session = cluster.connect();
            List<QueryResult> queryResult = session.delete(delete);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("delete exception");
        } finally {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("Session close make io exception");
            } 
        }
    }
    
    @Test
    public void testWithRetry() throws ErrorValueTypeException, IOException {
        Configuration configuration = new Configuration.Builder().build();// default configuration
        cluster = new Cluster.Builder().withAddress("localhost").withPort(4243)
                .withConfiguration(configuration).build();
        Session session = cluster.connect();
  
        //put datapoint;
        Long ts = System.currentTimeMillis();
        String metric = "metric";
        HashMap<String, String> tags = new HashMap<>();
        tags.put("tagk1", "tagv1");
        tags.put("tagk2", "tagv2");
        DataPoint dataPoint = new DataPoint.Builder().setMetric(metric).setTimestamp(ts)
                .setTags(tags).setValue(12333).build();
        try {
            PutResult putResult = session.put(dataPoint);
        } catch (Exception e) {
            assertTrue(e.getClass().getName().contains("OverRetryException"));
        } finally {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("Session close make io exception");
            }
        }
        
        // version 
        try {
            session = cluster.connect();
            TsdbVsersion version = session.version();
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getClass().getName().contains("OverRetryException"));
        } finally {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("Session close make io exception");
            } 
        }
        
        //query 
        List<Filter> filters = new ArrayList<>();
        Filter filter1 = new Filter.Builder().setType(FilterType.ilteral_or).setTagk("tagk1")
                .setFilter("filter1").build();
        Filter filter2 = new Filter.Builder().setType(FilterType.wildcard).setTagk("tagk2")
                .setFilter("filter2").build();
        filters.add(filter1);
        filters.add(filter2);
        MetricQuery metricQuery = new MetricQuery.Builder().metric("metric").downsample("5m-avg")
                .aggregator(Aggregator.max).rate().explicittags(false).filters(filters).tags(tags)
                .build();
        List<MetricQuery> metricQueries = new ArrayList<>();
        metricQueries.add(metricQuery);
        Query query = new Query.Builder().start(ts).sub(metricQueries).build();
        try {
            session = cluster.connect();
            List<QueryResult> queryResult = session.query(query);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getClass().getName().contains("OverRetryException"));
        } finally {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("Session close make io exception");
            } 
        }
        
        //delete
        MetricQuery metricQueryDelete = new MetricQuery.Builder().metric("metric").downsample("5m-avg")
                .aggregator(Aggregator.max).rate().explicittags(false).filters(filters).tags(tags)
                .build();
        List<MetricQuery> metricQueriesDelete = new ArrayList<>();
        metricQueriesDelete.add(metricQueryDelete);
        Query delete = new Query.Builder().start(ts).sub(metricQueries).build();
        try {
            session = cluster.connect();
            List<QueryResult> queryResult = session.delete(query);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getClass().getName().contains("OverRetryException"));
        } finally {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("Session close make io exception");
            } 
        }
    }
}
