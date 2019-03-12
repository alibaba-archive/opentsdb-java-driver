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
package net.opentsdb.driver.core;

public enum TsdbVsersion {
    // may be there will exist version format like : 2.0.1 ?
    VERSION_2_0("2.0"),
    VERSION_2_1("2.1"),
    VERSION_2_2("2.2"),
    VERSION_2_3("2.3"),
    VERSION_2_4("2.4"),
    VERSION_3_0("3.0"),
    VERSION_UNKNOW(null);
    

    private String version;
    private TsdbVsersion(String version) {
        // TODO Auto-generated constructor stub
        this.version = version;
    }
    
    public String toString() {
        return version.toLowerCase();
    }
}
