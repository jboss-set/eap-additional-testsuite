/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.additional.testsuite.jdkall.present.elytron.realmmappers;

import java.net.URL;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.management.util.CLIWrapper;
import org.jboss.as.test.integration.security.common.Utils;
import org.jboss.as.test.shared.ServerReload;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.jboss.additional.testsuite.jdkall.present.elytron.realmmappers.RealmMapperServerSetupTask.CORRECT_PASSWORD;
import static org.jboss.additional.testsuite.jdkall.present.elytron.realmmappers.RealmMapperServerSetupTask.DEFAULT_REALM;
import static org.jboss.additional.testsuite.jdkall.present.elytron.realmmappers.RealmMapperServerSetupTask.REALM1;
import static org.jboss.additional.testsuite.jdkall.present.elytron.realmmappers.RealmMapperServerSetupTask.REALM2;
import static org.jboss.additional.testsuite.jdkall.present.elytron.realmmappers.RealmMapperServerSetupTask.USER_IN_DEFAULT_REALM;
import static org.jboss.additional.testsuite.jdkall.present.elytron.realmmappers.RealmMapperServerSetupTask.USER_IN_REALM1;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * Test case for 'constant-realm-mapper' Elytron subsystem resource.
  *
 * @author olukas
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/elytron/src/main/java","modules/testcases/jdkAll/Wildfly/elytron/src/main/java","modules/testcases/jdkAll/WildflyRelease-17.0.0.Final/elytron/src/main/java","modules/testcases/jdkAll/Eap72x/elytron/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/elytron/src/main/java","modules/testcases/jdkAll/Eap7/elytron/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/elytron/src/main/java","modules/testcases/jdkAll/Eap71x/elytron/src/main/java"})
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup({RealmMapperServerSetupTask.class, ConstantRealmMapperTestCase.SetupTask.class})
public class ConstantRealmMapperTestCase extends AbstractRealmMapperTest {

    private static final String DEFAULT_REALM_MAPPER = "defaultRealmMapper";
    private static final String REALM1_MAPPER = "realm1Mapper";
    private static final String REALM2_MAPPER = "realm2Mapper";
    private static final String NON_EXIST_MAPPER = "nonExistMapper";

    /**
     * Test whether default realm is used in security domain when no realm-mapper is configured.
     *
     * @param webAppURL
     * @throws Exception
     */
    @Test
    @OperateOnDeployment(DEPLOYMENT)
    public void testDefaultRealmWithoutAnyRealmMapper(@ArquillianResource URL webAppURL) throws Exception {
        assertEquals("Response body is not correct.", USER_IN_DEFAULT_REALM,
                Utils.makeCallWithBasicAuthn(principalServlet(webAppURL), USER_IN_DEFAULT_REALM, CORRECT_PASSWORD, SC_OK));
        Utils.makeCallWithBasicAuthn(principalServlet(webAppURL), USER_IN_REALM1, CORRECT_PASSWORD, SC_UNAUTHORIZED);
    }

    /**
     * Test whether constant realm mapper return expected value. It means that security domain uses expected realm instead of
     * default.
     *
     * @param webAppURL
     * @throws Exception
     */
    @Test
    @OperateOnDeployment(DEPLOYMENT)
    public void testRealmMapper(@ArquillianResource URL webAppURL) throws Exception {
        setupRealmMapper(REALM1_MAPPER);
        try {
            assertEquals("Response body is not correct.", USER_IN_REALM1,
                    Utils.makeCallWithBasicAuthn(principalServlet(webAppURL), USER_IN_REALM1, CORRECT_PASSWORD, SC_OK));
            Utils.makeCallWithBasicAuthn(principalServlet(webAppURL), USER_IN_DEFAULT_REALM, CORRECT_PASSWORD, SC_UNAUTHORIZED);
        } finally {
            undefineRealmMapper();
        }
    }

    static class SetupTask implements ServerSetupTask {

        @Override
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            try (CLIWrapper cli = new CLIWrapper(true)) {
                cli.sendLine(String.format("/subsystem=elytron/constant-realm-mapper=%s:add(realm-name=%s)",
                        DEFAULT_REALM_MAPPER, DEFAULT_REALM));
                cli.sendLine(String.format("/subsystem=elytron/constant-realm-mapper=%s:add(realm-name=%s)",
                        REALM1_MAPPER, REALM1));
                cli.sendLine(String.format("/subsystem=elytron/constant-realm-mapper=%s:add(realm-name=%s)",
                        REALM2_MAPPER, REALM2));
                cli.sendLine(String.format("/subsystem=elytron/constant-realm-mapper=%s:add(realm-name=nonExistRealm)",
                        NON_EXIST_MAPPER));
            }
            ServerReload.reloadIfRequired(managementClient.getControllerClient());
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            try (CLIWrapper cli = new CLIWrapper(true)) {
                cli.sendLine(String.format("/subsystem=elytron/constant-realm-mapper=%s:remove()",
                        DEFAULT_REALM_MAPPER));
                cli.sendLine(String.format("/subsystem=elytron/constant-realm-mapper=%s:remove()",
                        REALM1_MAPPER));
                cli.sendLine(String.format("/subsystem=elytron/constant-realm-mapper=%s:remove()",
                        REALM2_MAPPER));
                cli.sendLine(String.format("/subsystem=elytron/constant-realm-mapper=%s:remove()",
                        NON_EXIST_MAPPER));
            }
            ServerReload.reloadIfRequired(managementClient.getControllerClient());
        }

    }

}
