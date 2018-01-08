/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.extension.siddhi.io.wso2event.test.osgi;

import org.apache.log4j.Logger;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.testng.listener.PaxExam;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.wso2.carbon.container.CarbonContainerFactory;
import org.wso2.carbon.databridge.agent.AgentHolder;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.commons.Credentials;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;
import org.wso2.carbon.databridge.core.AgentCallback;
import org.wso2.carbon.databridge.core.DataBridgeSubscriberService;
import org.wso2.carbon.kernel.CarbonServerInfo;
import org.wso2.extension.siddhi.io.wso2event.test.osgi.util.DataPublisherTestUtil;

import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.wso2.carbon.container.options.CarbonDistributionOption.carbonDistribution;
import static org.wso2.carbon.container.options.CarbonDistributionOption.copyOSGiLibBundle;

//import org.testng.Assert;

/**
 * WSO2Event Simple OSGI Tests.
 */

@Listeners(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
@ExamFactory(CarbonContainerFactory.class)
public class WSO2EventSimpleFlowTestcase {

    private static final String VERSION = "1.0.0";
    private Logger log = Logger.getLogger(WSO2EventSimpleFlowTestcase.class);
    private static AtomicInteger actualEventCount = new AtomicInteger(0);
    private String agentConfigFileName = "sync.data.agent.config.yaml";

    @Inject
    private CarbonServerInfo carbonServerInfo;

    @Inject
    private DataBridgeSubscriberService dataBridgeSubscriberService;

    @Configuration
    public Option[] createConfiguration() {
        return new Option[]{
                //CarbonDistributionOption.debug(5005),
                copyOSGiLibBundle(maven().artifactId("siddhi-io-wso2event").
                        groupId("org.wso2.extension.siddhi.io.wso2event")
                        .versionAsInProject()),
                copyOSGiLibBundle(maven().artifactId("siddhi-map-wso2event").
                        groupId("org.wso2.extension.siddhi.map.wso2event")
                        .version("4.0.8")),
                carbonDistribution(Paths.get("target", "wso2siddhi-io-wso2event-test-" +
                        System.getProperty("siddhi.io.wso2.version")), "worker")
        };
    }

    private void init() throws Exception {
        DataPublisherTestUtil.setKeyStoreParams();
        DataPublisherTestUtil.setTrustStoreParams();
    }

    @Test
    public void testBasicWSO2EventFlow() throws Exception {
        init();
        Awaitility.await().atLeast(4000, TimeUnit.MILLISECONDS);
        AgentHolder.setConfigPath(DataPublisherTestUtil.getDataAgentConfigPath(agentConfigFileName));
        String hostName = DataPublisherTestUtil.LOCAL_HOST;
        DataPublisher dataPublisher = new DataPublisher("Thrift", "tcp://" + hostName + ":7611",
                "ssl://" + hostName + ":7711", "admin", "admin");
        Event event = new Event();
        event.setStreamId(DataBridgeCommonsUtils.generateStreamId("FooStream", VERSION));
        event.setMetaData(new Object[]{"127.0.0.1"});
        event.setCorrelationData(null);
        event.setPayloadData(new Object[]{"WSO2", 123.4, 2, 12.4, 1.3});

        dataBridgeSubscriberService.subscribe(new AgentCallback() {
            @Override
            public void definedStream(StreamDefinition streamDefinition) {

            }

            @Override
            public void removeStream(StreamDefinition streamDefinition) {

            }

            @Override
            public void receive(List<Event> list, Credentials credentials) {
                for (Event event : list) {
                    if (event.getStreamId().equalsIgnoreCase("BarStream:1.0.0")) {
                        actualEventCount.incrementAndGet();
                    }
                }

            }
        });

        int numberOfEventsSent = 1000;
        for (int i = 0; i < numberOfEventsSent; i++) {
            dataPublisher.publish(event);
        }

        waitTillVariableCountMatches(1000, Duration.ONE_MINUTE);
        dataPublisher.shutdown();
    }

    private static void waitTillVariableCountMatches(long expected, Duration duration) {
        Awaitility.await().atMost(duration).until(() -> {
            return actualEventCount.get() == expected;
        });
    }

}
