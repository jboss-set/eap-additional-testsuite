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

package org.jboss.additional.testsuite.jdkall.present.core.deployment;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.Operation;
import org.jboss.as.controller.client.OperationBuilder;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.dmr.ModelNode;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Test that the ZipSlip vulnerability (CVE-2018-10862) doesn't affect deployments.
 *
 * JBEAP-14914 / WFCORE-3938
 *
 * @author Peter Mackay
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap72x/core/src/main/java#7.2.0.CD13","modules/testcases/jdkAll/Eap71x-Proposed/core/src/main/java#7.1.4","modules/testcases/jdkAll/Eap71x/core/src/main/java#7.1.4","modules/testcases/jdkAll/Wildfly/core/src/main/java#14.0.0"})
@RunWith(Arquillian.class)
@RunAsClient
public class ZipSlipTestCase {

    private static final String DEPLOYMENT_NAME = "zipSlip.war";
    private static final String TEST_FILE_NAME = "test.txt";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private Path testFilePath;

    @ArquillianResource
    private ManagementClient mc;

    @Deployment
    /**
     * Just a dummy deployment to initialize the arquillian management client
     */
    public static Archive<?> getDummyDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "dummy.war");
        return war;
    }

    public Archive<?> getDeployment() throws IOException {
        // we need to construct a relative path from the deployment content folder to the testing temporary folder
        testFilePath = tempFolder.getRoot().toPath().resolve(TEST_FILE_NAME).toAbsolutePath();
        // the actual path is not important here as long as it has the right length
        Path deploymentContent = Paths.get(TestSuiteEnvironment.getJBossHome(), "standalone", "data", "content", "content_hash", "content");
        Path relativeTestFilePath = deploymentContent.toAbsolutePath().normalize().relativize(testFilePath);

        File zipSlipWar = new File("zipSlip.war");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipSlipWar));
        ZipEntry e = new ZipEntry(relativeTestFilePath.toString());
        out.putNextEntry(e);

        StringBuilder sb = new StringBuilder();
        sb.append("Test String");
        byte[] data = sb.toString().getBytes();
        out.write(data, 0, data.length);
        out.closeEntry();
        out.close();

        return ShrinkWrap.createFromZipFile(WebArchive.class, zipSlipWar);
    }

    /**
     * Test that it is not possible to write arbitrary files abusing path traversal when exploding deploymets.
     */
    @Test
    public void testDeploymentExplodingZipSlip() throws IOException {
        Archive<?> deployment = getDeployment();
        try {
            ModelNode deployResult = deployDisabled(deployment);
            assertOperationSuccess(deployResult);

            Assert.assertFalse("The temporary folder already contains " + TEST_FILE_NAME + " before the test.",
                Files.exists(testFilePath));

            // explode the deployment to try to exploit ZipSlip
            ModelNode explodeResult = explodeDeployment(deployment);

            Assert.assertFalse("ZipSlip exploited via deployment exploding.", Files.exists(testFilePath));
            Assert.assertEquals("The explode operation should have failed.",
                ModelDescriptionConstants.FAILED, explodeResult.get(ModelDescriptionConstants.OUTCOME).asString());
            Assert.assertTrue("The explode operation failed for the wrong reason.",
                explodeResult.get(ModelDescriptionConstants.FAILURE_DESCRIPTION).asString().startsWith("WFLYCTL0158"));
        } finally {
            undeploy(deployment);
        }
    }

    private ModelNode deployDisabled(Archive<?> deployment) throws IOException {
        final ModelNode deploy = new ModelNode();
        deploy.get(ModelDescriptionConstants.OP).set("add");
        deploy.get(ModelDescriptionConstants.OP_ADDR).add("deployment", deployment.getName());
        deploy.get(ModelDescriptionConstants.ENABLED).set("false");
        deploy.get("content").get(0).get("input-stream-index").set(0);

        final Operation o = OperationBuilder.create(deploy)
            .addInputStream(deployment.as(ZipExporter.class).exportAsInputStream()).build();

        final ModelNode deployResult = mc.getControllerClient().execute(o);
        return deployResult;
    }

    private ModelNode explodeDeployment(Archive<?> deployment) throws IOException {
        final ModelNode explode = new ModelNode();
        explode.get(ModelDescriptionConstants.OP).set("explode");
        explode.get(ModelDescriptionConstants.OP_ADDR).add("deployment", deployment.getName());
        return mc.getControllerClient().execute(explode);
    }

    private ModelNode undeploy(Archive<?> deployment) throws IOException {
        final ModelNode undeploy = new ModelNode();
        undeploy.get(ModelDescriptionConstants.OP).set("remove");
        undeploy.get(ModelDescriptionConstants.OP_ADDR).add("deployment", deployment.getName());
        return mc.getControllerClient().execute(undeploy);
    }

    private void assertOperationSuccess(ModelNode operationResult) {
        Assert.assertEquals("Management operation failed:\n" + operationResult.toString() + "\n",
            ModelDescriptionConstants.SUCCESS, operationResult.get(ModelDescriptionConstants.OUTCOME).asString());
    }

}
