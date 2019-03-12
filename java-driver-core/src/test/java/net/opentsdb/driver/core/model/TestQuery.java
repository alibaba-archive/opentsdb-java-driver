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
package net.opentsdb.driver.core.model;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class TestQuery {

    @Test
    public void testQueryMulitMetricQuery() {
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
        Query query = new Query.Builder().start(new Long(12345678)).end(new Long(87654321)).sub(metricQueries).build();
        String jsonString =
                JSON.toJSONString(query, SerializerFeature.DisableCircularReferenceDetect);
        System.out.println(jsonString);
        System.out.println(jsonString.equals(
                "{\"delete\":false,\"end\":87654321,\"globalAnnotations\":false,\"msResolution\":false,\"noAnnotations\":false,\"queries\":[{\"aggregator\":\"max\",\"downsample\":\"5m-avg\",\"explicitTags\":false,\"filters\":[{\"filter\":\"filter1\",\"groupBy\":false,\"tagk\":\"tagk1\",\"type\":\"ilteral_or\"},{\"filter\":\"filter2\",\"groupBy\":false,\"tagk\":\"tagk2\",\"type\":\"wildcard\"}],\"metric\":\"metric\",\"rate\":false,\"tags\":{\"tagk1\":\"tagv1\",\"tagk2\":\"tagv2\"}}],\"showQuery\":false,\"showStats\":false,\"showTSUIDs\":false,\"start\":12345678,\"timezone\":\"UTC\",\"useCalendar\":false}"));
        assertTrue(jsonString.equals(
                "{\"delete\":false,\"end\":87654321,\"globalAnnotations\":false,\"msResolution\":false,\"noAnnotations\":false,\"queries\":[{\"aggregator\":\"max\",\"downsample\":\"5m-avg\",\"explicitTags\":false,\"filters\":[{\"filter\":\"filter1\",\"groupBy\":false,\"tagk\":\"tagk1\",\"type\":\"ilteral_or\"},{\"filter\":\"filter2\",\"groupBy\":false,\"tagk\":\"tagk2\",\"type\":\"wildcard\"}],\"metric\":\"metric\",\"rate\":false,\"tags\":{\"tagk1\":\"tagv1\",\"tagk2\":\"tagv2\"}}],\"showQuery\":false,\"showStats\":false,\"showTSUIDs\":false,\"start\":12345678,\"timezone\":\"UTC\",\"useCalendar\":false}"));
    }

    @Test
    public void testMulitQuery() {
        List<Filter> filters1 = new ArrayList<>();
        HashMap<String, String> tags1 = new HashMap<>();
        tags1.put("tagk1", "tagv1");
        tags1.put("tagk2", "tagv2");
        Filter filter1 = new Filter.Builder().setType(FilterType.ilteral_or).setTagk("tagk1")
                .setFilter("filter1").build();
        Filter filter2 = new Filter.Builder().setType(FilterType.wildcard).setTagk("tagk2")
                .setFilter("filter2").build();
        filters1.add(filter1);
        filters1.add(filter2);
        MetricQuery metricQuery1 = new MetricQuery.Builder().metric("metric1").downsample("5m-avg")
                .aggregator(Aggregator.max).rate().explicittags(false).filters(filters1).tags(tags1)
                .build();
        List<MetricQuery> metricQueries = new ArrayList<>();

        List<Filter> filters2 = new ArrayList<>();
        HashMap<String, String> tags2 = new HashMap<>();
        tags2.put("tagk3", "tagv3");
        tags2.put("tagk4", "tagv4");
        Filter filter3 = new Filter.Builder().setType(FilterType.ilteral_or).setTagk("tagk3")
                .setFilter("filter3").build();
        Filter filter4 = new Filter.Builder().setType(FilterType.wildcard).setTagk("tagk4")
                .setFilter("filter4").build();
        filters2.add(filter3);
        filters2.add(filter4);
        MetricQuery metricQuery2 = new MetricQuery.Builder().metric("metric2").downsample("5m-avg")
                .aggregator(Aggregator.min).rate().explicittags(false).filters(filters2).tags(tags2)
                .build();
        metricQueries.add(metricQuery2);
        metricQueries.add(metricQuery1);
        Query query = new Query.Builder().start(new Long(12345678)).end(new Long(87654321)).sub(metricQueries).build();
        String jsonString =
                JSON.toJSONString(query, SerializerFeature.DisableCircularReferenceDetect);
        assertTrue(jsonString.equals(
                "{\"delete\":false,\"end\":87654321,\"globalAnnotations\":false,\"msResolution\":false,\"noAnnotations\":false,\"queries\":[{\"aggregator\":\"min\",\"downsample\":\"5m-avg\",\"explicitTags\":false,\"filters\":[{\"filter\":\"filter3\",\"groupBy\":false,\"tagk\":\"tagk3\",\"type\":\"ilteral_or\"},{\"filter\":\"filter4\",\"groupBy\":false,\"tagk\":\"tagk4\",\"type\":\"wildcard\"}],\"metric\":\"metric2\",\"rate\":false,\"tags\":{\"tagk3\":\"tagv3\",\"tagk4\":\"tagv4\"}},{\"aggregator\":\"max\",\"downsample\":\"5m-avg\",\"explicitTags\":false,\"filters\":[{\"filter\":\"filter1\",\"groupBy\":false,\"tagk\":\"tagk1\",\"type\":\"ilteral_or\"},{\"filter\":\"filter2\",\"groupBy\":false,\"tagk\":\"tagk2\",\"type\":\"wildcard\"}],\"metric\":\"metric1\",\"rate\":false,\"tags\":{\"tagk1\":\"tagv1\",\"tagk2\":\"tagv2\"}}],\"showQuery\":false,\"showStats\":false,\"showTSUIDs\":false,\"start\":12345678,\"timezone\":\"UTC\",\"useCalendar\":false}"));
    }
}
