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
import org.junit.Test;
import net.opentsdb.driver.core.Cluster;
import net.opentsdb.driver.core.Session;
import net.opentsdb.driver.core.TsdbVsersion;
import net.opentsdb.driver.core.conf.Configuration;

public class TestParseVersion {
    @Test
    public void testAnalyzeVersionResult() throws Exception {
        String version =
                "{\"short_revision\":\"c95dfbf\",\"repo\":\"\",\"host\":\"8dce1d6b1963\",\"version\":\"2.3.4\",\"full_revision\":\"c95dfbfef046604f909e25b030856842a3ef0be9\",\"repo_status\":\"MODIFIED\",\"user\":\"admin\",\"branch\":\"release-2.3.4\",\"timestamp\":\"1546850783\"}";
        Session session = null;
        Cluster cluster = null;
        try {
            Configuration configuration = new Configuration.Builder().build();
            cluster = new Cluster.Builder().withAddress("localhost").withPort(4242)
                    .withConfiguration(configuration).build();
            session = cluster.connect();
        } catch (Exception e) {
            // TODO: handle exception
            throw new Exception("exception");
        }


        TsdbVsersion tsdbversion = session.getVersionResult(version);
        assertTrue(tsdbversion.equals(TsdbVsersion.VERSION_2_3));
        version =
                "{\"short_revision\":\"c95dfbf\",\"repo\":\"\",\"host\":\"8dce1d6b1963\",\"version\":\"2.0.4\",\"full_revision\":\"c95dfbfef046604f909e25b030856842a3ef0be9\",\"repo_status\":\"MODIFIED\",\"user\":\"admin\",\"branch\":\"release-2.3.4\",\"timestamp\":\"1546850783\"}";
        tsdbversion = session.getVersionResult(version);
        assertTrue(tsdbversion.equals(TsdbVsersion.VERSION_2_0));

        version =
                "{\"short_revision\":\"c95dfbf\",\"repo\":\"\",\"host\":\"8dce1d6b1963\",\"version\":\"2.1.4\",\"full_revision\":\"c95dfbfef046604f909e25b030856842a3ef0be9\",\"repo_status\":\"MODIFIED\",\"user\":\"admin\",\"branch\":\"release-2.3.4\",\"timestamp\":\"1546850783\"}";
        tsdbversion = session.getVersionResult(version);
        assertTrue(tsdbversion.equals(TsdbVsersion.VERSION_2_1));

        version =
                "{\"short_revision\":\"c95dfbf\",\"repo\":\"\",\"host\":\"8dce1d6b1963\",\"version\":\"2.2.4\",\"full_revision\":\"c95dfbfef046604f909e25b030856842a3ef0be9\",\"repo_status\":\"MODIFIED\",\"user\":\"admin\",\"branch\":\"release-2.3.4\",\"timestamp\":\"1546850783\"}";
        tsdbversion = session.getVersionResult(version);
        assertTrue(tsdbversion.equals(TsdbVsersion.VERSION_2_2));

        version =
                "{\"short_revision\":\"c95dfbf\",\"repo\":\"\",\"host\":\"8dce1d6b1963\",\"version\":\"2.4.4\",\"full_revision\":\"c95dfbfef046604f909e25b030856842a3ef0be9\",\"repo_status\":\"MODIFIED\",\"user\":\"admin\",\"branch\":\"release-2.3.4\",\"timestamp\":\"1546850783\"}";
        tsdbversion = session.getVersionResult(version);
        assertTrue(tsdbversion.equals(TsdbVsersion.VERSION_2_4));

        version =
                "{\"short_revision\":\"c95dfbf\",\"repo\":\"\",\"host\":\"8dce1d6b1963\",\"version\":\"3.0.4\",\"full_revision\":\"c95dfbfef046604f909e25b030856842a3ef0be9\",\"repo_status\":\"MODIFIED\",\"user\":\"admin\",\"branch\":\"release-2.3.4\",\"timestamp\":\"1546850783\"}";
        tsdbversion = session.getVersionResult(version);
        assertTrue(tsdbversion.equals(TsdbVsersion.VERSION_3_0));

        version =
                "{\"short_revision\":\"c95dfbf\",\"repo\":\"\",\"host\":\"8dce1d6b1963\",\"version\":\"4.0.4\",\"full_revision\":\"c95dfbfef046604f909e25b030856842a3ef0be9\",\"repo_status\":\"MODIFIED\",\"user\":\"admin\",\"branch\":\"release-2.3.4\",\"timestamp\":\"1546850783\"}";
        tsdbversion = session.getVersionResult(version);
        assertTrue(tsdbversion.equals(TsdbVsersion.VERSION_UNKNOW));
        session.close();
    }

    @Test
    public void testAnalyzeExceptionResult() throws Exception {
        String version =
                "{\"short_revision\":\"c95dfbf\",\"repo\":\"\",\"host\":\"8dce1d6b1963\",\"full_revision\":\"c95dfbfef046604f909e25b030856842a3ef0be9\",\"repo_status\":\"MODIFIED\",\"user\":\"admin\",\"branch\":\"release-2.3.4\",\"timestamp\":\"1546850783\"}";
        Session session = null;
        Cluster cluster = null;
        try {
            Configuration configuration = new Configuration.Builder().build();
            cluster = new Cluster.Builder().withAddress("localhost").withPort(4242)
                    .withConfiguration(configuration).build();
            session = cluster.connect();
        } catch (Exception e) {
            // TODO: handle exception
            throw new Exception("exception");
        }

        TsdbVsersion tsdbversion = session.getVersionResult(version);
        assertTrue(tsdbversion.equals(TsdbVsersion.VERSION_UNKNOW));

        version =
                "{\"short_revision\":\"c95dfbf\",\"repo\":\"\",\"host\":\"8dce1d6b1963\",\"version\":\"1.0.4\",\"full_revision\":\"c95dfbfef046604f909e25b030856842a3ef0be9\",\"repo_status\":\"MODIFIED\",\"user\":\"admin\",\"branch\":\"release-2.3.4\",\"timestamp\":\"1546850783\"}";
        tsdbversion = session.getVersionResult(version);
        assertTrue(tsdbversion.equals(TsdbVsersion.VERSION_UNKNOW));

        version =
                "{\"short_revision\":\"c95dfbf\",\"repo\":\"\",\"host\":\"8dce1d6b1963\",\"version\":\"0.0.4\",\"full_revision\":\"c95dfbfef046604f909e25b030856842a3ef0be9\",\"repo_status\":\"MODIFIED\",\"user\":\"admin\",\"branch\":\"release-2.3.4\",\"timestamp\":\"1546850783\"}";
        tsdbversion = session.getVersionResult(version);
        assertTrue(tsdbversion.equals(TsdbVsersion.VERSION_UNKNOW));
        session.close();
    }
}
