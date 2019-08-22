/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.siddhi.extension.io.wso2event.sink;


/**
 * WSO2EventSink related constants
 */
public class WSO2EventSinkConstants {

    static final String WSO2EVENT_SINK_AUTHENTICATION_URL = "auth.url";
    static final String WSO2EVENT_SINK_URL = "url";
    static final String WSO2EVENT_SINK_USERNAME = "username";
    static final String WSO2EVENT_SINK_PASSWORD = "password";
    static final String WSO2EVENT_SINK_PUBLISHER_MODE = "mode";
    static final String WSO2EVENT_SINK_TIMEOUT = "timeout";
    static final String WSO2EVENT_SINK_PUBLISHER_PROTOCOL = "protocol";
    static final String DEFAULT_PUBLISHER_MODE = "non-blocking";
    static final String DEFAULT_PUBLISHER_PROTOCOL = "thrift";
    static final String SOURCE_STREAM_ID = "wso2.stream.id";
    private WSO2EventSinkConstants() {
    }

}
