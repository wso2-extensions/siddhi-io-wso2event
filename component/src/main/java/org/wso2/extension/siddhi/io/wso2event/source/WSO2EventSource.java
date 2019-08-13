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

import io.siddhi.annotation.Example;
import io.siddhi.annotation.Extension;
import io.siddhi.annotation.Parameter;
import io.siddhi.annotation.util.DataType;
import io.siddhi.core.config.SiddhiAppContext;
import io.siddhi.core.exception.ConnectionUnavailableException;
import io.siddhi.core.stream.ServiceDeploymentInfo;
import io.siddhi.core.stream.input.source.Source;
import io.siddhi.core.stream.input.source.SourceEventListener;
import io.siddhi.core.util.config.ConfigReader;
import io.siddhi.core.util.snapshot.state.State;
import io.siddhi.core.util.snapshot.state.StateFactory;
import io.siddhi.core.util.transport.OptionHolder;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.extension.siddhi.map.wso2event.source.WSO2SourceMapper;

/**
 * This processes the WSO2Event messages.
 */
@Extension(
        name = "wso2event",
        namespace = "source",
        description = "The WSO2Event source receives wso2events via TCP (databridge) in `wso2event` format. " +
                "You can receive wso2events through `Thrift` or `Binary` protocols.",
        parameters = {
                @Parameter(name = "wso2.stream.id",
                        description = "Stream Id to consume events. If stream id is not " +
                        "defined, it uses the respective siddhi stream definition with version 1.0.0 \n",
                        defaultValue = "`the defined stream id:1.0.0`. \n" +
                        "(e.g. if the stream definition is `org.wso2.stream.bar.stream`, " +
                        "then the value is `org.wso2.stream.bar.stream:1.0.0`.)",
                        type = {DataType.STRING})
        },
        examples = @Example(syntax =
        "@source(type='wso2event', wso2.stream.id='inputstream:1.0.0', @map(type='wso2event')) \n" +
                "Define stream Foo (symbol string, price float, volume long);",
        description = "As defined in above query events are received to stream id that defined in source.")
)
public class WSO2EventSource extends Source {

    private SourceEventListener sourceEventListener;
    private OptionHolder optionHolder;
    private String streamId;

    @Override
    public StateFactory init(SourceEventListener sourceEventListener,
                                          OptionHolder optionHolder,
                                          String[] requestedTransportPropertyNames,
                                          ConfigReader configReader,
                                          SiddhiAppContext siddhiAppContext) {
        this.sourceEventListener = sourceEventListener;
        this.optionHolder = optionHolder;
        return null;
    }

    @Override
    protected ServiceDeploymentInfo exposeServiceDeploymentInfo() {
        return null;
    }

    @Override
    public void connect(ConnectionCallback connectionCallback, State state)
            throws ConnectionUnavailableException {
        streamId = optionHolder.validateAndGetStaticValue(WSO2EventSourceConstants.SOURCE_STREAM_ID, null);
        if (!WSO2EventSourceDataHolder.isDatabridgeActivated()) {
            // Source connection delayed until data bridge core activated
            WSO2EventSourceDataHolder.getSources().add(this);
        } else {
            connect();
        }
    }

    protected void connect() throws ConnectionUnavailableException {
        StreamDefinition streamDefinition = ((WSO2SourceMapper) getMapper()).getWSO2StreamDefinition();

        if (streamId != null) {
            String[] streamIdArray = streamId.split(":");
            try {
                StreamDefinition sourceStreamDefinition = new StreamDefinition(streamIdArray[0], streamIdArray[1]);
                sourceStreamDefinition.setTags(streamDefinition.getTags());
                sourceStreamDefinition.setDescription(streamDefinition.getDescription());
                sourceStreamDefinition.setNickName(streamDefinition.getNickName());
                sourceStreamDefinition.setMetaData(streamDefinition.getMetaData());
                sourceStreamDefinition.setCorrelationData(streamDefinition.getCorrelationData());
                sourceStreamDefinition.setPayloadData(streamDefinition.getPayloadData());
                streamDefinition = sourceStreamDefinition;
            } catch (MalformedStreamDefinitionException e) {
                throw new ConnectionUnavailableException("Exception when generating the WSO2 stream definition", e);
            }
        } else {
            streamId = streamDefinition.getStreamId();
        }

        WSO2EventSourceRegistrationManager.getDataBridgeStreamStore().addStreamDefinition(streamDefinition);
        WSO2EventSourceRegistrationManager.registerEventConsumer(streamId, sourceEventListener);
    }

    @Override
    public Class[] getOutputEventClasses() {
        return new Class[]{Event.class};
    }

    @Override
    public void disconnect() {
        WSO2EventSourceRegistrationManager.unregisterEventConsumer(streamId, sourceEventListener);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void pause() {
        WSO2EventSourceRegistrationManager.getAgentCallbackImpl().pause();
    }

    @Override
    public void resume() {
        WSO2EventSourceRegistrationManager.getAgentCallbackImpl().resume();
    }

}
