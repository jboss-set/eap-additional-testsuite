/*
 * JBoss, Home of Professional Open Source
 * Copyright 2019, Red Hat, Inc. and/or its affiliates, and individual
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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import java.time.Duration;
import java.time.Instant;

/**
 * Tests for deserialization of JSR310 objects.
 * See: https://issues.jboss.org/browse/JBEAP-16904
 *
 * @author pmackay@redhat.com
 */
@RunWith(Arquillian.class)
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/security/src/main/java#17.0.0.Final","modules/testcases/jdkAll/WildflyRelease-17.0.0.Final/security/src/main/java","modules/testcases/jdkAll/Eap72x/security/src/main/java#7.2.1","modules/testcases/jdkAll/Eap72x-Proposed/security/src/main/java#7.2.1","modules/testcases/jdkAll/Eap7/security/src/main/java#7.3.0.CD16"})
public class Jsr310DeserializationTestCase {

    // All the deserializations in this test case should return almost instantly
    @Rule
    public Timeout globalTimeout = Timeout.seconds(1);

    @Test
    public void testDurationDeserialization() throws Exception {
        ObjectReader reader = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .readerFor(Duration.class);

        final String INPUT = "1e10000000";

        // this should return almost instantly
        Duration value = reader.without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS).readValue(INPUT);
        Assert.assertEquals("Didn't get the expected value.", 0, value.getSeconds());

    }

    @Test
    public void testInstantDeserialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        final String INPUT = "1e10000000";

        // this should return almost instantly
        Instant value = mapper.readValue(INPUT, Instant.class);
        Assert.assertEquals("Didn't get the expected value.",0, value.getEpochSecond());
    }
}
