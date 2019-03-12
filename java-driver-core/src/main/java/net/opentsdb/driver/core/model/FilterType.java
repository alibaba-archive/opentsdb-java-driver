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

public enum FilterType {

    literal_or("literal_or"),
    ilteral_or("ilteral_or"),
    not_literal_or("not_literal_or"),
    not_iliteral_or("not_iliteral_or"),
    wildcard("wildcard"),
    iwildcard("iwildcard"),
    regexp("regexp"),
    nullfilter(null);
    
    final String filterType;
    private FilterType(String filterType) {
        // TODO Auto-generated constructor stub
        this.filterType = filterType;
    }
    
    public String toString() {
        return filterType;
    }
}
