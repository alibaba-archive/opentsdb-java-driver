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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.client.utils.URIBuilder;
import net.opentsdb.driver.core.Session;
import net.opentsdb.driver.core.http.HttpApiEndpoint;

public class HttpClientUrI {
    private String host;
    private int port;
    private HttpApiEndpoint api;
    private boolean enableSSL;
    
    /**
     * Creates a new {@link HttpClientUrI} instance.
     * <p/>
     * This Builder is get the basic arguments and make the needed uri 
     * for the http client to do real connection , if the enableSSL is
     * set to true ,then a https connection is made. otherwise a http 
     * connection is made.
     */
    public static class Builder {
        private String host;
        private int port;
        private HttpApiEndpoint api;
        private boolean enableSSL;
        
        public Builder setHost(String host) {
            this.host = host;
            return this;
        }
        
        public Builder setPort(int port) {
            this.port = port;
            return this;
        }
        
        public Builder setApi(HttpApiEndpoint api) {
            this.api = api;
            return this;
        }
            
        public Builder setEnableSSL(boolean enableSSL) {
            this.enableSSL = enableSSL;
            return this;
        }
        
        public HttpClientUrI build() {
            return new HttpClientUrI(this);
        }
    }
    
    public HttpClientUrI(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.api = builder.api;
        this.enableSSL = builder.enableSSL;
    }

    public boolean isEnableSSL() {
        return enableSSL;
    }

    public void setEnableSSL(boolean enableSSL) {
        this.enableSSL = enableSSL;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getApi() {
        return api.toString();
    }

    public void setApi(HttpApiEndpoint api) {
        this.api = api;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    private String getNormalPrefixUrI () {
        return "http://" + host + ":" + port + api.toString();
    }
    
    private String getSSLPrefixUrI() {
        return "http://" + host + ":" + port + api.toString();
    }
        
    /** 
     *  return the right uri for the host, port to make http or https
     *  connection. if enableSSL is set to true a https connection will
     *  be made, otherwise a http connection is made.
     *  
     *  @return right uri if enable ssl then return https uri, otherwis 
     *  return http uri;
     * */
    public String getRightUrI() {
        if (isEnableSSL()) {
            return getSSLPrefixUrI();
        } else {
            return getNormalPrefixUrI();
        }
    }
    
    /** 
     * create the uri needed, for the params that use can set from  {@link Session} 
     * apis. 
     * 
     * @param params map of some params that for opentsdb server params like sync/sync_timeout
     * @return uri the new uri for the params
     * */
    public URI createURI(Map<String, String> params) throws URISyntaxException {
        URIBuilder builder = null;
        try {
            builder = new URIBuilder(getRightUrI());
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new URISyntaxException("uri syntax exception ", e.getMessage());
        }

        if (params != null && !params.isEmpty()) {
            for (Entry<String, String> entry : params.entrySet()) {
                builder.setParameter(entry.getKey(), entry.getValue());
            }
        }

        URI uri = null;
        try {
            uri = builder.build();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return uri;
    }
    
}
