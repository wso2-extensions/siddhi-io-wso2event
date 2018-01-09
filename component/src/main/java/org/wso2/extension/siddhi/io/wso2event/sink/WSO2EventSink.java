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
import org.wso2.siddhi.annotation.Parameter;
import org.wso2.siddhi.annotation.util.DataType;
import org.wso2.siddhi.core.config.SiddhiAppContext;
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
        parameters = {
                @Parameter(name = "wso2.stream.id",
                          description = "Stream Id to use when publishing events. If " +
                          "stream id is not defined, it uses the respective siddhi stream name with version 1.0.0.",
                          defaultValue = "the defined stream id:1.0.0\n" +
                          "e.g.,if the stream definition is `org.wso2.stream.bar.stream`, " +
                          "then the value is `org.wso2.stream.bar.stream:1.0.0`.",
                          type = {DataType.STRING},
                          optional = true),
                @Parameter(name = "url",
                          description = "The URL to which the outgoing events published via " +
                          "TCP over Thrift or Binary. e.g., `tcp://localhost:7611`",
                          type = {DataType.STRING}),
                @Parameter(name = "auth.url",
                          description = "The Thrift/Binary server endpoint url which used for " +
                          "authentication purposes. It is not mandatory property. If this property is not provided " +
                          "then tcp-port+100 used for port in auth.url.",
                          type = {DataType.STRING},
                          optional = true,
                          defaultValue = "ssl://localhost:<tcp-port> + 100\n" +
                          "e.g., if the tcp port is 7611, then the value is `ssl://localhost:7711`"
                ),
                @Parameter(name = "username",
                          description = "The username is used for authentication flow before " +
                          "publishing events" +
                          "e.g., `admin`",
                          type = {DataType.STRING}),
                @Parameter(name = "password",
                          description = "The password is used for authentication flow before " +
                          "publishing events" + "e.g., `admin`",
                          type = {DataType.STRING}),
                @Parameter(name = "protocol",
                          description = "There are two protocols that we can use to publish " +
                          "events through data bridge.Either, we can use thrift or binary. Default value is Thrift" +
                           "e.g., `thrift`",
                          type = {DataType.STRING},
                          optional = true,
                          defaultValue = WSO2EventSinkConstants.DEFAULT_PUBLISHER_PROTOCOL),
                @Parameter(name = "mode",
                          description = "Property which decides whether to publish events in " +
                          "synchronous or asynchronous mode. Default is non-blocking mode." +
                          "e.g., `blocking`",
                          type = {DataType.STRING},
                          optional = true,
                          defaultValue = WSO2EventSinkConstants.DEFAULT_PUBLISHER_MODE),
        },
        examples = @Example(syntax = "@sink(type='wso2event', wso2.stream.id='fooStream:1.0.0'," +
                " url=\"tcp://localhost:7611\", auth.url=\"ssl://localhost:7711\", protocol=\"thrift\", " +
                "username=\"admin\", password=\"admin\", " +
                "mode=\"non-blocking\" , @map(type='wso2event'))\n" +
                "Define stream barStream(system string, price float, volume long);",
                description = "As defined in above query events are pushed to destination that defined.")
)
public class WSO2EventSink extends Sink {

    private static final Logger LOGGER = Logger.getLogger(WSO2EventSink.class);
    private DataPublisher dataPublisher;
    private String authUrl;
    private String url;
    private String username;
    private String password;
    private String publisherMode;
    private String protocol;
    private String siddhiAppName;
    private int timeout;
    private String streamId;


    @Override
    protected void init(StreamDefinition outputStreamDefinition, OptionHolder optionHolder,
                        ConfigReader sinkConfigReader, SiddhiAppContext siddhiAppContext) {
        this.authUrl = optionHolder.validateAndGetStaticValue(WSO2EventSinkConstants.WSO2EVENT_SINK_AUTHENTICATION_URL,
                null);
        this.url = optionHolder.validateAndGetStaticValue(WSO2EventSinkConstants.WSO2EVENT_SINK_URL);
        this.username = optionHolder.validateAndGetStaticValue(WSO2EventSinkConstants.WSO2EVENT_SINK_USERNAME);
        this.password = optionHolder.validateAndGetStaticValue(WSO2EventSinkConstants.WSO2EVENT_SINK_PASSWORD);
        this.publisherMode = optionHolder.validateAndGetStaticValue(WSO2EventSinkConstants.
                WSO2EVENT_SINK_PUBLISHER_MODE, WSO2EventSinkConstants.DEFAULT_PUBLISHER_MODE);
        this.protocol = optionHolder.validateAndGetStaticValue(WSO2EventSinkConstants.
                WSO2EVENT_SINK_PUBLISHER_PROTOCOL, WSO2EventSinkConstants.DEFAULT_PUBLISHER_PROTOCOL);
        this.streamId = optionHolder.validateAndGetStaticValue(WSO2EventSinkConstants.SOURCE_STREAM_ID,
                null);

        String timeoutAsString = optionHolder.validateAndGetStaticValue(WSO2EventSinkConstants.WSO2EVENT_SINK_TIMEOUT,
                null);
        if (timeoutAsString != null) {
            this.timeout = Integer.parseInt(timeoutAsString);
        }

        siddhiAppName = siddhiAppContext.getName();
    }

    @Override
    public void connect() throws ConnectionUnavailableException {
        try {
            dataPublisher = new DataPublisher(protocol, url, authUrl, username, password);
        } catch (DataEndpointAgentConfigurationException e) {
            throw new ConnectionUnavailableException(
                    "Error in event sink data-bridge client configuration given in " + siddhiAppName
                            + " with the url:" + url + " authUrl:" + authUrl + " protocol:" + protocol
                            + " and userName:" + username + "," + e.getMessage(), e);
        } catch (DataEndpointException e) {
            throw new ConnectionUnavailableException(
                    "Error in connecting to databridge endpoint configuration given in " + siddhiAppName
                            + " with the url:" + url + " authUrl:" + authUrl + " protocol:" + protocol
                            + " and userName:" + username + "," + e.getMessage(), e);
        } catch (DataEndpointConfigurationException e) {
            throw new ConnectionUnavailableException(
                    "Error in databridge endpoint configuration given in " + siddhiAppName
                            + " with the url:" + url + " authUrl:" + authUrl + " protocol:" + protocol
                            + " and userName:" + username + "," + e.getMessage(), e);
        } catch (DataEndpointAuthenticationException e) {
            throw new ConnectionUnavailableException(
                    "Error while authenticating to databridge endpoint given in " + siddhiAppName
                            + " with the url:" + url + " authUrl:" + authUrl + " protocol:" + protocol
                            + " and userName:" + username + "," + e.getMessage(), e);
        } catch (TransportException e) {
            throw new ConnectionUnavailableException(
                    "Transport exception occurred when connecting to databridge endpoint given in " + siddhiAppName
                            + " with the url:" + url + " authUrl:" + authUrl + " protocol:" + protocol
                            + " and userName:" + username + "," + e.getMessage(), e);
        }

    }

    @Override
    public void publish(Object payload, DynamicOptions transportOptions) throws ConnectionUnavailableException {

        Event event = (Event) (payload);
        if (streamId != null) {
            event.setStreamId(streamId);
        }

        if (WSO2EventSinkConstants.DEFAULT_PUBLISHER_MODE.equalsIgnoreCase(publisherMode)) {
            dataPublisher.publish(event);
        } else {
            if (!dataPublisher.tryPublish(event, timeout)) {
                LOGGER.error("Event dropped at WSO2Event sink in executionplan '" + siddhiAppName +
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
                LOGGER.error("Error in shutting down the data publisher created for execution plan " +
                        siddhiAppName + " with the url:" + url + " authUrl:" + authUrl + " protocol:" +
                        protocol + " and userName:" + username + "," + e.getMessage(), e);
            }
        }
    }

    @Override
    public void destroy() {
        //not required
    }

    @Override
    public Class[] getSupportedInputEventClasses() {
        return new Class[]{Event.class};
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
