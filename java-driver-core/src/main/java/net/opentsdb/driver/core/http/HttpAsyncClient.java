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

import java.io.IOException;
import java.nio.charset.CodingErrorAction;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.net.ssl.SSLContext;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.HttpContext;
import net.opentsdb.driver.core.conf.Configuration;

public class HttpAsyncClient {
    private Configuration configuration;

    private CloseableHttpAsyncClient realHttpClient;

    public HttpAsyncClient(Configuration configuration) {
        this.configuration = configuration;
    }

    public HttpAsyncClient create() throws IOReactorException {
        if (configuration != null) {
            this.realHttpClient = createAsyncClient(configuration);
            return this;
        }
        return null;
    }

    private PoolingNHttpClientConnectionManager getTheConnectionManager(Configuration configuration)
            throws IOReactorException {
        SSLContext sslcontext = SSLContexts.createDefault();

        Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder
                .<SchemeIOSessionStrategy>create().register("http", NoopIOSessionStrategy.INSTANCE)
                .register("https", new SSLIOSessionStrategy(sslcontext)).build();

        IOReactorConfig ioReactorConfig =
                IOReactorConfig.custom().setIoThreadCount(configuration.getIoThreadCount()).build();

        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
        PoolingNHttpClientConnectionManager pconMgr = new PoolingNHttpClientConnectionManager(
                ioReactor, null, sessionStrategyRegistry, null);
        return pconMgr;
    }

    public CloseableHttpAsyncClient createAsyncClient(Configuration configuration)
            throws IOReactorException {

        PoolingNHttpClientConnectionManager pconMgr = getTheConnectionManager(configuration);

        if (configuration.getPoolSize() > 0) {
            pconMgr.setMaxTotal(configuration.getPoolSize());
            pconMgr.setDefaultMaxPerRoute(configuration.getMaxRoute());
            pconMgr.closeExpiredConnections();
        }

        HttpAsyncClientBuilder httpAsyncClientBuilder = HttpAsyncClients.custom();
        httpAsyncClientBuilder.setConnectionManager(pconMgr);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(configuration.getHttpConnectionTimeout())
                .setSocketTimeout(configuration.getHttpSocketTimeout())
                .setConnectionRequestTimeout(configuration.getHttpConnectionTimeout()).build();

        if (requestConfig != null) {
            httpAsyncClientBuilder.setDefaultRequestConfig(requestConfig);
        }

        if (configuration.getHttpKeepaliveTimeout() > 0) {
            httpAsyncClientBuilder.setKeepAliveStrategy(
                    new TsdbkeepAliveStrategy(configuration.getHttpKeepaliveTimeout()));
        } else if (configuration.getHttpKeepaliveTimeout() == 0) {
            httpAsyncClientBuilder.setConnectionReuseStrategy(new TsdbConnectionReuseStrategy());
        }


        CloseableHttpAsyncClient client = httpAsyncClientBuilder.build();
        return client;
    }

    class TsdbkeepAliveStrategy implements ConnectionKeepAliveStrategy {

        private long time;

        public TsdbkeepAliveStrategy(long time) {
            super();
            this.time = time;
        }

        @Override
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            return time;
        }

    }

    class TsdbConnectionReuseStrategy implements ConnectionReuseStrategy {

        @Override
        public boolean keepAlive(HttpResponse response, HttpContext context) {
            return false;
        }
    }

    public CloseableHttpAsyncClient getAsyncHttpClient() {
        return realHttpClient;
    }

    public HttpResponse execute(HttpRequestBase requestJson, FutureCallback<HttpResponse> callback)
            throws InterruptedException, ExecutionException {
        Future<HttpResponse> future = realHttpClient.execute(requestJson, null);
        return future.get();
    }

    public void start() {
        if (realHttpClient != null && !realHttpClient.isRunning()) {
            this.realHttpClient.start();
        }
    }

    public void close() throws IOException {
        if (realHttpClient != null) {
            this.realHttpClient.close();
        }
    }

    public boolean isRunning() {
        if (realHttpClient != null) {
            return realHttpClient.isRunning();
        } else {
            return false;
        }
    }
}
