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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.databridge.commons.Credentials;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.core.AgentCallback;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Databridge Agent Callback Implementation
 */
public class AgentCallbackImpl implements AgentCallback {

    private static final Log LOGGER = LogFactory.getLog(AgentCallbackImpl.class);
    private boolean paused;
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    @Override
    public void definedStream(StreamDefinition streamDefinition) {

    }

    @Override
    public void removeStream(StreamDefinition streamDefinition) {

    }

    @Override
    public void receive(List<Event> list, Credentials credentials) {

        if (paused) {
            lock.lock();
            try {
                condition.await();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }

        for (Event event : list) {
            List<SourceEventListener> sourceEventListenerList = WSO2EventSourceRegistrationManager.
                    getStreamSpecificEventListenerMap().get(event.getStreamId());
            if (sourceEventListenerList != null) {
                for (SourceEventListener sourceEventListener : sourceEventListenerList) {
                    sourceEventListener.onEvent(event, null);
                }
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Event received at wso2Event source - " + event);
            }
        }
    }

    void pause() {
        paused = true;
    }

    void resume() {
        paused = false;
        try {
            lock.lock();
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}

