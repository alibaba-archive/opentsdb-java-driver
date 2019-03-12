/*
[ * Licensed to the Apache Software Foundation (ASF) under one
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

public class Filter {
    
    private FilterType type;
    private String tagk;
    private String filter;
    private boolean groupBy = false;
    
    public static class Builder {
        private FilterType type;
        private String tagk;
        private String filter;
        private boolean groupBy;
        
        public Builder setType(FilterType filterType) {
            this.type = filterType;
            return this;
        }
        
        public Builder setTagk(String tagk) {
            this.tagk = tagk;
            return this;
        }
        
        public Builder setFilter(String filter) {
            this.filter = filter;
            return this;
        }
        
        public Builder setGroupBy(boolean groupBy) {
            this.groupBy = groupBy;
            return this;
        }
        
        public Filter build() {
           return new Filter(this);
        }
    }
    
    private Filter(Builder builder) {
        this.type = builder.type;
        this.tagk = builder.tagk;
        this.filter = builder.filter;
        this.groupBy = builder.groupBy;
    }
    
    public FilterType getType () {
        return this.type;
    }
    
    public String getTagk() {
        return this.tagk;
    }
    
    public String getFilter() {
        return this.filter;
    }
    
    public boolean getGroupBy() {
        return this.groupBy;
    }
    
    public String toString() {
        return "Filter info , tag key : " + tagk + " filter : " + filter;
    }
}
