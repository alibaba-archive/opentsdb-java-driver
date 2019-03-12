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
package net.opentsdb.driver.core.conf;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import net.opentsdb.driver.core.TConstants;

public class TestConfiguration {
    private final int connectTimeout = 100;
    private final int sockectTimeout = 100;
    private final int retry = 2;
    private final int ioThreadCount = 3;
    private final int rout = 3;
    private final int poolSize = 2;
    private final int syncTimeout = 10;

    @Test
    public void testDefaultConf() {
        Configuration configuration = new Configuration.Builder().build();
        assertTrue(configuration.getHttpConnectionTimeout() == TConstants.HTTP_CONNECTION_TIMOUT);
        assertTrue(configuration.getHttpKeepaliveTimeout() == 0);
        assertTrue(configuration.getHttpSocketTimeout() == TConstants.HTTP_SOCKET_TIMEOUT);
        assertTrue(configuration.getIoThreadCount() == Runtime.getRuntime().availableProcessors());
        assertTrue(configuration.getMaxRoute() == TConstants.DEFAULT_MAX_ROUTE);
        assertTrue(configuration.getPoolSize() == TConstants.DEFAULT_POOL_SIZE);
        assertTrue(configuration.getPutRetryNum() == TConstants.NORMAL_RETRY);
        assertTrue(configuration.getQueryRetryNum() == TConstants.NORMAL_RETRY);
        assertTrue(configuration.getSyncTimeout() == 0);
        assertTrue(configuration.isSSL() == false);
    }

    @Test
    public void testSeTConf() {
        Configuration configuration = new Configuration.Builder()
                .connectionTimeout(connectTimeout)
                .keepaliveTimeout(connectTimeout)
                .socketTimeout(sockectTimeout)
                .isSSL(true)
                .maxRoute(rout)
                .poolSzie(poolSize)
                .ioThreadCount(ioThreadCount)
                .putRetryNum(retry)
                .queryRetryNum(retry)
                .syncTimeout(syncTimeout).build();
        assertTrue(configuration.getHttpConnectionTimeout() == connectTimeout);
        assertTrue(configuration.getHttpKeepaliveTimeout() == connectTimeout);
        assertTrue(configuration.getHttpSocketTimeout() == sockectTimeout);
        assertTrue(configuration.getIoThreadCount() == ioThreadCount);
        assertTrue(configuration.getMaxRoute() == rout);
        assertTrue(configuration.getPoolSize() == poolSize);
        assertTrue(configuration.getPutRetryNum() == retry);
        assertTrue(configuration.getQueryRetryNum() == retry);
        assertTrue(configuration.getSyncTimeout() == syncTimeout);
        assertTrue(configuration.isSSL() == true);
    }
}
