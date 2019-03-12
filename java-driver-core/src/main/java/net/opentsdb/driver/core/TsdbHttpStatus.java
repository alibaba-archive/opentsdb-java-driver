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

public enum TsdbHttpStatus {
    
    REQUEST_COMPLETED_SUCCESS("SUCCESS"),
    REQUEST_SUCCESS_ACCEPT_UNDONE("CALL_ACCEPT_UNDONE"),
    REQUEST_SUCCESS_NOCONTENT("SUCCESS_NO_CONTENT"),
    REQUEST_SUCCESS_RESET_CONTENT("RESET_CONTENT"),
    REQUEST_SUCCESS_PARTIAL_CONTENT("PARTIAL_CONTENT"),
    REQUEST_MORE_PROCESS_TO_SUCCESS("NEED_MORE_PROCESS_TO_SUCCESS"),
    REQUEST_ERROR_USERDEFINED_CLIENTERROR("USER_DEFINED_CLIENT_ERRROR"),
    REQUEST_ERROR_USERDEFINED_SERVERERROR("USER_DEFINED_SERVER_ERROR"),
    REQUEST_OTHER_ERROR("OTHER_ERROR"),
    UNKNOW(null);
    
    private String message;
    private TsdbHttpStatus(String message) {
        // TODO Auto-generated constructor stub
        this.message = message;
    }
    
    public String toString() {
        return name().toLowerCase();
    }
}
