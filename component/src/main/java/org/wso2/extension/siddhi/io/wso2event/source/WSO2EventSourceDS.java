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
import org.wso2.carbon.databridge.core.DataBridgeReceiverService;
import org.wso2.carbon.databridge.core.DataBridgeStreamStore;
import org.wso2.carbon.databridge.core.DataBridgeSubscriberService;
import org.wso2.siddhi.core.exception.ConnectionUnavailableException;
import org.wso2.siddhi.core.stream.input.source.Source;

import java.util.Iterator;

/**
 * Service component to consume DataBridgeReceiver Service.
 */
@Component(
        name = "org.wso2.extension.siddhi.io.wso2event.source.WSO2EventSourceDS",
        immediate = true
)
public class WSO2EventSourceDS {
    private static final Log LOGGER = LogFactory.getLog(WSO2EventSourceDS.class);
    private boolean isEventStreamServiceActive;
    private boolean isReceiverServiceActive;

    /**
     * This is the activation method of WSO2EventSource service. This will be called when its references are
     * satisfied. Agent server is initialized here
     *
     * @param bundleContext the bundle context instance of this bundle.
     * @throws Exception this will be thrown if an issue occurs while executing the activate method
     */
    @Activate
    protected void start(BundleContext bundleContext) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("WSO2EventSource Component is started");
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
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("WSO2EventSource Component is stopped");
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
            AgentCallbackImpl agentCallback = new AgentCallbackImpl();
            WSO2EventSourceRegistrationManager.setAgentCallbackImpl(agentCallback);
            dataBridgeSubscriberService.subscribe(agentCallback);
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

    /**
     * This bind method will be called when DataBridgeStreamStore OSGi service is registered.
     *
     * @param dataBridgeStreamStore The DataBridgeStreamStore instance registered by databridge
     *                              as an OSGi service
     */
    @Reference(
            name = "databridge.stream.store",
            service = DataBridgeStreamStore.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetDataBridgeEventStreamService"
    )
    protected void setDataBridgeEventStreamService(DataBridgeStreamStore dataBridgeStreamStore) {
        WSO2EventSourceRegistrationManager.setDataBridgeStreamStore(dataBridgeStreamStore);
        isEventStreamServiceActive = true;
        if (isReceiverServiceActive) {
            WSO2EventSourceDataHolder.setDatabridgeActivated(true);
            connectSources();
        }
    }

    /**
     * This is the unbind method which gets called at the un-registration of DataBridgeStreamStore OSGi service.
     *
     * @param dataBridgeStreamStore The DataBridgeStreamStore instance registered by databridge
     *                              as an OSGi service
     */
    protected void unsetDataBridgeEventStreamService(DataBridgeStreamStore dataBridgeStreamStore) {
        WSO2EventSourceRegistrationManager.setDataBridgeStreamStore(null);
        WSO2EventSourceDataHolder.setDatabridgeActivated(false);
        isEventStreamServiceActive = false;
    }

    /**
     * This bind method will be called when DataBridgeReceiverService OSGi service is registered.
     *
     * @param dataBridgeReceiverService The DataBridgeReceiverService instance registered by databridge
     *                                  as an OSGi service
     */
    @Reference(
            name = "databridge.receiver.service",
            service = DataBridgeReceiverService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetDataBridgeReceiverService"
    )
    protected void setDataBridgeReceiverService(DataBridgeReceiverService dataBridgeReceiverService) {
        isReceiverServiceActive = true;
        if (isEventStreamServiceActive) {
            WSO2EventSourceDataHolder.setDatabridgeActivated(true);
            connectSources();
        }
    }

    /**
     * This is the unbind method which gets called at the un-registration of DataBridgeReceiverService OSGi service.
     *
     * @param dataBridgeReceiverService The DataBridgeReceiverService instance registered by databridge
     *                                  as an OSGi service
     */
    protected void unsetDataBridgeReceiverService(DataBridgeReceiverService dataBridgeReceiverService) {
        WSO2EventSourceRegistrationManager.setDataBridgeStreamStore(null);
        WSO2EventSourceDataHolder.setDatabridgeActivated(false);
        isReceiverServiceActive = false;
    }

    private void connectSources() {

        for (Iterator<Source> iterator = WSO2EventSourceDataHolder.getSources().iterator(); iterator.hasNext(); ) {
            Source source = iterator.next();
            try {
                ((WSO2EventSource) source).connect();
            } catch (ConnectionUnavailableException e) {
                LOGGER.error("Exception when generating the WSO2 stream definition for Source "
                        + source.getElementId(), e);
            }
            iterator.remove();
        }
    }
}
