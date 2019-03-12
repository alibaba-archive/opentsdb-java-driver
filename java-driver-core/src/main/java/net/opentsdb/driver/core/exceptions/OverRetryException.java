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
package net.opentsdb.driver.core.exceptions;

public class OverRetryException extends Exception {
    private String errmsg;
    private String statCode;
    private String serverException;

    public OverRetryException(String errmsg) {
        this.errmsg = errmsg;
    }

    public OverRetryException(String statCode, String errmsg, String serverException) {
        this.statCode = statCode;
        this.errmsg = errmsg;
        this.serverException = serverException;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public String getStatCode() {
        return statCode;
    }

    public String getServerException() {
        return serverException;
    }

    public String toString() {
        return "stat code : " + statCode != null ? statCode
                : " no valid code, " + "server excepion :" + serverException != null
                        ? serverException
                        : " no valid exception" + " errmsg : " + errmsg;
    }
}
