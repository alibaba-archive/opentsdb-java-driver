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

import java.util.List;

/**
 * The result of the put operation ,the datapoints means the put datapoints and if all datapoint is
 * put successfully then the success is the datapoints's size failed is 0 ,otherwise the failed is
 * the failed datapoints number and the sucess is the successed number; the errors means the put
 * result message of the datapoints ,if no failed datapoint, then the errors is empty; for detaild
 * information see http://opentsdb.net/docs/build/html/api_http/put.html
 */
public class PutResult extends Response {
    private List<Errors> errors;
    private int failed;
    private int success;

    public PutResult(int failed, int success, List<Errors> errors) {
        // TODO Auto-generated constructor stub
        this.failed = failed;
        this.success = success;
        this.errors = errors;
    }

    public List<Errors> getErrors() {
        return errors;
    }

    public void setErrors(List<Errors> errors) {
        this.errors = errors;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof PutResult) {
            PutResult tmPutResult = (PutResult) obj;
            return (tmPutResult.failed == failed) && (tmPutResult.success == success)
                    && (tmPutResult.errors.equals(errors));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new Integer(failed).hashCode() + new Integer(success).hashCode() + errors.hashCode();
    }

    @Override
    public String toString() {
        return " operation is " + (failed > 0 ? " failed " : " successed ") + " ,errors : "
                + errors;
    }
}
