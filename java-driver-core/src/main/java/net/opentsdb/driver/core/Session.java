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
package net.opentsdb.driver.core;

import java.io.Closeable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.nio.reactor.IOReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.opentsdb.driver.core.conf.Configuration;
import net.opentsdb.driver.core.exceptions.AbnormalResultException;
import net.opentsdb.driver.core.exceptions.ErrorResultException;
import net.opentsdb.driver.core.exceptions.OverRetryException;
import net.opentsdb.driver.core.http.HttpApiEndpoint;
import net.opentsdb.driver.core.http.HttpClient;
import net.opentsdb.driver.core.model.AnalyziedResult;
import net.opentsdb.driver.core.model.DataPoint;
import net.opentsdb.driver.core.model.PutResult;
import net.opentsdb.driver.core.model.Query;
import net.opentsdb.driver.core.model.QueryResult;

/**
 * put/query/version and other requests can be done through this class but batch is not support this
 * time ,multi datapoints can be put throw put ,batch may be of better throghtput, latter will be
 * support; the configuration , host , port shoud be get from Cluster. and {@link #start()} should
 * be called before other interface;
 */
public class Session implements Closeable{
    private static final Logger LOG = LoggerFactory.getLogger(Session.class);

    private HttpClient httpClient;
    private String host;
    private int port;
    private Configuration configuration;

    /**
     * Constructs a new Session instance.
     * <p/>
     * This constructor is for user to connect and do real operation to opentsdb.
     *
     * @param host the host to contact of the session.
     * @param port the port to contact of the session.
     * @param configuration the configuration of the session.
     */
    public Session(String host, int port, Configuration configuration) {
        this.host = host;
        this.port = port;
        this.configuration = configuration;
    }

    /**
     * start the real http client ;
     */
    public void start() throws IOReactorException {
        this.httpClient = new HttpClient.Builder().setHost(host).setPort(port)
                .setConf(configuration).client().build();
        this.httpClient.start();
    }

    /**
     * put for opentsdb /api/put operation ; this will use the default of sync and detailed params,
     * which will sync datapoints to opentsdb storage and return withc the detailed messages;
     * 
     * @param dataPoint the data point to put into the opentsdb
     * @return PutResult the return result for this put, the detail see {@PutResult}
     */
    public PutResult put(DataPoint dataPoint) throws ParseException, IllegalArgumentException,
            InterruptedException, ExecutionException, IOException, AbnormalResultException,
            URISyntaxException, OverRetryException, ErrorResultException {
        return put(Arrays.asList(dataPoint), true, true);
    }



    public PutResult put(DataPoint dataPoint, boolean detailed) throws ParseException,
            IllegalArgumentException, InterruptedException, ExecutionException, IOException,
            AbnormalResultException, URISyntaxException, OverRetryException, ErrorResultException {
        if (configuration.getSyncTimeout() > 0) {
            return put(Arrays.asList(dataPoint), true, detailed);
        } else {
            return put(Arrays.asList(dataPoint), false, detailed);
        }
    }

    /**
     * real put operation ,using this the data point can be put into opentsdb ,if sync is set and
     * putRetryNum is set more than 0 ,then this operation will retry when exception is occurs,
     * retry times can be got from putRetryNum of {@Configuration};
     * 
     * @param dataPoints the list of data point that will be put into opentsdb
     * @param sync when set to true then the data will be put entil successfully to opentsdb storage
     * @param detailed when set to true then will return with the detailed message see {@PutResult}
     * @return PutResult the result of put operation see {@PutResult}
     */
    public PutResult put(List<DataPoint> dataPoints, boolean sync, boolean detailed)
            throws ParseException, IllegalArgumentException, InterruptedException,
            ExecutionException, IOException, AbnormalResultException, URISyntaxException,
            OverRetryException, ErrorResultException {

        String jsonString =
                JSON.toJSONString(dataPoints, SerializerFeature.DisableCircularReferenceDetect);
        String content = null;

        int time = configuration.getPutRetryNum();
        if (configuration.getPutRetryNum() > 0 && sync) {
            while (time > 0) {
                try {
                    content = getHttpContent(HttpApiEndpoint.PUT, jsonString, detailed);
                    break;
                } catch (AbnormalResultException e) {
                    // TODO: handle exception
                    time--;
                    LOG.warn("Put occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver put tried after " + configuration.getQueryRetryNum()
                                + " times for AbnormalResultException, detailed information : "
                                + e.toString());
                        throw new OverRetryException(e.getStatCode(), e.getErrmsg(),
                                e.getServerException());
                    }
                } catch (InterruptedException e) {
                    time--;
                    LOG.warn("Put occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver put tried after " + configuration.getQueryRetryNum()
                                + " times for InterruptedException, detailed information : " + e.getMessage());
                        throw new OverRetryException(e.getMessage());
                    }
                } catch (ExecutionException e) {
                    time--;
                    LOG.warn("Put occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver put tried after " + configuration.getQueryRetryNum()
                                + " times for ExecutionException, detailed information : " + e.getMessage());
                        throw new OverRetryException(e.getMessage());
                    }
                } catch (ParseException e) {
                    time--;
                    LOG.warn("Put occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver put tried after " + configuration.getQueryRetryNum()
                                + " times for ParseException, detailed information : " + e.getMessage());
                        throw new OverRetryException(e.getMessage());
                    }
                } catch (IllegalArgumentException e) {
                    time--;
                    LOG.warn("Put occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver put tried after " + configuration.getQueryRetryNum()
                                + " times for IllegalArgumentException, detailed information : " + e.getMessage());
                        throw new OverRetryException(e.getMessage());
                    }
                } catch (IOException e) {
                    time--;
                    LOG.warn("Put occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver put tried after " + configuration.getQueryRetryNum()
                                + " times for IOException, detailed information : " + e.getMessage());
                        throw new OverRetryException(e.getMessage());
                    }
                } catch (URISyntaxException e) {
                    time--;
                    LOG.warn("Put occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver put tried after " + configuration.getQueryRetryNum()
                                + " times for URISyntaxException, detailed information : " + e.getMessage());
                        throw new OverRetryException(e.getMessage());
                    }
                }
            }
        } else {
            content = getHttpContent(HttpApiEndpoint.PUT, jsonString, detailed);
        }

        PutResult putResult = JSON.parseObject(content, PutResult.class);
        if (putResult.getFailed() != 0) {
            throw new ErrorResultException("put errors :" + putResult.getErrors(), dataPoints);
        }
        return putResult;
    }

    /**
     * real query operation ,using this the data point can be query from opentsdb , if putRetryNum
     * is set more than 0 ,then this operation will retry when exception is occurs, retry times can
     * be got from getRetryNum of {@Configuration};
     * 
     * @param query the query data see {@Query}
     * @param api the api for http endpoint
     * @return queryResults the List of QueryResult see {@QueryResult}
     */
    public List<QueryResult> query(Query query, HttpApiEndpoint api) throws OverRetryException,
            ParseException, IllegalArgumentException, InterruptedException, ExecutionException,
            IOException, AbnormalResultException, URISyntaxException {
        String jsonString =
                JSON.toJSONString(query, SerializerFeature.DisableCircularReferenceDetect);
        String content = null;

        int time = configuration.getQueryRetryNum();
        if (configuration.getQueryRetryNum() > 0) {
            while (time > 0) {
                try {
                    content = getHttpContent(api, jsonString, false);
                    break;
                } catch (AbnormalResultException e) {
                    // TODO: handle exception
                    time--;
                    LOG.warn("Query occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver query tried after " + configuration.getQueryRetryNum()
                                + " times for AbnormalResultException, detailed information : "
                                + e.toString());
                        throw new OverRetryException(e.getStatCode(), e.getErrmsg(),
                                e.getServerException());
                    }
                } catch (InterruptedException e) {
                    time--;
                    LOG.warn("Query occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver query tried after " + configuration.getQueryRetryNum()
                                + " times for InterruptedException, detailed information : " + e.getMessage());
                        throw new OverRetryException(e.getMessage());
                    }
                } catch (ExecutionException e) {
                    time--;
                    LOG.warn("Query occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver query tried after " + configuration.getQueryRetryNum()
                                + " times for ExecutionException, detailed information : " + e.getMessage());
                        throw new OverRetryException(e.getMessage());
                    }
                } catch (ParseException e) {
                    time--;
                    LOG.warn("Query occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver query tried after " + configuration.getQueryRetryNum()
                                + " times for ParseException, detailed information : " + e.getMessage());
                        throw new OverRetryException(e.getMessage());
                    }
                } catch (IllegalArgumentException e) {
                    time--;
                    LOG.warn("Query occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver query tried after " + configuration.getQueryRetryNum()
                                + " times for IllegalArgumentException, detailed information : " + e.getMessage());
                        throw new OverRetryException(e.getMessage());
                    }
                } catch (IOException e) {
                    time--;
                    LOG.warn("Query occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver query tried after " + configuration.getQueryRetryNum()
                                + " times for IOException, detailed information : " + e.getMessage());
                        throw new OverRetryException(e.getMessage());
                    }
                } catch (URISyntaxException e) {
                    time--;
                    LOG.warn("Query occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver query tried after " + configuration.getQueryRetryNum()
                                + " times for URISyntaxException, detailed information : " + e.getMessage());
                        throw new OverRetryException(e.getMessage());
                    }
                }
            }
        } else {
            content = getHttpContent(api, jsonString, false);
        }

        List<QueryResult> queryResults = JSON.parseArray(content, QueryResult.class);
        return queryResults;
    }

    /**
     * query operation ,default is /api/query;
     * 
     * @param query the query param for the operation see {@Query};
     * @return queryResult the list of {@QueryResult}
     */
    public List<QueryResult> query(Query query) throws ParseException, IllegalArgumentException,
            OverRetryException, InterruptedException, ExecutionException, IOException,
            AbnormalResultException, URISyntaxException {
        return query(query, HttpApiEndpoint.QUERY);
    }

    /**
     * query exp operation, for /api/query/exp
     * 
     * @param query the query param see {@Query}
     * @throws URISyntaxException
     * @throws AbnormalResultException
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws OverRetryException
     * @throws IllegalArgumentException
     * @throws ParseException
     * @return queryResult the list of {@QueryResult}
     */
    public List<QueryResult> queryExp(Query query) throws ParseException, IllegalArgumentException,
            OverRetryException, InterruptedException, ExecutionException, IOException,
            AbnormalResultException, URISyntaxException {
        return query(query, HttpApiEndpoint.QUERY_EXP);
    }

    /**
     * query gexp operation, for /api/query/gexp
     * 
     * @param query the query param see {@Query}
     * @throws URISyntaxException
     * @throws AbnormalResultException
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws OverRetryException
     * @throws IllegalArgumentException
     * @throws ParseException
     * @return queryResult the list of {@QueryResult}
     */
    public List<QueryResult> queryGexp(Query query) throws ParseException, IllegalArgumentException,
            OverRetryException, InterruptedException, ExecutionException, IOException,
            AbnormalResultException, URISyntaxException {
        return query(query, HttpApiEndpoint.QUERY_GEXP);
    }

    /**
     * query last operation, for /api/query/last
     * 
     * @param query the query param see {@Query}
     * @throws URISyntaxException
     * @throws AbnormalResultException
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws OverRetryException
     * @throws IllegalArgumentException
     * @throws ParseException
     * @return queryResult the list of {@QueryResult}
     */
    public List<QueryResult> queryLast(Query query) throws ParseException, IllegalArgumentException,
            OverRetryException, InterruptedException, ExecutionException, IOException,
            AbnormalResultException, URISyntaxException {
        return query(query, HttpApiEndpoint.QUERY_LAST);
    }

    /**
     * Return the {@value TSDBVersion} of the opentsdb server
     * 
     * @return version the version of openstdb server
     * @throws OverRetryException
     * @throws URISyntaxException
     * @throws AbnormalResultException
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IllegalArgumentException
     * @throws ParseException
     */
    public TsdbVsersion version() throws OverRetryException, ParseException,
            IllegalArgumentException, InterruptedException, ExecutionException, IOException,
            AbnormalResultException, URISyntaxException {
        String content = null;
        int time = configuration.getQueryRetryNum();
        if (configuration.getQueryRetryNum() > 0) {
            while (time > 0) {
                try {
                    content = getHttpContent(HttpApiEndpoint.VERSION, null, false);
                    break;
                } catch (AbnormalResultException e) {
                    // TODO: handle exception
                    time--;
                    LOG.warn("Version occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver version tried after " + configuration.getQueryRetryNum()
                                + " times for AbnormalResultException, detailed information : "
                                + e.toString());
                        throw new OverRetryException(e.getStatCode(), e.getErrmsg(),
                                e.getServerException());
                    }
                } catch (InterruptedException e) {
                    time--;
                    LOG.warn("Version occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver version tried after " + configuration.getQueryRetryNum()
                                + " times for InterruptedException, detailed information : " + e.getMessage());
                        throw new OverRetryException(e.getMessage());
                    }
                } catch (ExecutionException e) {
                    time--;
                    LOG.warn("Version occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver version tried after " + configuration.getQueryRetryNum()
                                + " times for ExecutionException, detailed information : " + e.getMessage());
                        throw new OverRetryException(e.getMessage());
                    }
                } catch (ParseException e) {
                    time--;
                    LOG.warn("Version occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver version tried after " + configuration.getQueryRetryNum()
                                + " times for ParseException, detailed information : " + e.getMessage());
                        throw new OverRetryException(e.getMessage());
                    }
                } catch (IllegalArgumentException e) {
                    time--;
                    LOG.warn("Version occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver version tried after " + configuration.getQueryRetryNum()
                                + " times for IllegalArgumentException, detailed information : " + e.getMessage());
                        throw new OverRetryException(e.getMessage());
                    }
                } catch (IOException e) {
                    time--;
                    LOG.warn("Version occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver version tried after " + configuration.getQueryRetryNum()
                                + " times for IOException, detailed information : " + e.getMessage());
                        throw new OverRetryException(e.getMessage());
                    }
                } catch (URISyntaxException e) {
                    time--;
                    LOG.warn("Version occurs exceptions time : "
                            + (configuration.getQueryRetryNum() - time));
                    if (time <= 0) {
                        LOG.error("driver version tried after " + configuration.getQueryRetryNum()
                                + " times for URISyntaxException, detailed information : " + e.getMessage());
                        throw new OverRetryException(e.getMessage());
                    }
                }
            }
        } else {
            content = getHttpContent(HttpApiEndpoint.VERSION, null, false);
        }
        return getVersionResult(content);
    }

    /**
     * get the enum version of opentsdb
     * 
     * @param content the http result
     * @return version of opentsdb see {@TsdbVersion}
     */
    protected TsdbVsersion getVersionResult(String content) {
        JSONObject jsonObject = JSON.parseObject(content);
        if (jsonObject != null) {
            String version = jsonObject.getString("version");
            if (version != null) {
                if (version.contains(TsdbVsersion.VERSION_2_0.toString())
                        || version.equals(TsdbVsersion.VERSION_2_0.toString())) {
                    return TsdbVsersion.VERSION_2_0;
                } else if (version.contains(TsdbVsersion.VERSION_2_1.toString())
                        || version.equals(TsdbVsersion.VERSION_2_1.toString())) {
                    return TsdbVsersion.VERSION_2_1;
                } else if (version.contains(TsdbVsersion.VERSION_2_2.toString())
                        || version.equals(TsdbVsersion.VERSION_2_2.toString())) {
                    return TsdbVsersion.VERSION_2_2;
                } else if (version.contains(TsdbVsersion.VERSION_2_3.toString())
                        || version.equals(TsdbVsersion.VERSION_2_3.toString())) {
                    return TsdbVsersion.VERSION_2_3;
                } else if (version.contains(TsdbVsersion.VERSION_2_4.toString())
                        || version.equals(TsdbVsersion.VERSION_2_4.toString())) {
                    return TsdbVsersion.VERSION_2_4;
                } else if (version.contains(TsdbVsersion.VERSION_3_0.toString())
                        || version.equals(TsdbVsersion.VERSION_3_0.toString())) {
                    return TsdbVsersion.VERSION_3_0;
                } else {
                    return TsdbVsersion.VERSION_UNKNOW;
                }
            }
        }
        return TsdbVsersion.VERSION_UNKNOW;
    }

    /**
     * delete operations ,for the specified query
     * 
     * @param query the query for specified metri in query ,see {@Query}
     * @return delete result ;
     */
    public List<QueryResult> delete(Query query) throws ParseException, IllegalArgumentException,
            OverRetryException, InterruptedException, ExecutionException, IOException,
            AbnormalResultException, URISyntaxException {
        query.setDelete(true);
        return query(query, HttpApiEndpoint.DELETE);
    }

    /**
     * the http request ,for different type of http request ,we return differenet result
     * 
     * @param endpoint the http endpoint see {@HttpApiEndpoint}
     * @param jsonString the request json
     * @param detailed the detailed for request to return detailed message
     * @return the http response
     */
    private String getHttpContent(HttpApiEndpoint endpoint, String jsonString, boolean detailed)
            throws InterruptedException, ExecutionException, ParseException,
            IllegalArgumentException, IOException, AbnormalResultException, URISyntaxException {
        assert httpClient != null;
        HttpResponse httpResponse = null;
        switch (endpoint) {
            case PUT:
                int syncTimeout = configuration.getSyncTimeout();
                Map<String, String> map = new HashMap<>();
                if (detailed) {
                    map.put("details", "true");
                    if (syncTimeout > 0) {
                        map.put("sync", "true");
                        map.put("timeout", syncTimeout + "");
                    }
                } else {
                    if (syncTimeout > 0) {
                        map.put("sync", "true");
                        map.put("timeout", syncTimeout + "");
                    }
                }
                httpResponse = httpClient.postDetailed(jsonString, map, endpoint);
                break;
            case DELETE:
                httpResponse = httpClient.delete(jsonString, endpoint);
                break;
            case VERSION:
                httpResponse = httpClient.version(endpoint);
                break;
            default:
                httpResponse = httpClient.post(jsonString, endpoint);
                break;
        }

        AnalyziedResult resultResponse = new AnalyziedResult.Builder().httpResponse(httpResponse)
                .statusCode().httpStatus().content().build();
        TsdbHttpStatus httpStatus = resultResponse.getHttpStatus();

        if (httpStatus == TsdbHttpStatus.REQUEST_COMPLETED_SUCCESS) {
            return resultResponse.getContent();
        } else {
            JSONObject json = JSONObject.parseObject(resultResponse.getContent());
            String errorContent = json.getString("error");
            JSONObject erroJson = JSONObject.parseObject(errorContent);
            String errorCode = erroJson.getString("code");
            String serverException = erroJson.getString("trace");
            String message = erroJson.getString("message");
            throw new AbnormalResultException(errorCode, serverException, message);
        }
    }

    /**
     * Return if the session is running;
     */
    public boolean isRunning() {
        if (httpClient != null) {
            return httpClient.isRunning();
        } else {
            return false;
        }
    }

    @Override
    public void close() throws IOException {
        if (this.httpClient != null) {
            this.httpClient.close();
        }
    }
}
