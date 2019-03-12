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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricQuery {
    private Aggregator aggregator;
    private String metric;
    private Boolean rate = false ;
    private String downsample;
    private Boolean explicitTags;
    private Map<String, String> tags;
    private List<Filter> filters;

    public static class Builder {
        private Aggregator aggregator;
        private String metric;
        private String downsample;
        private Boolean rate = false;
        private Map<String, String> tags = new HashMap<String, String>();
        private Boolean explicitTags;
        private List<Filter> filters;
        
        public Builder metric(String metric) {
            assert metric != null;
            this.metric = metric;
            return this;
        }

        public Builder aggregator(Aggregator aggregator) {
            assert aggregator != null;
            this.aggregator = aggregator;
            return this;
        }
        
        public Builder rate() {
            this.rate = false;
            return this;
        }
        
        public Builder rate(Boolean rate) {
            this.rate = rate;
            return this;
        }
        
        public Builder downsample(String downsample) {
            this.downsample = downsample;
            return this;
        }
        
        public Builder tags(Map<String, String> tags) {
            this.tags = tags;
            return this;
        }
        
        public Builder explicitTags() {
            this.explicitTags = false;
            return this;
        }
        
        public Builder explicittags(Boolean explicitTags) {
            this.explicitTags = explicitTags;
            return this;
        }
        
        public Builder filters(List<Filter> filters) {
            this.filters = filters;
            return this;
        }
        
        public MetricQuery build() {
            MetricQuery metricQuery = new MetricQuery();
            metricQuery.aggregator = this.aggregator;
            metricQuery.downsample = this.downsample;
            metricQuery.explicitTags = this.explicitTags;
            metricQuery.metric = this.metric;
            metricQuery.rate = this.rate;
            metricQuery.tags = this.tags;
            metricQuery.filters = this.filters;
            return metricQuery;
        }
    }
    
    public String getMetric() {
        return this.metric;
    }
    
    public Aggregator getAggregator() {
        return this.aggregator;
    }
    
    public String getDownsample() {
        return this.downsample;
    }
    
    public Boolean getExplicitTags() {
        return this.explicitTags;
    }
    
    public Boolean getRate() {
        return this.rate;
    }
    
    public Map<String, String> getTags() {
        return this.tags;
    }
    
    public List<Filter> getFilters() {
        return this.filters;
    }
    
    public void setMetric(String metric) {
        this.metric = metric;
    }

    public void setDownsample(String downsample) {
        this.downsample = downsample;
    }
    
    public void setExplicitTags(Boolean explicitTags) {
        this.explicitTags = explicitTags;
    }

    public void setRate(Boolean rate) {
        this.rate = rate;
    }
    
    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
    
    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }
    
    public String toString() {
        return " metricquery info , metric : " + metric + " tags : " + tags + " filters : " + filters;
    }
}
