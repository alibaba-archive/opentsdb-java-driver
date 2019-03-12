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
package net.opentsdb.driver.core.http;

import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.nio.reactor.IOReactorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import net.opentsdb.driver.core.conf.Configuration;

public class TestHttpAsyncClient {

    private HttpAsyncClient httpAsyncClient;

    @Before
    public void init() throws IOReactorException {
        httpAsyncClient = new HttpAsyncClient(new Configuration.Builder().build()).create();
    }


    @After
    public void after() {
        try {
            if (httpAsyncClient != null) {
                httpAsyncClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHttpSync() throws IOException {

        // Start the client
        httpAsyncClient.start();
        // Execute request
        HttpGet request = new HttpGet("http://opentsdb.net/");
        try {
            HttpResponse response = httpAsyncClient.execute(request, null);
            assertTrue(response.getStatusLine().getStatusCode() == 200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
