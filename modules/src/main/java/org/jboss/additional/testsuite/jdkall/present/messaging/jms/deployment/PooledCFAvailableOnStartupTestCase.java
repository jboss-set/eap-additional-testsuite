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

package org.jboss.additional.testsuite.jdkall.present.messaging.jms.deployment;

import org.apache.commons.io.input.Tailer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.shared.ServerReload;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;
import org.jboss.additional.testsuite.jdkall.present.shared.ServerLogPatternListener;

/**
 * Test that querying MBeans in the init method of an eagerly loaded Servlet doesn't cause errors
 * due to Pooled Connection Factories not being defined yet.
 *
 * @author Peter Mackay
 */
@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap72x/messaging/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/messaging/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/messaging/src/main/java#7.1.4","modules/testcases/jdkAll/Eap71x/messaging/src/main/java#7.1.4","modules/testcases/jdkAll/Wildfly/messaging/src/main/java#12.0.0"})
public class PooledCFAvailableOnStartupTestCase {

    private static final String DEPLOYMENT_NAME = "eagerServletMBeanQuery.war";
    private static final File SERVER_LOG = new File(TestSuiteEnvironment.getJBossHome(), "standalone" + File.separator + "log" +
        File.separator + "server.log");

    @ArquillianResource
    private ManagementClient mc;

    @Deployment
    public static Archive<?> getDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, DEPLOYMENT_NAME);
        war.addClass(EagerMBeanQueryServlet.class);
        return war;
    }

    @Test
    public void test(){

        ServerLogPatternListener listener = new ServerLogPatternListener(Pattern.compile(".*WFLYCTL0030.*"));
        Tailer serverLogTailer =  new Tailer(SERVER_LOG, listener, 100, true);
        Thread tailerThread = new Thread(serverLogTailer);
        tailerThread.start();

        ServerReload.executeReloadAndWaitForCompletion(mc.getControllerClient());
        serverLogTailer.stop();
        Assert.assertTrue("Server log contains error messages caused by missing resource definitions!",
            listener.getMatchedLines().isEmpty());
    }
}
