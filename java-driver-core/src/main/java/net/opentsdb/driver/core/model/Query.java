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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * The Query consist of some elements like start which means the start timestamp to query ,if end is
 * not set then it means it is the current timestamp. if delete is set then it means this metric is
 * going to be delete. the queries can be consist of {@MetricQuery} or TSUID Query, now MetricQuery
 * is support. for deltail information ,see
 * http://opentsdb.net/docs/build/html/api_http/query/index.html
 */
public class Query extends BaseRequest {
    private Long start;
    private Long end;
    private Boolean noAnnotations;
    private Boolean globalAnnotations;
    private Boolean msResolution;
    private Boolean showTSUIDs;
    private Boolean showSummary;
    private Boolean showStats;
    private Boolean showQuery;
    private Boolean delete;
    private String timezone;
    private Boolean useCalendar;
    private List<MetricQuery> queries;


    public Query(Long start, Long end, Boolean noAnnotations, Boolean globalAnnotations,
            Boolean msResolution, Boolean showTSUIDs, Boolean showSummary, Boolean showStats,
            Boolean showQuery, Boolean delete, String timezone, Boolean useCalendar,
            List<MetricQuery> queries) {
        this.start = start;
        this.end = end;
        this.noAnnotations = noAnnotations;
        this.globalAnnotations = globalAnnotations;
        this.showQuery = showQuery;
        this.showSummary = showSummary;
        this.timezone = timezone;
        this.useCalendar = useCalendar;
        this.showTSUIDs = showTSUIDs;
        this.delete = delete;
        this.msResolution = msResolution;
        this.queries = queries;
        this.showStats = showStats;
    }

    public static class Builder {
        private Long startTime;
        private Long endTime = System.currentTimeMillis();
        private Boolean noAnnotations = false;
        private Boolean globalAnnotations = false;
        private Boolean msResolution = false;
        private Boolean showSummary = false;
        private Boolean showStats = false;
        private Boolean showQuery = false;
        private String timezone = "UTC";
        private Boolean useCalendar = false;
        private Boolean delete = false;
        private Boolean showTSUIDs = false;
        private List<MetricQuery> queries = new ArrayList<MetricQuery>();

        public Builder noAnnotations(Boolean noAnnotations) {
            this.noAnnotations = noAnnotations;
            return this;
        }

        public Builder globalAnnotations(Boolean globalAnnotations) {
            this.globalAnnotations = globalAnnotations;
            return this;
        }

        public Builder start(Long startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder end(Date endDate) {
            this.endTime = endDate.getTime();
            return this;
        }

        public Builder end(Long endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder end() {
            this.endTime = System.currentTimeMillis();
            return this;
        }

        public Builder msResolution(boolean msResolution) {
            this.msResolution = msResolution;
            return this;
        }

        public Builder sub(Collection<MetricQuery> metricQueries) {
            Iterator<MetricQuery> iterator = metricQueries.iterator();
            while (iterator.hasNext()) {
                this.queries.add(iterator.next());
            }
            return this;
        }

        public Builder showSummary(boolean showSummary) {
            this.showSummary = showSummary;
            return this;
        }

        public Builder showStatus(boolean showStatus) {
            this.showStats = showStatus;
            return this;
        }

        public Builder showQuery(boolean showQuery) {
            this.showQuery = showQuery;
            return this;
        }

        public Builder showTSUIDs(boolean showTSUIDs) {
            this.showTSUIDs = showTSUIDs;
            return this;
        }

        public Builder delete(boolean delete) {
            this.delete = delete;
            return this;
        }

        public Builder timeZone(String timezone) {
            this.timezone = timezone;
            return this;
        }

        public Builder useCalendar(boolean useCalendar) {
            this.useCalendar = useCalendar;
            return this;
        }

        public Query build() {
            return new Query(startTime, endTime, noAnnotations, globalAnnotations, msResolution,
                    showTSUIDs, showSummary, showStats, showQuery, delete, timezone, useCalendar,
                    queries);
        }
    }

    public Long getStart() {
        return start;
    }

    public Long getEnd() {
        return end;
    }

    public Boolean getDelete() {
        return delete;
    }

    public Boolean getMsResolution() {
        return msResolution;
    }

    public List<MetricQuery> getQueries() {
        return queries;
    }

    public Boolean getNoAnnotations() {
        return noAnnotations;
    }

    public Boolean getGlobalAnnotations() {
        return globalAnnotations;
    }

    public Boolean getShowTSUIDs() {
        return showTSUIDs;
    }

    public void setShowTSUIDs(Boolean showTSUIDs) {
        this.showTSUIDs = showTSUIDs;
    }

    public Boolean getShowStats() {
        return showStats;
    }

    public void setShowStats(Boolean showStats) {
        this.showStats = showStats;
    }

    public Boolean getShowQuery() {
        return showQuery;
    }

    public void setShowQuery(Boolean showQuery) {
        this.showQuery = showQuery;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Boolean getUseCalendar() {
        return useCalendar;
    }

    public void setUseCalendar(Boolean useCalendar) {
        this.useCalendar = useCalendar;
    }

    public void setGlobalAnnotations(Boolean globalAnnotations) {
        this.globalAnnotations = globalAnnotations;
    }

    public void setNoAnnotations(Boolean noAnnotations) {
        this.noAnnotations = noAnnotations;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public void setMsResolution(Boolean msResolution) {
        this.msResolution = msResolution;
    }

    public void setQueries(List<MetricQuery> subQueries) {
        this.queries = subQueries;
    }

    public String toString() {
        return "start ts : " + start + " end ts : " + end + " metricqueries : " + queries;
    }
}

