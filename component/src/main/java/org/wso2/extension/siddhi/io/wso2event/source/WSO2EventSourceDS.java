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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.databridge.commons.Credentials;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.core.AgentCallback;
import org.wso2.carbon.databridge.core.DataBridgeSubscriberService;
import org.wso2.siddhi.core.stream.input.source.SourceEventListener;

import java.util.List;

/**
 * Service component to consume DataBridgeReceiver Service.
 */
@Component(
        name = "org.wso2.extension.siddhi.io.wso2event.source.WSO2EventSourceDS",
        immediate = true
)
public class WSO2EventSourceDS {
    private static final Log log = LogFactory.getLog(WSO2EventSourceDS.class);

    /**
     * This is the activation method of WSO2EventSource service. This will be called when its references are
     * satisfied. Agent server is initialized here
     *
     * @param bundleContext the bundle context instance of this bundle.
     * @throws Exception this will be thrown if an issue occurs while executing the activate method
     */
    @Activate
    protected void start(BundleContext bundleContext) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("WSO2EventSource Component is started");
        }
    }

    /**
     * This is the deactivation method of WSO2EventSource service. This will be called when this component
     * is being stopped or references are satisfied during runtime.
     *
     * @throws Exception this will be thrown if an issue occurs while executing the de-activate method
     */
    @Deactivate
    protected void stop() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("WSO2EventSource Component is stoped");
        }
    }

    /**
     * This bind method will be called when DataBridgeSubscriberService OSGi service is registered.
     *
     * @param dataBridgeSubscriberService The DataBridgeSubscriberService instance registered by databridge
     *                                    as an OSGi service
     */
    @Reference(
            name = "databridge.subscriber.service",
            service = DataBridgeSubscriberService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetDataBridgeSubscriberService"
    )
    protected void setDataBridgeSubscriberService(DataBridgeSubscriberService dataBridgeSubscriberService) {
        if (WSO2EventSourceRegistrationManager.getDataBridgeSubscriberService() == null) {
            WSO2EventSourceRegistrationManager.setDataBridgeSubscriberService(dataBridgeSubscriberService);
            dataBridgeSubscriberService.subscribe(new AgentCallbackInner());
        }

    }

    /**
     * This is the unbind method which gets called at the un-registration of DataBridgeSubscriberService OSGi service.
     *
     * @param dataBridgeSubscriberService The DataBridgeSubscriberService instance registered by databridge
     *                                    as an OSGi service
     */
    protected void unsetDataBridgeSubscriberService(DataBridgeSubscriberService dataBridgeSubscriberService) {
        WSO2EventSourceRegistrationManager.setDataBridgeSubscriberService(null);
    }


    static class AgentCallbackInner implements AgentCallback {

        @Override
        public void definedStream(StreamDefinition streamDefinition) {

        }

        @Override
        public void removeStream(StreamDefinition streamDefinition) {

        }

        @Override
        public void receive(List<Event> list, Credentials credentials) {
            for (Event event : list) {
                List<SourceEventListener> sourceEventListenerList = WSO2EventSourceRegistrationManager.
                        getStreamSpecificEventListenerMap().get(event.getStreamId());
                if (sourceEventListenerList != null) {
                    for (SourceEventListener sourceEventListener : sourceEventListenerList) {
                        sourceEventListener.onEvent(event);
                    }
                }
                if (log.isDebugEnabled()) {
                    log.debug("Event received at wso2Event source - " + event);
                }
            }
        }
    }

}
