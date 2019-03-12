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
package net.opentsdb.driver.core.http;

import static org.junit.Assert.assertTrue;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import org.junit.Test;

public class TestHttpUrI {
    
    @Test
    public void testPutUri() throws URISyntaxException {
        //norml put
        String hostNoSSL = "http://localhost:4242/api/put";
        String hostSSL = "http://localhost:4242/api/put";
        HttpClientUrI httpClientUrINoSSL = new HttpClientUrI.Builder().setHost("localhost")
                .setPort(4242).setEnableSSL(false).setApi(HttpApiEndpoint.PUT).build();
        assertTrue(httpClientUrINoSSL.getRightUrI().equals(hostNoSSL));
        
        HttpClientUrI httpClientUrISSL = new HttpClientUrI.Builder().setHost("localhost")
                .setPort(4242).setEnableSSL(true).setApi(HttpApiEndpoint.PUT).build();
        assertTrue(httpClientUrISSL.getRightUrI().equals(hostSSL));
        
        //put with summary or details
        String hostWithSummary = "http://localhost:4242/api/put?summary=true";//not support 
        String hostWithDetail = "http://localhost:4242/api/put?details=true";
        String hostWithSync = "http://localhost:4242/api/put?sync=true";
        String hostWithSyncAndTimeout = "http://localhost:4242/api/put?sync_timeout=60000&sync=true";
        
        HashMap<String, String> params = new HashMap<>();
        params.put("summary", "true");
        HttpClientUrI uri = new HttpClientUrI.Builder().setHost("localhost")
                .setPort(4242).setEnableSSL(false).setApi(HttpApiEndpoint.PUT).build();
        URI uriSummary = uri.createURI(params);
        assertTrue(uriSummary.toString().equals(hostWithSummary));
        params.clear();
        
        params.put("details", "true");
        URI uriDetails = uri.createURI(params);
        assertTrue(uriDetails.toString().equals(hostWithDetail));
        params.clear();
        
        params.put("sync", "true");
        URI uriSync = uri.createURI(params);
        assertTrue(uriSync.toString().equals(hostWithSync));
        params.clear();
        
        params.put("sync", "true");
        params.put("sync_timeout", "60000");
        URI uriSyncTimeout = uri.createURI(params);
        assertTrue(uriSyncTimeout.toString().equals(hostWithSyncAndTimeout));
        params.clear();
    }

    @Test
    public void testQueryUri() {
        String query = "http://localhost:4242/api/query";
        HttpClientUrI httpClientQueryURI = new HttpClientUrI.Builder().setHost("localhost")
                .setPort(4242).setEnableSSL(false).setApi(HttpApiEndpoint.QUERY).build();
        assertTrue(query.equals(httpClientQueryURI.getRightUrI()));
        
        String queryExp = "http://localhost:4242/api/query/exp";
        HttpClientUrI httpClientQueryExpURI = new HttpClientUrI.Builder().setHost("localhost")
                .setPort(4242).setEnableSSL(false).setApi(HttpApiEndpoint.QUERY_EXP).build();
        assertTrue(queryExp.equals(httpClientQueryExpURI.getRightUrI()));
        
        String queryGexp = "http://localhost:4242/api/query/gexp";
        HttpClientUrI httpClientQueryGexpURI = new HttpClientUrI.Builder().setHost("localhost")
                .setPort(4242).setEnableSSL(false).setApi(HttpApiEndpoint.QUERY_GEXP).build();
        assertTrue(queryGexp.equals(httpClientQueryGexpURI.getRightUrI()));
        
        String queryLast = "http://localhost:4242/api/query/last";
        HttpClientUrI httpClientQueryLastURI = new HttpClientUrI.Builder().setHost("localhost")
                .setPort(4242).setEnableSSL(false).setApi(HttpApiEndpoint.QUERY_LAST).build();
        assertTrue(queryLast.equals(httpClientQueryLastURI.getRightUrI()));
    }
    
    @Test
    public void testVersionUri() {
        String version = "http://localhost:4242/api/version";
        HttpClientUrI httpClientURIVersion = new HttpClientUrI.Builder().setHost("localhost")
                .setPort(4242).setEnableSSL(false).setApi(HttpApiEndpoint.VERSION).build();
        assertTrue(httpClientURIVersion.getRightUrI().equals(version));
    }
    
    @Test
    public void testDelete() {
        String delete = "http://localhost:4242/api/query";
        HttpClientUrI httpClientURIDelete = new HttpClientUrI.Builder().setHost("localhost")
                .setPort(4242).setEnableSSL(false).setApi(HttpApiEndpoint.DELETE).build();
        assertTrue(httpClientURIDelete.getRightUrI().equals(delete));
    }
}
