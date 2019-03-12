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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.opentsdb.driver.core.TConstants;
import net.opentsdb.driver.core.exceptions.ErrorValueTypeException;

/**
 * put datapoint ,which consist of metric, timestamp, tags and other you can see details from
 * http://opentsdb.net/docs/build/html/api_http/put.html
 */
public class DataPoint extends BaseRequest {

    private String metric;
    private Long timestamp;
    private Object value;
    private HashMap<String, String> tags;
    private String tsuid;


    /**
     * Creates a new {@link DataPoint.Builder} instance.
     * <p/>
     *
     * @return the new DataPoint builder.
     */
    public static class Builder {
        private String metric;
        private Long timestamp;
        private Object value;
        private HashMap<String, String> tags;
        private String tsuid;

        public Builder setMetric(String metric) {
            this.metric = metric;
            return this;
        }

        public Builder setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setValue(Object value) throws ErrorValueTypeException {
            if (value instanceof Integer || value instanceof Double || value instanceof Long
                    || value instanceof Float) {
                this.value = value;
            } else {
                throw new ErrorValueTypeException(
                        "The data point value type should be integer/double/long/float.");
            }
            return this;
        }

        public Builder setTags(HashMap<String, String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder setTsuid(String tsuid) {
            this.tsuid = tsuid;
            return this;
        }

        public DataPoint build() {
            return new DataPoint(this);
        }
    }

    private DataPoint(Builder builder) {
        this.metric = builder.metric;
        this.timestamp = builder.timestamp;
        this.tags = builder.tags;
        this.value = builder.value;
        if ((builder.tsuid != null) && (!builder.tsuid.isEmpty())) {
            this.tsuid = builder.tsuid;
        }
    }

    public final String getMetric() {
        return metric;
    }

    public final Long getTimestamp() {
        return timestamp;
    }

    public final Object getValue() {
        return value;
    }

    public final HashMap<String, String> getTags() {
        return tags;
    }

    public final String getTSUID() {
        return tsuid;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setTags(HashMap<String, String> tags) {
        this.tags = tags;
    }

    public void setTsuid(String tsuid) {
        this.tsuid = tsuid;
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("metric = ").append(this.metric);
        buf.append(" ts = ").append(this.timestamp);
        buf.append(" value = ").append(this.value);
        if (this.tags != null) {
            for (Map.Entry<String, String> entry : this.tags.entrySet()) {
                buf.append(" ").append(entry.getKey()).append(" = ").append(entry.getValue());
            }
        }
        return buf.toString();
    }


    public long size() {
        int tagsLen = 0;
        String key = "";
        if (tags.size() > 0) {
            Iterator<String> iter = tags.keySet().iterator();
            while (iter.hasNext()) {
                key = iter.next();
                tagsLen += key != null ? key.getBytes(TConstants.UTF8_CHARSET).length : 0;
                if (key != null) {
                    tagsLen += tags.get(key) != null
                            ? tags.get(key).getBytes(TConstants.UTF8_CHARSET).length
                            : 0;
                }
            }
        }
        int metriLen = metric != null ? metric.getBytes(TConstants.UTF8_CHARSET).length : 0;
        int tsUidLen = tsuid != null ? tsuid.getBytes(TConstants.UTF8_CHARSET).length : 0;
        int valueLen = 0;
        if (value instanceof Long || value instanceof Float) {
            valueLen = 8;
        } else if (value instanceof Integer) {
            valueLen = 4;
        } else {
            String tmp = (String) value;
            valueLen = tmp != null ? tmp.getBytes(TConstants.UTF8_CHARSET).length : 0;
        }

        return metriLen + tagsLen + 8 + tsUidLen + valueLen;
    }
}
