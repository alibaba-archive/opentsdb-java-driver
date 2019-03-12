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

import java.util.List;
import net.opentsdb.driver.core.model.DataPoint;

public class ErrorResultException extends Exception {
    private List<DataPoint> list;
    private String errmsg;

    public ErrorResultException(String errmsg, List<DataPoint> list) {
        this.errmsg = errmsg;
        this.list = list;
    }

    public String toString() {
        return "error data points are : " + list + ", error message is : " + errmsg;
    }
}
