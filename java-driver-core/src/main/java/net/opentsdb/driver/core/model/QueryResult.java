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
package net.opentsdb.driver.core.model;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * QueryResult which, when you send the start and end timestamp to get the data points,then with the
 * metric and filter/tags we will return the metric with the specified tags and dps means a map with
 * the timestamp to value , for detailed information see
 * http://opentsdb.net/docs/build/html/api_http/put.html
 */
public class QueryResult extends Response {
    private String metric;
    private Map<String, String> tags;
    private List<String> aggregatedTags;
    private Map<Long, Object> dps;

    public QueryResult(String metric, Map<String, String> tags, List<String> aggregatedTags,
            Map<Long, Object> dps) {
        this.metric = metric;
        this.tags = tags;
        this.aggregatedTags = aggregatedTags;
        this.dps = dps;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public List<String> getAggregatedTags() {
        return aggregatedTags;
    }

    public void setAggregatedTags(List<String> aggregatedTags) {
        this.aggregatedTags = aggregatedTags;
    }

    private Map<Long, Object> getDps() {
        return dps;
    }

    public void setDps(Map<Long, Object> dps) {
        this.dps = dps;
    }

    /**
     * retun the all datapoint, by default it is a map, if use arrays flag should be array, but we
     * do not set ,so it is map; Inner the datapoint the map key is long and the map value is
     * double. The map key is the timestamp and the value is the real value.
     */
    public Map<Long, Double> getDoubleDps() {
        Map<Long, Double> tmMap =
                (dps.entrySet().stream().filter(e -> e.getValue() instanceof Number)
                        .collect(Collectors.toMap(e -> e.getKey(), e -> (double) e.getValue())));
        return tmMap.entrySet().stream().filter(e -> e.getValue() instanceof Number)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> newValue, TreeMap::new));

    }

    /**
     * return the datapoints map of the querys, by default the map key is the long type and the
     * value type is int , which the map key is the timestamp and the map value is the real value;
     */
    public Map<Long, Integer> getIntDps() {
        Map<Long, Integer> tmMap =
                (dps.entrySet().stream().filter(e -> e.getValue() instanceof Number)
                        .collect(Collectors.toMap(e -> e.getKey(), e -> (int) e.getValue())));
        return tmMap.entrySet().stream().filter(e -> e.getValue() instanceof Number)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> newValue, TreeMap::new));
    }

    /**
     * return the datapoints map of the querys, by default the map key is the long type and the
     * value type is long , which the map key is the timestamp and the map value is the real value;
     */
    public Map<Long, Long> getLongDps() {
        Map<Long, Long> tmMap = (dps.entrySet().stream().filter(e -> e.getValue() instanceof Number)
                .collect(Collectors.toMap(e -> e.getKey(), e -> (long) e.getValue())));
        return tmMap.entrySet().stream().filter(e -> e.getValue() instanceof Number)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> newValue, TreeMap::new));
    }


    public String toString() {
        return "metric : " + metric + " tags : " + tags + " aggregatedTags : " + aggregatedTags
                + " dps : " + dps;
    }
}
