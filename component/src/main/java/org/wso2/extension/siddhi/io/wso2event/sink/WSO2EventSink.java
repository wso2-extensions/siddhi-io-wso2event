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

package org.wso2.extension.siddhi.io.wso2event.sink;

import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import org.wso2.siddhi.annotation.Example;
import org.wso2.siddhi.annotation.Extension;
import org.wso2.siddhi.core.config.ExecutionPlanContext;
import org.wso2.siddhi.core.exception.ConnectionUnavailableException;
import org.wso2.siddhi.core.stream.output.sink.Sink;
import org.wso2.siddhi.core.util.config.ConfigReader;
import org.wso2.siddhi.core.util.transport.DynamicOptions;
import org.wso2.siddhi.core.util.transport.OptionHolder;
import org.wso2.siddhi.query.api.definition.StreamDefinition;

import java.util.Map;

/**
 * WSO2Event output transport class.
 */
@Extension(
        name = "wso2event",
        namespace = "sink",
        description = "The WSO2Event source pushes wso2events via TCP (databridge) in `wso2event` format. " +
                "You can send wso2events through `Thrift` or `Binary` protocols.",
        examples = @Example(syntax = "@sink(type='wso2event', url=\"tcp://localhost:7611\", " +
                "auth.url=\"ssl://localhost:7711\", protocol=\"thrift\", username=\"admin\", password=\"admin\", " +
                "mode=\"non-blocking\" , @map(type='wso2event',  wso2.stream.id='fooStream:1.0.0'))\n" +
                "Define stream barStream(system string, price float, volume long);",
                description = "As defined in above query events are pushed to destination that defined.")
)
public class WSO2EventSink extends Sink {

    private static final Logger log = Logger.getLogger(WSO2EventSink.class);
    private DataPublisher dataPublisher;
    private String authUrl;
    private String url;
    private String username;
    private String password;
    private String publisherMode;
    private String protocol;
    private String executionPlanName;
    private int timeout;


    @Override
    protected void init(StreamDefinition outputStreamDefinition, OptionHolder optionHolder,
                        ConfigReader sinkConfigReader, ExecutionPlanContext executionPlanContext) {
        this.authUrl = optionHolder.validateAndGetStaticValue(WSO2EventSinkConstants.WSO2EVENT_SINK_AUTHENTICATION_URL,
                null);
        this.url = optionHolder.validateAndGetStaticValue(WSO2EventSinkConstants.WSO2EVENT_SINK_URL);
        this.username = optionHolder.validateAndGetStaticValue(WSO2EventSinkConstants.WSO2EVENT_SINK_USERNAME);
        this.password = optionHolder.validateAndGetStaticValue(WSO2EventSinkConstants.WSO2EVENT_SINK_PASSWORD);
        this.publisherMode = optionHolder.validateAndGetStaticValue(WSO2EventSinkConstants.
                WSO2EVENT_SINK_PUBLISHER_MODE, WSO2EventSinkConstants.DEFAULT_PUBLISHER_MODE);
        this.protocol = optionHolder.validateAndGetStaticValue(WSO2EventSinkConstants.
                WSO2EVENT_SINK_PUBLISHER_PROTOCOL, WSO2EventSinkConstants.DEFAULT_PUBLISHER_PROTOCOL);

        String timeoutAsString = optionHolder.validateAndGetStaticValue(WSO2EventSinkConstants.WSO2EVENT_SINK_TIMEOUT,
                null);
        if (timeoutAsString != null) {
            this.timeout = Integer.parseInt(timeoutAsString);
        }

        executionPlanName = executionPlanContext.getName();
    }

    @Override
    public void connect() throws ConnectionUnavailableException {
        try {
            dataPublisher = new DataPublisher(protocol, url, authUrl, username, password);
        } catch (DataEndpointAgentConfigurationException e) {
            throw new ConnectionUnavailableException(
                    "Error in event sink data-bridge client configuration given in " + executionPlanName
                            + " with the url:" + url + " authUrl:" + authUrl + " protocol:" + protocol
                            + " and userName:" + username + "," + e.getMessage(), e);
        } catch (DataEndpointException e) {
            throw new ConnectionUnavailableException(
                    "Error in connecting to databridge endpoint configuration given in " + executionPlanName
                            + " with the url:" + url + " authUrl:" + authUrl + " protocol:" + protocol
                            + " and userName:" + username + "," + e.getMessage(), e);
        } catch (DataEndpointConfigurationException e) {
            throw new ConnectionUnavailableException(
                    "Error in databridge endpoint configuration given in " + executionPlanName
                            + " with the url:" + url + " authUrl:" + authUrl + " protocol:" + protocol
                            + " and userName:" + username + "," + e.getMessage(), e);
        } catch (DataEndpointAuthenticationException e) {
            throw new ConnectionUnavailableException(
                    "Error while authenticating to databridge endpoint given in " + executionPlanName
                            + " with the url:" + url + " authUrl:" + authUrl + " protocol:" + protocol
                            + " and userName:" + username + "," + e.getMessage(), e);
        } catch (TransportException e) {
            throw new ConnectionUnavailableException(
                    "Transport exception occurred when connecting to databridge endpoint given in " + executionPlanName
                            + " with the url:" + url + " authUrl:" + authUrl + " protocol:" + protocol
                            + " and userName:" + username + "," + e.getMessage(), e);
        }

    }

    @Override
    public void publish(Object payload, DynamicOptions transportOptions) throws ConnectionUnavailableException {
        Event event = (Event) (payload);
        if (WSO2EventSinkConstants.DEFAULT_PUBLISHER_MODE.equalsIgnoreCase(publisherMode)) {
            dataPublisher.publish(event);
        } else {
            if (!dataPublisher.tryPublish(event, timeout)) {
                log.error("Event dropped at WSO2Event sink in executionplan '" + executionPlanName +
                        " , dropping event: " + event);
            }
        }
    }

    @Override
    public void disconnect() {
        if (dataPublisher != null) {
            try {
                dataPublisher.shutdown();
            } catch (DataEndpointException e) {
                log.error("Error in shutting down the data publisher created for execution plan " +
                        executionPlanName + " with the url:" + url + " authUrl:" + authUrl + " protocol:" +
                        protocol + " and userName:" + username + "," + e.getMessage(), e);
            }
        }
    }

    @Override
    public void destroy() {
        //not required
    }

    @Override
    public String[] getSupportedDynamicOptions() {
        return new String[]{};
    }

    @Override
    public Map<String, Object> currentState() {
        return null;
    }

    @Override
    public void restoreState(Map<String, Object> state) {

    }

}