/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2019, Red Hat, Inc., and individual contributors
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

import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.shared.TestSuiteEnvironment;

import org.apache.commons.io.input.Tailer;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;
import org.jboss.additional.testsuite.jdkall.present.shared.ServerLogPatternListener;

@RunAsClient
@RunWith(Arquillian.class)
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap7/manualmode/src/main/java#7.2.6","modules/testcases/jdkAll/Eap72x/manualmode/src/main/java#7.2.6", "modules/testcases/jdkAll/Eap72x-Proposed/manualmode/src/main/java#7.2.6","modules/testcases/jdkAll/Wildfly/manualmode/src/main/java#11.0.0"})
public class PasswordRedactingTestCase {

    // defined in arquillian config
    private static final String PWD = "mySecretPwd";
    private static final String CONTAINER = "default-jbossas";
    private static final File SERVER_LOG = new File(TestSuiteEnvironment.getJBossHome(), "standalone" + File.separator + "log" +
            File.separator + "server.log");

    @ArquillianResource
    private static ContainerController containerController;

    @AfterClass
    public static void stopContainer() {
        if (containerController.isStarted(CONTAINER)) {
            containerController.stop(CONTAINER);
        }
    }

    @Test
    public void testPasswordRedacting() {
        containerController.start(CONTAINER);
        containerController.stop(CONTAINER);

        ServerLogPatternListener listener = new ServerLogPatternListener(Pattern.compile(".*" + PWD + ".*"));
        Tailer serverLogTailer =  new Tailer(SERVER_LOG, listener, 100, true);
        Thread tailerThread = new Thread(serverLogTailer);
        tailerThread.start();

        containerController.start(CONTAINER);
        containerController.stop(CONTAINER);
        serverLogTailer.stop();
        Assert.assertTrue("The server log contains an unredacted password property value:\n" + listener.getMatchedLines(),
                listener.getMatchedLines().isEmpty());
    }
}
