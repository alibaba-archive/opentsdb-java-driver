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
package net.opentsdb.driver.core.model;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import org.junit.Test;
import com.alibaba.fastjson.JSON;

public class TestPutResult {

    @Test
    public void simplePut() {
        String content = "";
        PutResult putResult = JSON.parseObject(content, PutResult.class);
        content = null;
        putResult = JSON.parseObject(content, PutResult.class);
    }
    
    
    @Test
    public void detailedPut() {
        String content = "{\"success\":1,\"failed\":0,\"errors\":[]}";
        PutResult putResult = JSON.parseObject(content, PutResult.class);
        PutResult putResult1 = new PutResult(0, 1, new ArrayList<>());
        assertTrue(putResult.equals(putResult1));
    }

    public void detailedPutUnNormal() {
        String content = "{\"success\":0,\"failed\":1,\"errors\":[{\"datapoint\":{\"timestamp\":1550473214,\"value\":\"100\",\"tags\":{\"tagK\":\"tagV\"}},\"error\":\"Metric name was empty\"}]}";
        PutResult putResult = JSON.parseObject(content, PutResult.class);
        assertTrue(putResult.getFailed()!= 0);
        assertTrue(putResult.getErrors() != null && !putResult.getErrors().isEmpty());
    }
}
