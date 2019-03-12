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
package net.opentsdb.driver.core.conf;

import net.opentsdb.driver.core.TConstants;

/**
 * The configuration of the cluster.
 * It configures the following:
 * <ul>
 * <li>http connection configure.</li>
 * <li>Connection pooling configurations.</li>
 * <li>Query/Put options.</li>
 * </ul>
 */
public class Configuration {
    
    /**
     * Returns a builder to create a new {@code Configuration} object.
     * <p/>
     * You only need this if you are building the configuration yourself. If you
     * use {@link Configuration#builder()}, it will be done under the hood for you.
     *
     * @return the builder.
     */
    public static Builder builder() {
        return new Builder();
    }
    
    private int httpKeepaliveTimeout; //ms
    private int httpConnectionTimeout;//ms
    private int httpSocketTimeout;//ms
    
    private int poolSize;
    private int maxRoute;
    private int ioThreadCount;
    
    private int queryRetryNum;
    private int putRetryNum;
     
    private int syncTimeout;
    private boolean isSSL;
    
    public Configuration(int httpKeepaliveTimeout, 
            int httpConnectionTimeout, 
            int httpSocketTimeout,
            int queryRetryNum, 
            int putRetryNum,
            int poolSzie,
            int maxRoute, 
            int ioThreadCount,
            int syncTimeout,
            boolean isSSL) {
        this.httpKeepaliveTimeout = httpKeepaliveTimeout;
        this.httpConnectionTimeout = httpConnectionTimeout;
        this.httpSocketTimeout = httpSocketTimeout;
        this.queryRetryNum = queryRetryNum;
        this.putRetryNum = putRetryNum;
        this.poolSize = poolSzie;
        this.maxRoute = maxRoute;
        this.ioThreadCount = ioThreadCount;
        this.syncTimeout = syncTimeout;
        this.isSSL = isSSL;
    }
    
    public Configuration (Builder builder) {
        this.httpKeepaliveTimeout = builder.httpKeepaliveTimeout;
        this.httpConnectionTimeout = builder.httpConnectionTimeout;
        this.httpSocketTimeout = builder.httpSocketTimeout;
        this.queryRetryNum = builder.queryRetryNum;
        this.putRetryNum = builder.putRetryNum;
        this.poolSize = builder.poolSize;
        this.maxRoute = builder.maxRoute;
        this.ioThreadCount = builder.ioThreadCount;
        this.syncTimeout = builder.syncTimeout;
        this.isSSL = builder.isSSL;
    }

    
    /**
     * A builder to create a new {@code Configuration} object.
     */
    public static class Builder {
        private int httpKeepaliveTimeout; 
        private int httpConnectionTimeout;
        private int httpSocketTimeout;
        private int poolSize;
        private int maxRoute;
        private int ioThreadCount;
        
        private int queryRetryNum;
        private int putRetryNum;
        
        private int syncTimeout;
        
        private boolean isSSL;
        
        /**
         * Sets the http keepalive timeout conf for this cluster.
         *  
         * @param httpKeepaliveTimeout the conf.
         * @return this builder.
         */
        public Builder keepaliveTimeout(int httpKeepaliveTimeout) {
            this.httpKeepaliveTimeout = httpKeepaliveTimeout;
            return this;
        }
        
        /**
         * Sets the http connection timeout conf for this cluster.
         * when conenct time is more than this conf, then throw out 
         * timeout exception;
         * 
         * @param httpConnectionTimeout the conf.
         * @return this builder.
         */
        public Builder connectionTimeout(int httpConnectionTimeout) {
            this.httpConnectionTimeout = httpConnectionTimeout;
            return this;
        }
        
        /**
         * Sets the http socket timeout conf for this cluster.
         * when communicate time is more than this conf, then an exception of 
         * timeout will be thrown;
         * 
         * @param httpSocketTimeout the conf.
         * @return this builder.
         */
        public Builder socketTimeout(int httpSocketTimeout) {
            this.httpSocketTimeout = httpSocketTimeout;
            return this;
        }
        
        /**
         * Sets the http connection pool size connection ,the default is 
         * Tconstants.DEFAULT_POOL_SIZE
         * 
         * @param poolSize the pool size connection of the http coonection
         * @return this builder
         * */
        public Builder poolSzie(int poolSize) {
            this.poolSize = poolSize;
            return this;
        }
        
        /**
         * Sets the http connection of per connect ,the default is 
         * Tconstants.DEFAULT_MAX_ROUTE
         * 
         * @param maxRoute the pool per connect
         * @return this builder
         * */
        public Builder maxRoute(int maxRoute) {
            this.maxRoute = maxRoute;
            return this;
        }
        
        /**
         * Sets the io thread
         * 
         * @param count io thread count
         * @return this builder
         * */
        public Builder ioThreadCount(int count) {
            this.ioThreadCount = count;
            return this;
        }
        
        /**
         * Sets the query retry number conf for this cluster.
         * when every time of query ,an exception is thrown ,then we will 
         * retry query for this operaction ,until the conf number of {@link #queryRetryNum}
         * is reached .
         * 
         * @param queryRetryNum the conf.
         * @return this builder.
         */
        public Builder queryRetryNum(int queryRetryNum) {
            this.queryRetryNum = queryRetryNum;
            return this;
        }
        
        /**
         * Sets the put retry number conf for this cluster.
         * when every time of put ,an exception is thrown ,then we will 
         * retry put for this operaction ,until the conf number of {@link #putRetryNum}
         * is reached 
         * 
         * @param putRetryNum the conf.
         * @return this builder.
         */
        public Builder putRetryNum(int putRetryNum) {
            this.putRetryNum = putRetryNum;
            return this;
        }
        
        /**
         * Set the syncTimeout if need;
         * @param syncTimeout the sync out of the conf, which means 
         *        the datapoint should be put into the storage.
         * @return this builder.
         * */
        public Builder syncTimeout(int syncTimeout) {
            this.syncTimeout = syncTimeout;
            return this;
        }
        
        /**
         * Sets the is ssl conf for this cluster.
         * after the {@link #isSSL} is set to true, then every operaction will use https 
         * 
         * @param isSSL the conf.
         * @return this builder.
         */
        public Builder isSSL(boolean isSSL) {
            this.isSSL = isSSL;
            return this;
        }
        
        /**
         * Builds the final object from this builder.
         * <p/>
         * Any field that hasn't been set explicitly will get its default value.
         *
         * @return the object.
         */
        public Configuration build() {
            return new Configuration(httpKeepaliveTimeout != 0 ? httpKeepaliveTimeout : 0, 
                    httpConnectionTimeout != 0 ? httpConnectionTimeout : TConstants.HTTP_CONNECTION_TIMOUT, 
                    httpSocketTimeout != 0 ? httpSocketTimeout : TConstants.HTTP_SOCKET_TIMEOUT, 
                    queryRetryNum != 0 ? queryRetryNum : TConstants.NORMAL_RETRY, 
                    putRetryNum != 0 ? putRetryNum : TConstants.NORMAL_RETRY, 
                    poolSize != 0 ? poolSize : TConstants.DEFAULT_POOL_SIZE, 
                    maxRoute != 0 ? maxRoute : TConstants.DEFAULT_MAX_ROUTE,
                    ioThreadCount != 0 ? ioThreadCount : Runtime.getRuntime().availableProcessors(),
                    syncTimeout > 0 ? syncTimeout : 0,        
                    isSSL != false ? isSSL : false);
        }
    }

    /** 
     * Returns the http keepalive timeout configuration
     * 
     *  @return the http keepalive timeout
     * */
    public int getHttpKeepaliveTimeout() {
        return httpKeepaliveTimeout;
    }

    /**
     * Returns the http connection timeout
     * 
     * @return the http connection timeout
     * */
    public int getHttpConnectionTimeout() {
        return httpConnectionTimeout;
    }
    
    /**
     * Returns the http socket timeout
     * 
     * @return the http socket timeout
     * */
    public int getHttpSocketTimeout() {
        return httpSocketTimeout;
    }
    
    /**
     * Returns the pool size which mean the 
     * http connection max number;
     * 
     * @return the pool size
     * */
    public int getPoolSize() {
        return poolSize;
    }
    
    /**
     * Returns the max route 
     * 
     * @return the max route
     * */
    public int getMaxRoute() {
        return maxRoute;
    }
    
    /**
     * Returns the  io thread count 
     * 
     * @return the io thread count
     * */
    public int getIoThreadCount() {
        return ioThreadCount;
    }
        
    /**
     * Returns the query retry number
     * 
     * @return the get retry number
     * */
    public int getQueryRetryNum() {
        return queryRetryNum;
    }

    /**
     * Returns the put operation retry number when excption occurs
     * */
    public int getPutRetryNum() {
        return putRetryNum;
    }
    
    /**
     * Return the sync timeout
     * */
    public int getSyncTimeout() {
        return syncTimeout;
    }
    
    /**
     * Returns the is ssl or not
     * */
    public boolean isSSL() {
        return isSSL;
    }
    
}
