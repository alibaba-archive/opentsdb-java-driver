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

public class TestMetricQuery {

    @Test
    public void testMetricQuery() {
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
        MetricQuery metricQuery = new MetricQuery.Builder().metric("metric").downsample("5m-avg")
                .aggregator(Aggregator.max).rate().explicittags(false).filters(filters).tags(tags)
                .build();
        String jsonString =
                JSON.toJSONString(metricQuery, SerializerFeature.DisableCircularReferenceDetect);
        assertTrue(jsonString.equals(
                "{\"aggregator\":\"max\",\"downsample\":\"5m-avg\",\"explicitTags\":false,\"filters\":[{\"filter\":\"filter1\",\"groupBy\":false,\"tagk\":\"tagk1\",\"type\":\"ilteral_or\"},{\"filter\":\"filter2\",\"groupBy\":false,\"tagk\":\"tagk2\",\"type\":\"wildcard\"}],\"metric\":\"metric\",\"rate\":false,\"tags\":{\"tagk1\":\"tagv1\",\"tagk2\":\"tagv2\"}}"));

    }
}
