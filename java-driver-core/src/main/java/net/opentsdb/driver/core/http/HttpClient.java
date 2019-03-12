/*
 * , * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.reactor.IOReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.opentsdb.driver.core.TConstants;
import net.opentsdb.driver.core.conf.Configuration;

public class HttpClient {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);

    private HttpAsyncClient realAsyncClient;
    private Configuration configuration;
    private String host;
    private int port;

    /**
     * Constructs a new HttpClient instance, the basic Constructi=or
     */
    public HttpClient(HttpAsyncClient client, Configuration configuration, String host, int port) {
        this.realAsyncClient = client;
        this.configuration = configuration;
        this.host = host;
        this.port = port;
    }

    public HttpClient(Builder builder) {
        this(builder.realAsyncClient, builder.configuration, builder.host, builder.port);
    }

    /**
     * Helper class to build {@link HttpClient} instances.
     */
    public static class Builder {
        private String host;
        private int port;
        private Configuration configuration;
        private HttpAsyncClient realAsyncClient;

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setConf(Configuration configuration) {
            this.configuration = configuration;
            return this;
        }

        public Builder client() throws IOReactorException {
            this.realAsyncClient = new HttpAsyncClient(configuration).create();
            return this;
        }

        public HttpClient build() {
            return new HttpClient(this);
        }
    }

    /**
     * send http request to server and get the result ;
     */
    private HttpResponse modifyRequestHttp(HttpMethod httpMethod, String requestJson,
            Map<String, String> params, HttpApiEndpoint api) throws InterruptedException,
            ExecutionException, URISyntaxException, UnsupportedEncodingException {
        HttpRequestBase request = null;
        HttpClientUrI uri = new HttpClientUrI.Builder().setHost(host).setPort(port).setApi(api)
                .setEnableSSL(configuration.isSSL()).build();

        switch (httpMethod) {
            case GET:
                request = new HttpGet(uri.getRightUrI());
                break;

            case POST:
                URI localUri = uri.createURI(params);
                HttpEntityEnclosingRequestBase innerRequest = null;
                innerRequest = new HttpPost(localUri);
                if (requestJson != null && requestJson.length() > 0) {
                    innerRequest.addHeader("Content-Type", "application/json");
                    innerRequest.setEntity(generateStringEntity(requestJson));
                }
                request = innerRequest;
                break;

            case DELETE:
                HttpEntityEnclosingRequestBase tmpRequest = null;
                tmpRequest = new HttpPost(uri.getRightUrI());
                if (requestJson != null && requestJson.length() > 0) {
                    tmpRequest.addHeader("Content-Type", "application/json");
                    tmpRequest.setEntity(generateStringEntity(requestJson));
                }
                request = tmpRequest;
                break;

            default:
                LOG.error("Http request format error, input error http method.");
                break;
        }

        HttpResponse httpResponse = realAsyncClient.execute(request, null);
        return httpResponse;
    }


    private StringEntity generateStringEntity(String json) throws UnsupportedEncodingException {
        StringEntity stringEntity = new StringEntity(json);
        return stringEntity;
    }

    public HttpResponse version(HttpApiEndpoint api) throws InterruptedException,
            ExecutionException, URISyntaxException, UnsupportedEncodingException {
        return post(TConstants.EMPTY_STTING, api);
    }

    public HttpResponse post(String jsonRequest, HttpApiEndpoint api) throws InterruptedException,
            ExecutionException, URISyntaxException, UnsupportedEncodingException {
        return postDetailed(jsonRequest, null, api);
    }

    public HttpResponse postDetailed(String jsonRequest, Map<String, String> map,
            HttpApiEndpoint api) throws InterruptedException, ExecutionException,
            URISyntaxException, UnsupportedEncodingException {
        return modifyRequestHttp(HttpMethod.POST, jsonRequest, map, api);
    }

    public HttpResponse get(String jsonRequest, HttpApiEndpoint api) throws InterruptedException,
            ExecutionException, URISyntaxException, UnsupportedEncodingException {
        return modifyRequestHttp(HttpMethod.GET, jsonRequest, null, api);
    }

    public HttpResponse delete(String jsonRequest, HttpApiEndpoint api) throws InterruptedException,
            ExecutionException, URISyntaxException, UnsupportedEncodingException {
        return modifyRequestHttp(HttpMethod.DELETE, jsonRequest, null, api);
    }
    
    /**
     * the http client is start
     * */
    public boolean isRunning() {
        if (realAsyncClient != null) {
            return realAsyncClient.isRunning();
        } else {
            return false;
        }
    }

    public void start() {
        if (!isRunning()) {
            this.realAsyncClient.start();
        }
    }

    public void close() throws IOException {
        if (realAsyncClient != null && isRunning()) {
            this.realAsyncClient.close();
        }
    }
}
