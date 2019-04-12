/*
 * JBoss, Home of Professional Open Source
 * Copyright 2018, Red Hat, Inc. and/or its affiliates, and individual
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

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.ext.EventData;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/security/src/main/java","modules/testcases/jdkAll/Wildfly/security/src/main/java","modules/testcases/jdkAll/Eap72x/security/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/security/src/main/java","modules/testcases/jdkAll/Eap71x/security/src/main/java"})
public class SecuritySlf4jTestCase {


    @Test
    public void testSecuirtyDatabind() throws Exception {

        String xml = new String("<?xml version='1.0' encoding='utf-8'?>"
			+ "<test>test</test>");
        try {
            EventData e = new EventData(xml);
            Assert.fail("EventData constructor should throw a SecurityException.");
        } catch(SecurityException e){
            // this is expected
        }

    }
    
}
