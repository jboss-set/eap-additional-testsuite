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

package org.jboss.additional.testsuite.jdkall.present.security.web;

import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.helpers.ClientConstants;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.dmr.ModelNode;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.junit.Assert;

import java.io.IOException;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/security/src/main/java#13.0.0","modules/testcases/jdkAll/WildflyRelease-17.0.0.Alpha1/security/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/security/src/main/java#7.2.0.CD14","modules/testcases/jdkAll/Eap72x/security/src/main/java#7.2.0.CD14","modules/testcases/jdkAll/Eap71x-Proposed/security/src/main/java#7.1.5","modules/testcases/jdkAll/Eap71x/security/src/main/java#7.1.5"})
public class BearerAuthServerSetup implements ServerSetupTask {

    @Override
    public void setup(ManagementClient managementClient, String s) throws Exception {
        ModelNode addRealm = new ModelNode();
        addRealm.get(ClientConstants.OP).set(ClientConstants.ADD);
        addRealm.get(ClientConstants.ADDRESS).add("subsystem", "elytron");
        addRealm.get(ClientConstants.ADDRESS).add("token-realm", "jwt-realm");
        addRealm.get("principal-claim").set("sub");
        addRealm.get("jwt").setEmptyObject();
        executeOperation(managementClient, addRealm);

        ModelNode addDomain = new ModelNode();
        addDomain.get(ClientConstants.OP).set(ClientConstants.ADD);
        addDomain.get(ClientConstants.ADDRESS).add("subsystem", "elytron");
        addDomain.get(ClientConstants.ADDRESS).add("security-domain", "jwt-domain");
        addDomain.get("realms").get(0).get("realm").set("jwt-realm");
        addDomain.get("realms").get(0).get("role-decoder").set("groups-to-roles");
        addDomain.get("default-realm").set("jwt-realm");
        addDomain.get("permission-mapper").set("default-permission-mapper");
        executeOperation(managementClient, addDomain);

        ModelNode addFactory = new ModelNode();
        addFactory.get(ClientConstants.OP).set(ClientConstants.ADD);
        addFactory.get(ClientConstants.ADDRESS).add("subsystem", "elytron");
        addFactory.get(ClientConstants.ADDRESS).add("http-authentication-factory", "jwt-http-authentication");
        addFactory.get("security-domain").set("jwt-domain");
        addFactory.get("http-server-mechanism-factory").set("global");
        addFactory.get("mechanism-configurations").get(0).get("mechanism-name").set("BEARER_TOKEN");
        addFactory.get("mechanism-configurations").get(0)
            .get("mechanism-realm-configurations").get(0).get("realm-name").set("jwt-realm");
        executeOperation(managementClient, addFactory);

        ModelNode undertowDomain = new ModelNode();
        undertowDomain.get(ClientConstants.OP).set(ClientConstants.ADD);
        undertowDomain.get(ClientConstants.ADDRESS).add("subsystem", "undertow");
        undertowDomain.get(ClientConstants.ADDRESS).add("application-security-domain", "other");
        undertowDomain.get("http-authentication-factory").set("jwt-http-authentication");
        executeOperation(managementClient, undertowDomain);
    }

    @Override
    public void tearDown(ManagementClient managementClient, String s) throws Exception {
        ModelNode undertowDomain = new ModelNode();
        undertowDomain.get(ClientConstants.OP).set(ClientConstants.REMOVE_OPERATION);
        undertowDomain.get(ClientConstants.ADDRESS).add("subsystem", "undertow");
        undertowDomain.get(ClientConstants.ADDRESS).add("application-security-domain", "other");
        executeOperation(managementClient, undertowDomain);

        ModelNode removeFactory = new ModelNode();
        removeFactory.get(ClientConstants.OP).set(ClientConstants.REMOVE_OPERATION);
        removeFactory.get(ClientConstants.ADDRESS).add("subsystem", "elytron");
        removeFactory.get(ClientConstants.ADDRESS).add("http-authentication-factory", "jwt-http-authentication");
        executeOperation(managementClient, removeFactory);

        ModelNode removeDomain = new ModelNode();
        removeDomain.get(ClientConstants.OP).set(ClientConstants.REMOVE_OPERATION);
        removeDomain.get(ClientConstants.ADDRESS).add("subsystem", "elytron");
        removeDomain.get(ClientConstants.ADDRESS).add("security-domain", "jwt-domain");
        executeOperation(managementClient, removeDomain);

        ModelNode removeRealm = new ModelNode();
        removeRealm.get(ClientConstants.OP).set(ClientConstants.REMOVE_OPERATION);
        removeRealm.get(ClientConstants.ADDRESS).add("subsystem", "elytron");
        removeRealm.get(ClientConstants.ADDRESS).add("token-realm", "jwt-realm");
        executeOperation(managementClient, removeRealm);
    }

    ModelNode executeOperation(ManagementClient mgmtClient, final ModelNode op) throws IOException {
        ModelNode result = mgmtClient.getControllerClient().execute(op);
        if (!Operations.isSuccessfulOutcome(result)) {
            Assert.fail(Operations.getFailureDescription(result).toString());
        }
        return result;
    }
}
