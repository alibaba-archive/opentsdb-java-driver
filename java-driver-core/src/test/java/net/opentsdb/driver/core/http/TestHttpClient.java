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
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpResponse;
import org.junit.Test;
import net.opentsdb.driver.core.conf.Configuration;

public class TestHttpClient {

    @Test
    public void testClient()
            throws InterruptedException, ExecutionException, URISyntaxException, IOException {
        Configuration configuration = new Configuration.Builder().build();
        HttpClient httpClient = null;
        try {
            httpClient = new HttpClient.Builder().setConf(configuration)
                    .setHost("http://opentsdb.net").setPort(80).client().build();

            HttpResponse response = httpClient.get("", HttpApiEndpoint.UNKNOW);
            assertTrue(response.getStatusLine().getStatusCode() == 200);
            
            response = httpClient.delete("", HttpApiEndpoint.UNKNOW);
            System.out.println(response.getStatusLine().getStatusCode());
            assertTrue(response.getStatusLine().getStatusCode() == 200);
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            httpClient.close();
        }
    }
}
