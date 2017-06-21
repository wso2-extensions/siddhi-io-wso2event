/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.testng.listener.PaxExam;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.wso2.carbon.container.CarbonContainerFactory;
import org.wso2.carbon.kernel.utils.CarbonServerInfo;

import java.util.List;
import javax.inject.Inject;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.wso2.carbon.container.options.CarbonDistributionOption.copyOSGiLibBundle;

//import org.testng.Assert;
//import org.wso2.siddhi.core.ExecutionPlanRuntime;
//import org.wso2.siddhi.core.SiddhiManager;
//import org.wso2.siddhi.core.event.Event;
//import org.wso2.siddhi.core.query.output.callback.QueryCallback;
//import org.wso2.siddhi.core.util.EventPrinter;
//import org.wso2.siddhi.core.util.persistence.InMemoryPersistenceStore;
//import org.wso2.siddhi.core.util.persistence.PersistenceStore;
//import org.wso2.siddhi.extension.input.mapper.xml.XmlSourceMapper;
//
//import java.net.URI;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;


//import static org.ops4j.pax.exam.CoreOptions.maven;
//import static org.wso2.carbon.container.options.CarbonDistributionOption.copyFile;
//import static org.wso2.carbon.container.options.CarbonDistributionOption.copyOSGiLibBundle;


/**
 * JAAS OSGI Tests.
 */

@Listeners(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
@ExamFactory(CarbonContainerFactory.class)
public class BasicAuthTrue {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.
            getLogger(BasicAuthTrue.class);
    private List<String> receivedEventNameList;


    @Inject
    private CarbonServerInfo carbonServerInfo;


    @Configuration
    public Option[] createConfiguration() {
        return new Option[]{
                copyOSGiLibBundle(maven().artifactId("siddhi-io-wso2event").
                        groupId("org.wso2.extension.siddhi.io.wso2event")
                        .versionAsInProject()),
                copyOSGiLibBundle(maven().artifactId("siddhi-map-wso2event").
                        groupId("org.wso2.extension.siddhi.map.wso2event")
                        .version("4.0.0-M5-SNAPSHOT"))
        };
    }

    @Test
    public void testHTTPInputTransportBasicAuthFalse() throws Exception {

        logger.info("FFFFFFFFFFFF");
//        URI baseURI = URI.create(String.format("http://%s:%d", "localhost", 8055));
//        receivedEventNameList = new ArrayList<>(2);
//        PersistenceStore persistenceStore = new InMemoryPersistenceStore();
//        SiddhiManager siddhiManager = new SiddhiManager();
//        siddhiManager.setPersistenceStore(persistenceStore);
//        siddhiManager.setExtension("xml-input-mapper", XmlSourceMapper.class);
//        String inStreamDefinition = "" + "@source(type='http', @map(type='xml'), "
//                + "receiver.url='http://localhost:8055/endpoints/RecPro', " + "is.basic.auth.enabled='false'" + ")"
//                + "define stream inputStream (name string, age int, country string);";
//        String query = ("@info(name = 'query1') " + "from inputStream " + "select *  " + "insert into outputStream;");
//        ExecutionPlanRuntime executionPlanRuntime = siddhiManager
//                .createExecutionPlanRuntime(inStreamDefinition + query);
//        executionPlanRuntime.addCallback("query1", new QueryCallback() {
//            @Override
//            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
//                EventPrinter.print(timeStamp, inEvents, removeEvents);
//                for (Event event : inEvents) {
//                    receivedEventNameList.add(event.getData(0).toString());
//                }
//            }
//        });
//        executionPlanRuntime.start();
//        // publishing events
//        List<String> expected = new ArrayList<>(2);
//        expected.add("John");
//        expected.add("Mike");
//        String event1 =
//                "<events><event><name>John</name>" + "<age>100</age><country>Sri Lanka</country></event></events>";
//        String event2 = "<events><event><name>Mike</name>" + "<age>20</age><country>USA</country></event></events>";
//        new TestUtil().httpPublishEvent(event1, baseURI, "/endpoints/RecPro", false, "text/xml",
//                "POST");
//        new TestUtil().httpPublishEvent(event2, baseURI, "/endpoints/RecPro", false, "text/xml",
//                "POST");
//        Thread.sleep(200);
//        logger.info(receivedEventNameList);
//        Assert.assertEquals(receivedEventNameList, expected);
//        executionPlanRuntime.shutdown();
    }

}
