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

import io.siddhi.core.stream.input.source.SourceEventListener;
import org.wso2.carbon.databridge.core.DataBridgeStreamStore;
import org.wso2.carbon.databridge.core.DataBridgeSubscriberService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class which manages the Databridge receiver connections
 */
public class WSO2EventSourceRegistrationManager {

    private static DataBridgeSubscriberService dataBridgeSubscriberService;
    private static DataBridgeStreamStore dataBridgeStreamStore;
    private static Map<String, List<SourceEventListener>> streamSpecificEventListenerMap = new ConcurrentHashMap<>();
    private static AgentCallbackImpl agentCallbackImpl;

    static DataBridgeSubscriberService getDataBridgeSubscriberService() {
        return dataBridgeSubscriberService;
    }

    static void setDataBridgeSubscriberService(DataBridgeSubscriberService dataBridgeSubscriberService) {
        WSO2EventSourceRegistrationManager.dataBridgeSubscriberService = dataBridgeSubscriberService;
    }

    static Map<String, List<SourceEventListener>> getStreamSpecificEventListenerMap() {
        return streamSpecificEventListenerMap;
    }

    static DataBridgeStreamStore getDataBridgeStreamStore() {
        return dataBridgeStreamStore;
    }

    static void setDataBridgeStreamStore(DataBridgeStreamStore dataBridgeStreamStore) {
        WSO2EventSourceRegistrationManager.dataBridgeStreamStore = dataBridgeStreamStore;
    }

    static void registerEventConsumer(String streamId, SourceEventListener sourceEventListener) {

        List<SourceEventListener> sourceEventListenerList = streamSpecificEventListenerMap.
                computeIfAbsent(streamId, k -> new ArrayList<>());
        sourceEventListenerList.add(sourceEventListener);
    }

    static void unregisterEventConsumer(String streamId, SourceEventListener sourceEventListener) {
        if (streamId != null) {
            List<SourceEventListener> sourceEventListenerList = streamSpecificEventListenerMap.get(streamId);
            if (sourceEventListenerList != null) {
                sourceEventListenerList.remove(sourceEventListener);
            }
        }
    }

    static AgentCallbackImpl getAgentCallbackImpl() {
        return agentCallbackImpl;
    }

    static void setAgentCallbackImpl(AgentCallbackImpl agentCallbackImpl) {
        WSO2EventSourceRegistrationManager.agentCallbackImpl = agentCallbackImpl;
    }
}
