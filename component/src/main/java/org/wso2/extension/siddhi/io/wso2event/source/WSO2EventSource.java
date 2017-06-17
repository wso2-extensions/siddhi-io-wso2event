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

import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.extension.siddhi.map.wso2event.WSO2SourceMapper;
import org.wso2.siddhi.annotation.Example;
import org.wso2.siddhi.annotation.Extension;
import org.wso2.siddhi.core.config.ExecutionPlanContext;
import org.wso2.siddhi.core.exception.ConnectionUnavailableException;
import org.wso2.siddhi.core.stream.input.source.Source;
import org.wso2.siddhi.core.stream.input.source.SourceEventListener;
import org.wso2.siddhi.core.util.config.ConfigReader;
import org.wso2.siddhi.core.util.transport.OptionHolder;

import java.util.Map;

/**
 * This processes the WSO2Event messages.
 */
@Extension(
        name = "wso2event",
        namespace = "source",
        description = "The WSO2Event source receives wso2events via TCP (databridge) in `wso2event` format. " +
                "You can receive wso2events through `Thrift` or `Binary` protocols.",
        examples = @Example(syntax =
                "@source(type='wso2event', @map(type='wso2event', wso2.stream.id='inputstream:1.0.0'))\n" +
                        "Define stream Foo (symbol string, price float, volume long);",
                description = "As defined in above query events are received to stream id that defined in mapping.")
)
public class WSO2EventSource extends Source {

    private SourceEventListener sourceEventListener;
    private OptionHolder optionHolder;

    @Override
    public void init(SourceEventListener sourceEventListener, OptionHolder optionHolder, ConfigReader configReader,
                     ExecutionPlanContext executionPlanContext) {

        this.sourceEventListener = sourceEventListener;
        this.optionHolder = optionHolder;
        executionPlanContext.getSnapshotService().addSnapshotable("wso2event-source", this);
    }

    @Override
    public void connect() throws ConnectionUnavailableException {
        String streamId = ((WSO2SourceMapper) sourceEventListener).getInputStreamId();
        StreamDefinition streamDefinition = WSO2EventSourceRegistrationManager.getWso2EventMappingService().
                getStreamDefinition(streamId);
        WSO2EventSourceRegistrationManager.getDataBridgeEventStreamService().addStreamDefinition(streamDefinition);
        WSO2EventSourceRegistrationManager.registerEventConsumer(streamId, sourceEventListener);
    }

    @Override
    public void disconnect() {
        String streamId = optionHolder.validateAndGetStaticValue(WSO2EventSourceConstants.SOURCE_STREAM_ID);
        WSO2EventSourceRegistrationManager.unregisterEventConsumer(streamId, sourceEventListener);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public Map<String, Object> currentState() {
        return null;
    }

    @Override
    public void restoreState(Map<String, Object> map) {

    }
}
