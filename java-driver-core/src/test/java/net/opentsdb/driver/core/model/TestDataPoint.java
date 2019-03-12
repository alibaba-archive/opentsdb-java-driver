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

import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import org.junit.Test;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.opentsdb.driver.core.exceptions.ErrorValueTypeException;

public class TestDataPoint {
    private String metric1 = "metric1";

    @Test
    public void testSyncPutDataPoint() throws ErrorValueTypeException {
        Long ts = System.currentTimeMillis();
        HashMap<String, String> tags = new HashMap<>();
        tags.put("tagk1", "tagv1");
        tags.put("tagk2", "tagv2");
        DataPoint dataPoint = new DataPoint.Builder().setMetric(metric1).setTimestamp(ts)
                .setTags(tags).setValue(12333).build();
        String jsonString =
                JSON.toJSONString(dataPoint, SerializerFeature.DisableCircularReferenceDetect);
        assertTrue(jsonString.equals(
                "{\"metric\":\"metric1\",\"tags\":{\"tagk1\":\"tagv1\",\"tagk2\":\"tagv2\"},\"timestamp\":"+ ts +",\"value\":12333}"));
    }
}
