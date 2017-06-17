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

package org.wso2.extension.siddhi.io.wso2event.source;

import org.wso2.carbon.databridge.core.DataBridgeEventStreamService;
import org.wso2.carbon.databridge.core.DataBridgeSubscriberService;
import org.wso2.extension.siddhi.map.wso2event.service.WSO2EventMappingServiceImpl;
import org.wso2.siddhi.core.stream.input.source.SourceEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class which manages the Databridge receiver connections
 */
public class WSO2EventSourceRegistrationManager {

    private static DataBridgeSubscriberService dataBridgeSubscriberService;
    private static DataBridgeEventStreamService dataBridgeEventStreamService;
    private static WSO2EventMappingServiceImpl wso2EventMappingService;

    private static Map<String, List<SourceEventListener>> streamSpecificEventListenerMap = new ConcurrentHashMap<>();

    public static DataBridgeSubscriberService getDataBridgeSubscriberService() {
        return dataBridgeSubscriberService;
    }

    public static void setDataBridgeSubscriberService(DataBridgeSubscriberService dataBridgeSubscriberService) {
        WSO2EventSourceRegistrationManager.dataBridgeSubscriberService = dataBridgeSubscriberService;
    }

    public static WSO2EventMappingServiceImpl getWso2EventMappingService() {
        return wso2EventMappingService;
    }

    public static void setWso2EventMappingService(WSO2EventMappingServiceImpl wso2EventMappingService) {
        WSO2EventSourceRegistrationManager.wso2EventMappingService = wso2EventMappingService;
    }

    public static Map<String, List<SourceEventListener>> getStreamSpecificEventListenerMap() {
        return streamSpecificEventListenerMap;
    }

    public static void setStreamSpecificEventListenerMap(Map<String,
            List<SourceEventListener>> streamSpecificEventListenerMap) {
        WSO2EventSourceRegistrationManager.streamSpecificEventListenerMap = streamSpecificEventListenerMap;
    }

    public static void registerEventConsumer(String streamId, SourceEventListener sourceEventListener) {

        List<SourceEventListener> sourceEventListenerList = streamSpecificEventListenerMap.
                computeIfAbsent(streamId, k -> new ArrayList<>());
        sourceEventListenerList.add(sourceEventListener);
    }

    public static DataBridgeEventStreamService getDataBridgeEventStreamService() {
        return dataBridgeEventStreamService;
    }

    public static void setDataBridgeEventStreamService(DataBridgeEventStreamService dataBridgeEventStreamService) {
        WSO2EventSourceRegistrationManager.dataBridgeEventStreamService = dataBridgeEventStreamService;
    }

    public static void unregisterEventConsumer(String streamId, SourceEventListener sourceEventListener) {

        List<SourceEventListener> sourceEventListenerList = streamSpecificEventListenerMap.get(streamId);
        if (sourceEventListenerList != null) {
            sourceEventListenerList.remove(sourceEventListener);
        }
    }
}
