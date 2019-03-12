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
package net.opentsdb.driver.core;

import java.nio.charset.Charset;

public class TConstants {
    
    //Bytes.UTF8_ENCODING should be updated if this changed
    /** When we encode strings, we always specify UTF8 encoding */
    public static final String UTF8_ENCODING = "UTF-8";

    //Bytes.UTF8_CHARSET should be updated if this changed
    /** When we encode strings, we always specify UTF8 encoding */
    public static final Charset UTF8_CHARSET = Charset.forName(UTF8_ENCODING);
    
    /** Empty string for default use */
    public final static String EMPTY_STTING = "";
    
    /** Default http port  */
    public final static int DEFAULT_PORT = 4242;
    
    /** Http response code for success */
    public static final int SUCCESS_CODE = 200;
    public static final int SUCCESS_CALL_UNDONE = 202;
    public static final int SUCCESS_NOCONTENT = 204;
    public static final int RESET_CONTENT = 205;
    public static final int PARTIAL_CONTENT = 206 ;
    public static final int MORE_PROCESS_TO_SUCCESS_PREFIX = 300;
    public static final int CLIENT_ERROR_PREFIX = 400 ;
    public static final int SERVER_ERROR_PREFIX = 500;
    public static final int UNKNOW_CODE_PREFIX = 600;
    
    /** Normal retry for put and query */
    public static final int NORMAL_RETRY = 3;
    
    /** Normal http connection timeout , ms */
    public static final int HTTP_CONNECTION_TIMOUT = 100;
    
    /** Normal http socket timeout, ms */
    public static final int HTTP_SOCKET_TIMEOUT = 200;
    
    public static final int BUFFER_NUM = 64;
    public static final int BATCH_COUNT_CONST = 3;
    
    /** The default Connection number */
    public static final int DEFAULT_POOL_SIZE = 256;
    /** The deafult max route*/
    public static final int DEFAULT_MAX_ROUTE = 32;

}
