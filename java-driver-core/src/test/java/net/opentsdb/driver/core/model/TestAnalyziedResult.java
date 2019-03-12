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

import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.reactor.IOReactorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.opentsdb.driver.core.TsdbHttpStatus;
import net.opentsdb.driver.core.conf.Configuration;
import net.opentsdb.driver.core.exceptions.ErrorValueTypeException;
import net.opentsdb.driver.core.http.HttpAsyncClient;

public class TestAnalyziedResult {

    private HttpResponse response;
    private HttpAsyncClient httpAsyncClient;
    private AnalyziedResult analyziedResult;

    @Before
    public void init() throws IOReactorException {
        Configuration configuration = new Configuration.Builder().build();
        httpAsyncClient = new HttpAsyncClient(configuration).create();
        httpAsyncClient.start();
    }


    @After
    public void close() throws IOException {
        if (httpAsyncClient != null) {
            httpAsyncClient.close();
        }
    }

    @Test
    public void testStatusCode() throws InterruptedException, ExecutionException {
        // get
        HttpGet request = new HttpGet("http://localhost:4242");
        try {
            response = httpAsyncClient.execute(request, null);
            assertTrue(response.getStatusLine().getStatusCode() == 200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //put
        HttpEntityEnclosingRequestBase requestTmp = null;
        requestTmp = new HttpPost("http://localhost:4242/api/put");
        response = httpAsyncClient.execute(request, null);
        assertTrue(response.getStatusLine().getStatusCode() == 200);

        requestTmp = new HttpPost("http://localhost:4242/api/put?details=true");
        response = httpAsyncClient.execute(request, null);
        assertTrue(response.getStatusLine().getStatusCode() == 200);

        requestTmp = new HttpPost("http://localhost:4242/api/put?sync_timeout=60000&sync=true");
        response = httpAsyncClient.execute(request, null);
        assertTrue(response.getStatusLine().getStatusCode() == 200);
        
        //version
        requestTmp = new HttpPost("http://localhost:4242/api/version");
        response = httpAsyncClient.execute(requestTmp, null);
        assertTrue(response.getStatusLine().getStatusCode() == 200);

    }

    @Test
    public void testHttpStatus() throws InterruptedException, ExecutionException, ParseException,
            IllegalArgumentException, IOException, ErrorValueTypeException {
        // get
        HttpGet request = new HttpGet("http://localhost:4242");
        try {
            response = httpAsyncClient.execute(request, null);
            analyziedResult = new AnalyziedResult.Builder().httpResponse(response).statusCode()
                    .httpStatus().content().build();
            assertTrue(analyziedResult.getHttpStatus() == TsdbHttpStatus.REQUEST_COMPLETED_SUCCESS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        
        Long ts = System.currentTimeMillis();
        HashMap<String, String> tags = new HashMap<>();
        tags.put("tagk1", "tagv1");
        tags.put("tagk2", "tagv2");
        DataPoint dataPoint = new DataPoint.Builder().setMetric("metric").setTimestamp(ts)
                .setTags(tags).setValue(12333).build();
        String requestJson =
                JSON.toJSONString(dataPoint, SerializerFeature.DisableCircularReferenceDetect);
        //put
        HttpEntityEnclosingRequestBase requestTmp = null;
        requestTmp = new HttpPost("http://localhost:4242/api/put?details=true");
        requestTmp.addHeader("Content-Type", "application/json");
        requestTmp.setEntity(new StringEntity(requestJson));
        response = httpAsyncClient.execute(requestTmp, null);
        analyziedResult = new AnalyziedResult.Builder().httpResponse(response).statusCode()
                .httpStatus().content().build();
        assertTrue(analyziedResult.getHttpStatus() == TsdbHttpStatus.REQUEST_COMPLETED_SUCCESS);

        //version
        requestTmp = new HttpPost("http://localhost:4242/api/version");
        response = httpAsyncClient.execute(requestTmp, null);
        assertTrue(analyziedResult.getHttpStatus() == TsdbHttpStatus.REQUEST_COMPLETED_SUCCESS);
    }

    @Test
    public void testHttpStatusAbnormal() {
        TsdbHttpStatus status = null;
        analyziedResult = new AnalyziedResult.Builder().statusCode(202).httpStatus().build();
        assertTrue(analyziedResult.getHttpStatus() == TsdbHttpStatus.REQUEST_SUCCESS_ACCEPT_UNDONE);

        analyziedResult = new AnalyziedResult.Builder().statusCode(204).httpStatus().build();
        assertTrue(analyziedResult.getHttpStatus() == TsdbHttpStatus.REQUEST_SUCCESS_NOCONTENT);

        analyziedResult = new AnalyziedResult.Builder().statusCode(205).httpStatus().build();
        assertTrue(analyziedResult.getHttpStatus() == TsdbHttpStatus.REQUEST_SUCCESS_RESET_CONTENT);

        analyziedResult = new AnalyziedResult.Builder().statusCode(206).httpStatus().build();
        assertTrue(
                analyziedResult.getHttpStatus() == TsdbHttpStatus.REQUEST_SUCCESS_PARTIAL_CONTENT);

        analyziedResult = new AnalyziedResult.Builder().statusCode(301).httpStatus().build();
        assertTrue(
                analyziedResult.getHttpStatus() == TsdbHttpStatus.REQUEST_MORE_PROCESS_TO_SUCCESS);

        analyziedResult = new AnalyziedResult.Builder().statusCode(401).httpStatus().build();
        assertTrue(analyziedResult
                .getHttpStatus() == TsdbHttpStatus.REQUEST_ERROR_USERDEFINED_CLIENTERROR);

        analyziedResult = new AnalyziedResult.Builder().statusCode(501).httpStatus().build();
        assertTrue(analyziedResult
                .getHttpStatus() == TsdbHttpStatus.REQUEST_ERROR_USERDEFINED_SERVERERROR);

        analyziedResult = new AnalyziedResult.Builder().statusCode(601).httpStatus().build();
        assertTrue(analyziedResult.getHttpStatus() == TsdbHttpStatus.REQUEST_OTHER_ERROR);
    }

    @Test
    public void testGetContent() throws InterruptedException, ExecutionException, ParseException,
            IllegalArgumentException, IOException,  ErrorValueTypeException {
        Long ts = System.currentTimeMillis();
        HashMap<String, String> tags = new HashMap<>();
        tags.put("tagk1", "tagv1");
        tags.put("tagk2", "tagv2");
        DataPoint dataPoint = new DataPoint.Builder().setMetric("metrictest").setTimestamp(ts)
                .setTags(tags).setValue(12333).build();
        String jsonString =
                JSON.toJSONString(dataPoint, SerializerFeature.DisableCircularReferenceDetect);
        //put
        HttpEntityEnclosingRequestBase requestTmp = null;
        requestTmp = new HttpPost("http://localhost:4242/api/put?details=true");
        requestTmp.addHeader("Content-Type", "application/json");
        requestTmp.setEntity(new StringEntity(jsonString));
        response = httpAsyncClient.execute(requestTmp, null);
        analyziedResult = new AnalyziedResult.Builder().httpResponse(response).statusCode()
                .httpStatus().content().build();
        assertTrue(analyziedResult.getHttpStatus() == TsdbHttpStatus.REQUEST_COMPLETED_SUCCESS);
        assertTrue(!analyziedResult.getContent().isEmpty());
        //version
        requestTmp = new HttpPost("http://localhost:4242/api/version");
        response = httpAsyncClient.execute(requestTmp, null);
        analyziedResult = new AnalyziedResult.Builder().httpResponse(response).statusCode()
                .httpStatus().content().build();
        JSONObject jsonObject = JSON.parseObject(analyziedResult.getContent());
        String version = jsonObject.getString("version");
        assertTrue(!version.isEmpty());
    }
}
