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

package org.jboss.additional.testsuite.jdkall.present.manualmode.logging;

import org.apache.commons.io.IOUtils;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.LEVEL;

/**
 * This test ensures that log4j Logger#getEffectiveLevel method always returns the correct logging level.
 * See https://issues.jboss.org/browse/JBEAP-14214 for further details.
 *
 * @author <a href="mailto:pmackay@redhat.com">Peter Mackay</a>
 */
@RunAsClient
@RunWith(Arquillian.class)
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap7/manualmode/src/main/java","modules/testcases/jdkAll/Eap72x/manualmode/src/main/java", "modules/testcases/jdkAll/Eap72x-Proposed/manualmode/src/main/java", "modules/testcases/jdkAll/Eap71x/manualmode/src/main/java#7.1.5", "modules/testcases/jdkAll/Eap71x-Proposed/manualmode/src/main/java#7.1.5", "modules/testcases/jdkAll/Wildfly/manualmode/src/main/java#14.0.0","modules/testcases/jdkAll/WildflyRelease-17.0.0.Final/manualmode/src/main/java"})
public class Log4jEffectiveLevelTestCase {

    private static final String LOG_LEVEL = "TRACE";
    private static final PathAddress SUBSYSTEM_ADDRESS =PathAddress.pathAddress(ModelDescriptionConstants.SUBSYSTEM, "logging");
    private static final PathAddress CUSTOM_LOGGER_ADDRESS =
        SUBSYSTEM_ADDRESS.append("logger", Log4jLoggingLevelServlet.class.getCanonicalName());
    private static final String CONTAINER = "default-jbossas";
    private static final String DEPLOYMENT = "log4j-level";

    @ArquillianResource
    private static ContainerController containerController;

    @ArquillianResource
    private Deployer deployer;

    @ArquillianResource
    ManagementClient managementClient;

    @Deployment(name = DEPLOYMENT, testable = false, managed = false)
    @TargetsContainer(CONTAINER)
    public static Archive<?> getDeployment() {
        return ShrinkWrap.create(WebArchive.class, DEPLOYMENT + ".war")
            .addClass(Log4jLoggingLevelServlet.class);
    }

    @Before
    public void prepareContainer() throws Exception {
        containerController.start(CONTAINER);

        // Add a custom logger
        ModelNode op = Operations.createAddOperation(CUSTOM_LOGGER_ADDRESS.toModelNode());
        op.get(LEVEL).set(LOG_LEVEL);
        executeOperation(op);

        deployer.deploy(DEPLOYMENT);
    }

    @After
    public void stopContainer() throws Exception {
        deployer.undeploy(DEPLOYMENT);

        // Remove the custom logger
        final ModelNode op = Operations.createRemoveOperation(CUSTOM_LOGGER_ADDRESS.toModelNode());
        executeOperation(op);

        containerController.stop(CONTAINER);
    }

    ModelNode executeOperation(final ModelNode op) throws IOException {
        ModelNode result = managementClient.getControllerClient().execute(op);
        if (!Operations.isSuccessfulOutcome(result)) {
            Assert.assertTrue(Operations.getFailureDescription(result).toString(), false);
        }
        return result;
    }

    @Test
    public void correctEffectiveLevel(@ArquillianResource URL urlBase) throws Exception {
        URL url = new URL(urlBase.toExternalForm() + Log4jLoggingLevelServlet.URL_PATTERN);

        HttpURLConnection httpURLConnection;
        // restart a few times as the issue doesn't happen always
        for (int i=0; i<5; i++) {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            Assert.assertEquals(HttpURLConnection.HTTP_OK, httpURLConnection.getResponseCode());
            try (InputStream responseInputStream = httpURLConnection.getInputStream()) {
                String response = IOUtils.toString(responseInputStream, "UTF-8");
                Assert.assertEquals(LOG_LEVEL, response);
            }
            containerController.stop(CONTAINER);
            containerController.start(CONTAINER);
        }
    }


}
