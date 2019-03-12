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
import java.util.List;
import org.junit.Test;
import com.alibaba.fastjson.JSON;

public class TestQueryResult {

    @Test
    public void testSimpleQuery() {
        String content =
                "[{\"metric\":\"aliyun.opentsdb.test\",\"tags\":{\"tagK\":\"tagV\"},\"aggregateTags\":[],\"dps\":{\"1550473214\":100}}]";
        List<QueryResult> queryResult = JSON.parseArray(content, QueryResult.class);
        assertTrue(queryResult.size() == 1);
        assertTrue(queryResult.get(0).getMetric().equals("aliyun.opentsdb.test"));
    }

}
