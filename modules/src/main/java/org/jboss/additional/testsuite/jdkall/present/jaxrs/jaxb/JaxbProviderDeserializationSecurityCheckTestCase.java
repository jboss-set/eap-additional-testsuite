/*
 * JBoss, Home of Professional Open Source
 * Copyright 2018, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.additional.testsuite.jdkall.present.jaxrs.jaxb;

import com.fasterxml.jackson.databind.JsonMappingException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.common.HttpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.springframework.jacksontest.BogusApplicationContext;
import org.springframework.jacksontest.BogusPointcutAdvisor;
import com.mchange.v2.c3p0.jacksonTest.ComboPooledDataSource;
import org.apache.ibatis.datasource.jndi.JndiDataSourceFactory;
import org.springframework.jacksontest.AbstractPointcutAdvisor;
import org.springframework.jacksontest.AbstractApplicationContext;
import org.hibernate.jmx.StatisticsService;
import org.apache.openjpa.ee.JNDIManagedRuntime;
import org.jboss.eap.additional.testsuite.annotations.EATDPM;
import org.jboss.eap.additional.testsuite.annotations.ATTest;

/**
 * Tests a JAX-RS deployment without an application bundled.
 *
 * The container should register a servlet with the name
 *
 * javax.ws.rs.core.Application
 *
 * It is the app providers responsibility to provide a mapping for the servlet
 *
 * JAX-RS 1.1 2.3.2 bullet point 1
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/jaxrs/src/main/java","modules/testcases/jdkAll/Wildfly/jaxrs/src/main/java#13.0.0","modules/testcases/jdkAll/Eap72x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/jaxrs/src/main/java","modules/testcases/jdkAll/Eap71x/jaxrs/src/main/java#7.1.2", "modules/testcases/jdkAll/Eap71x-Proposed/jaxrs/src/main/java#7.1.2","modules/testcases/jdkAll/Eap7/jaxrs/src/main/java#7.1.2"})
//@ATDPM(config="standalone.xml", features={"org.wildfly:wildfly-feature-pack-new#jaxrs:subsystem=jaxrs"}, minVersions={"13.0.0"}, maxVersions={"null"}, isClassAnnotation="true")
//@ATDPM(config="standalone.xml", features={"org.wildfly:wildfly-feature-pack-new#subsystem.jaxrs:subsystem=jaxrs"}, minVersions={"13.0.0"}, maxVersions={"null"}, isClassAnnotation="true")
public class JaxbProviderDeserializationSecurityCheckTestCase {

    @Deployment(testable = false)
    public static Archive<?> deploy() {
        WebArchive war = ShrinkWrap.create(WebArchive.class,"jaxrssecurity.war");
        war.addPackage(HttpRequest.class.getPackage());
        war.addClasses(JaxbProviderDeserializationSecurityCheckTestCase.class, JaxbResourceDeserializationSecurityCheck.class,ExampleApplication.class,
                       org.springframework.jacksontest.BogusPointcutAdvisor.class, org.springframework.jacksontest.AbstractPointcutAdvisor.class,
                       org.springframework.jacksontest.BogusApplicationContext.class, org.springframework.jacksontest.AbstractApplicationContext.class,
                       org.apache.ibatis.datasource.jndi.JndiDataSourceFactory.class, org.hibernate.jmx.StatisticsService.class,
                       TestMapperResolver.class,com.mchange.v2.c3p0.jacksonTest.ComboPooledDataSource.class, org.apache.openjpa.ee.JNDIManagedRuntime.class,
                       org.apache.openjpa.ee.ManagedRuntime.class,org.apache.openjpa.ee.AbstractManagedRuntime.class);
        war.addPackage(org.apache.openjpa.util.GeneralException.class.getPackage());
        war.addPackage(org.apache.openjpa.lib.util.Localizer.class.getPackage());
        return war;
    }

    @ArquillianResource
    private URL url;

    private String performCall(String urlPattern) throws Exception {
        return HttpRequest.get(url + urlPattern, 10, TimeUnit.SECONDS);
    }

    @Test
    @EATDPM(config="standalone.xml", features={"org.wildfly:wildfly-feature-pack-new#jaxrs:subsystem=jaxrs"}, minVersions={"13.0.0"}, maxVersions={"null"}, isClassAnnotation="false", excludeDependencies={"javax.ws.rs.GET;"})
    //@EATDPM(config="standalone.xml", features={"org.wildfly:wildfly-feature-pack-new#subsystem.jaxrs:subsystem=jaxrs"}, minVersions={"13.0.0"}, maxVersions={"null"}, isClassAnnotation="false", excludeDependencies={"javax.ws.rs.GET;"})
    public void testPointcutAdvisor() throws Exception {
        String result = performCall("rest/jaxb/advisor");

        try{
            BogusPointcutAdvisor jaxbModel = new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).readValue(result, BogusPointcutAdvisor.class);
            Assert.fail("Should prevente json deserialization because of security reasons.");
        }catch(JsonMappingException e){
            Assert.assertTrue("Should prevente json deserialization because of security reasons.", e.getMessage().contains("Illegal type"));
        }

    }

    @EATDPM(config="standalone.xml", features={"org.wildfly:wildfly-feature-pack-new#jaxrs:subsystem=jaxrs"}, minVersions={"13.0.0"}, maxVersions={"null"}, isClassAnnotation="false", excludeDependencies={"javax.ws.rs.GET;"})
    //@EATDPM(config="standalone.xml", features={"org.wildfly:wildfly-feature-pack-new#subsystem.jaxrs:subsystem=jaxrs"}, minVersions={"13.0.0"}, maxVersions={"null"}, isClassAnnotation="false", excludeDependencies={"javax.ws.rs.GET;"})
    @Test
    public void testApplicationContext() throws Exception {
        String result = performCall("rest/jaxb/appcontext");

        try{
            BogusApplicationContext jaxbModel = new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).readValue(result, BogusApplicationContext.class);
            Assert.fail("Should prevente json deserialization because of security reasons.");
        }catch(JsonMappingException e){
            Assert.assertTrue("Should prevente json deserialization because of security reasons.", e.getMessage().contains("Illegal type"));
        }

    }
    
    @ATTest({"modules/testcases/jdkAll/Wildfly/jaxrs/src/main/java#13.0.0.Alpha1","modules/testcases/jdkAll/Eap71x/jaxrs/src/main/java#7.1.3","modules/testcases/jdkAll/Eap71x-Proposed/jaxrs/src/main/java#7.1.3", "modules/testcases/jdkAll/Eap7/jaxrs/src/main/java#7.1.2","modules/testcases/jdkAll/Eap72x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/jaxrs/src/main/java"})
    public void testMChangeV2C3p0() throws Exception {
        String result = performCall("rest/jaxb/mchange");

        try{
            ComboPooledDataSource jaxbModel = new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).readValue(result, ComboPooledDataSource.class);
            Assert.fail("Should prevente json deserialization because of security reasons.");
        }catch(JsonMappingException e){
            Assert.assertTrue("Should prevente json deserialization because of security reasons.", e.getMessage().contains("Illegal type"));
        }

    }

    @ATTest({"modules/testcases/jdkAll/Wildfly/jaxrs/src/main/java#13.0.0","modules/testcases/jdkAll/Eap71x/jaxrs/src/main/java#7.1.2","modules/testcases/jdkAll/Eap71x-Proposed/jaxrs/src/main/java#7.1.2", "modules/testcases/jdkAll/Eap7/jaxrs/src/main/java#7.1.2","modules/testcases/jdkAll/Eap72x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/jaxrs/src/main/java"})
    public void testStatisticsService() throws Exception{
        String result = performCall("rest/jaxb/statistics");

        try{
            StatisticsService jaxbModel = new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).readValue(result, StatisticsService.class);
            Assert.fail("Should prevente json deserialization because of security reasons.");
        }catch(JsonMappingException e){
            Assert.assertTrue("Should prevente json deserialization because of security reasons.", e.getMessage().contains("Illegal type"));
        }

    }

    @ATTest({"modules/testcases/jdkAll/Wildfly/jaxrs/src/main/java#16.0.0.Beta1","modules/testcases/jdkAll/Eap71x/jaxrs/src/main/java#7.1.6","modules/testcases/jdkAll/Eap71x-Proposed/jaxrs/src/main/java#7.1.6"})
    public void testOpenjpaService() throws Exception{
        String result = performCall("rest/jaxb/openjpa");

        try{
            JNDIManagedRuntime jaxbModel = new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).readValue(result, JNDIManagedRuntime.class);
            Assert.fail("Should prevente json deserialization because of security reasons.");
        }catch(JsonMappingException e){
            Assert.assertTrue("Should prevente json deserialization because of security reasons.", e.getMessage().contains("Illegal type"));
        }

    }

    @ATTest({"modules/testcases/jdkAll/Wildfly/jaxrs/src/main/java#13.0.0","modules/testcases/jdkAll/Eap71x/jaxrs/src/main/java#7.1.2","modules/testcases/jdkAll/Eap71x-Proposed/jaxrs/src/main/java#7.1.2", "modules/testcases/jdkAll/Eap7/jaxrs/src/main/java#7.1.2","modules/testcases/jdkAll/Eap72x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/jaxrs/src/main/java"})
    public void testMyBatisJndiDataSourceFactory() throws Exception{
        String result = performCall("rest/jaxb/datasource");

        try{
            JndiDataSourceFactory jaxbModel = new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).readValue(result, JndiDataSourceFactory.class);
            Assert.fail("Should prevente json deserialization because of security reasons.");
        }catch(JsonMappingException e){
            Assert.assertTrue("Should prevente json deserialization because of security reasons.", e.getMessage().contains("Illegal type"));
        }
    }

    @Test
    public void defaultTest(){
        System.out.println("Adding a default test for usage of this class with the EAT workshop");
    }

}
