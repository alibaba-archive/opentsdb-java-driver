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

import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;
import net.opentsdb.driver.core.TConstants;
import net.opentsdb.driver.core.TsdbHttpStatus;


public class AnalyziedResult {
    private int statusCode;
    private String content;
    private TsdbHttpStatus httpStatus;
    private HttpResponse httpResponse;

    
    /**
     *  Helper class to build {@link AnalyziedResult} instances.
     * */
    public static class Builder {
        private int statusCode = -1;
        private String content;
        private TsdbHttpStatus httpStatus;
        private HttpResponse httpResponse = null;

        public Builder httpResponse(HttpResponse httpResponse) {
            this.httpResponse = httpResponse;
            return this;
        }
       
        public Builder statusCode() {
            assert httpResponse != null;
            StatusLine statusLine = httpResponse.getStatusLine();
            this.statusCode = statusLine.getStatusCode();
            return this;
        }

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }
        
        public Builder httpStatus() {
            assert statusCode != -1;
            switch (statusCode) {
                case TConstants.SUCCESS_CODE:
                    this.httpStatus = TsdbHttpStatus.REQUEST_COMPLETED_SUCCESS;
                    break;
                    
                case TConstants.SUCCESS_CALL_UNDONE:
                    this.httpStatus = TsdbHttpStatus.REQUEST_SUCCESS_ACCEPT_UNDONE;
                    break;
                    
                case TConstants.SUCCESS_NOCONTENT:
                    this.httpStatus = TsdbHttpStatus.REQUEST_SUCCESS_NOCONTENT;
                    break;
                    
                case TConstants.RESET_CONTENT:
                    this.httpStatus = TsdbHttpStatus.REQUEST_SUCCESS_RESET_CONTENT;
                    break;
                  
                case TConstants.PARTIAL_CONTENT:
                    this.httpStatus = TsdbHttpStatus.REQUEST_SUCCESS_PARTIAL_CONTENT;
                    break;
                    
                default:
                    if (statusCode >= TConstants.MORE_PROCESS_TO_SUCCESS_PREFIX &&
                        statusCode < TConstants.CLIENT_ERROR_PREFIX) {
                        this.httpStatus = TsdbHttpStatus.REQUEST_MORE_PROCESS_TO_SUCCESS;
                    } else if (statusCode >= TConstants.CLIENT_ERROR_PREFIX &&
                            statusCode < TConstants.SERVER_ERROR_PREFIX){
                        this.httpStatus = TsdbHttpStatus.REQUEST_ERROR_USERDEFINED_CLIENTERROR;
                    } else if (statusCode >= TConstants.SERVER_ERROR_PREFIX &&
                            statusCode < TConstants.UNKNOW_CODE_PREFIX) {
                        this.httpStatus = TsdbHttpStatus.REQUEST_ERROR_USERDEFINED_SERVERERROR;
                    } else {
                        this.httpStatus = TsdbHttpStatus.REQUEST_OTHER_ERROR;
                    }
                    break;
            }
            return this;
        }

        public Builder content() throws ParseException, IllegalArgumentException, IOException {
            assert httpResponse != null;
            if (content == null) {
                HttpEntity entity = httpResponse.getEntity();
                try {
                    String content = null;
                    Header[] headers = this.httpResponse.getHeaders("Content-Encoding");
                    content = EntityUtils.toString(entity);
                    this.content = content;
                } catch (ParseException e) {
                    throw new ParseException("Transform ParseException exception , class type : "
                            + e.getClass().getName() + " exception message : " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(
                            "Transform IllegalArgumentException exception , class type : "
                                    + e.getClass().getName() + " exception message : "
                                    + e.getMessage());
                } catch (IOException e) {
                    throw new IOException("Transform IOException exception , class type : "
                            + e.getClass().getName() + " exception message : " + e.getMessage());
                }
            }
            return this;
        }

        public AnalyziedResult build() {
            return new AnalyziedResult(this);
        }
    }

    public AnalyziedResult(Builder builder) {
        this.statusCode = builder.statusCode;
        this.content = builder.content;
        this.httpStatus = builder.httpStatus;
        this.httpResponse = builder.httpResponse;
    }

    public TsdbHttpStatus getHttpStatus() {
        return httpStatus;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getContent() {
        return content;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }
    
    @Override
    public String toString() {
        return "status code : " + statusCode + " http status : " + httpStatus + " content : " + content; 
    }
}
