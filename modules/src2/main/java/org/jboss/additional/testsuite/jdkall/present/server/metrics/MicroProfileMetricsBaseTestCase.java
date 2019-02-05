/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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

package org.jboss.additional.testsuite.jdkall.present.server.metrics;

import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.test.integration.management.ManagementOperations;
import org.jboss.as.test.integration.management.util.MgmtOperationException;
import org.jboss.dmr.ModelNode;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * Test required base metrics that are always present.
 *
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2018 Red Hat inc.
 */
@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/server/src/main/java@9a9b7d2c4fafbfa118b01a2677bb01aaf302637b"})
public class MicroProfileMetricsBaseTestCase {

    // Use an empty deployment as the test deals with base metrics only
    @Deployment(name = "MicroProfileMetricsBaseTestCase", managed = false)
    public static Archive<?> deploy() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "MicroProfileMetricsBaseTestCase.war");
        return war;
    }

    @ContainerResource
    ManagementClient managementClient;

    @Test
    public void testUnknownExposedSubsystem() throws Exception {
        final ModelNode address = Operations.createAddress("subsystem", "microprofile-metrics-smallrye");
        final ModelNode op = Operations.createWriteAttributeOperation(address, "exposed-subsystems", new ModelNode().add("bad").add("unknown"));
        try {
            ManagementOperations.executeOperation(managementClient.getControllerClient(), op);
            Assert.fail("update exposed subsystems to unknown ones should fail");
        } catch (MgmtOperationException e) {
            ModelNode result = e.getResult();
            assertTrue(result.get("failure-description").asString().contains("WFLYMETRICS0006"));
        }

    }
}
