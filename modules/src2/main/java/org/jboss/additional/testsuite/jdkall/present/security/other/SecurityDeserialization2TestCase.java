/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.additional.testsuite.jdkall.present.security.other;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.eap.additional.testsuite.annotations.ATTest;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * A sample program which acts a remote client for a EJB deployed on JBoss EAP
 * server. This program shows how to lookup stateful and stateless beans via
 * JNDI and then invoke on them
 *
 * @author Jaikiran Pai
 */
@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/security/src/main/java"})
public class SecurityDeserialization2TestCase {

    private static final String ARCHIVE_NAME = "SecurityDeserialization2TestCase";

    @Deployment
    public static Archive<?> deploy() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, ARCHIVE_NAME + ".jar");
        jar.addClass(SecurityDeserialization2TestCase.class);
        return jar;
    }

    @Test
    public void testSecuirtyDatabind() throws Exception {

        final String JSON = aposToQuotes(
                "{'id': 1111,\n"
                + " 'obj':[ 'org.apache.xalan.xsltc.trax.TemplatesImpl',\n"
                + "  {\n"
                + "    'transletBytecodes' : [ 'AAIAZQ==' ],\n"
                + "    'transletName' : 'a.b',\n"
                + "    'outputProperties' : { }\n"
                + "  }\n"
                + " ]\n"
                + "}"
        );
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();

        try {
            SerializedClass sc = mapper.readValue(JSON, SerializedClass.class);
            fail("Should not be able to deserialize because of security prevention.");
        }catch(JsonMappingException e){
            assertTrue("Fail because of security issues...",e.getMessage().contains("prevented for security reasons"));
        }

    }
    
    @Test
    public void testSecuirtyDatabind2() throws Exception {

        final String JSON = aposToQuotes(
                "{'v':['java.util.logging.FileHandler','/tmp/foobar.txt']}"
                 );

        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();

        try {
            PolyWrapper sc = mapper.readValue(JSON, PolyWrapper.class);
            fail("Should not be able to deserialize because of security prevention.");
        }catch(JsonMappingException e){
            assertTrue("Fail because of security issues...",e.getMessage().contains("prevented for security reasons"));
        }

    }
    
    @Test
    public void testSecuirtyDatabind3() throws Exception {

        final String JSON = aposToQuotes(
                "{'v':['java.rmi.server.UnicastRemoteObject','/tmp/foobar.txt']}"
                 );

        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();

        try {
            PolyWrapper sc = mapper.readValue(JSON, PolyWrapper.class);
            fail("Should not be able to deserialize because of security prevention.");
        }catch(JsonMappingException e){
            assertTrue("Fail because of security issues...",e.getMessage().contains("prevented for security reasons"));
        }

    }

    static class SerializedClass {

        public int id;
        public Object obj;
    }
    
    static class PolyWrapper {
         @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
                 include = JsonTypeInfo.As.WRAPPER_ARRAY)
         public Object v;
     }

    protected String aposToQuotes(String json) {
        return json.replace("'", "\"");
    }
}
