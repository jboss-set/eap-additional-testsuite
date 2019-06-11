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

package org.jboss.additional.testsuite.jdkall.present.ejb.stateful.passivation;

import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.clustering.controller.Operations;
import org.jboss.as.test.integration.security.common.CoreUtils;
import org.jboss.as.test.shared.ServerReload;
import org.jboss.as.test.shared.SnapshotRestoreSetupTask;
import org.jboss.dmr.ModelNode;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ALLOW_RESOURCE_SERVICE_RESTART;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OPERATION_HEADERS;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ROLLBACK_ON_RUNTIME_FAILURE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;
import static org.jboss.as.test.integration.management.util.ModelUtil.createOpNode;

/**
 * Server setup task for TwoPassivationStoresTestCase. Configures two passivation stores and two EJB caches.
 *
 * @author Daniel Cihak
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java#16.0.0.Beta1","modules/testcases/jdkAll/WildflyRelease-17.0.0.Final/ejb/src/main/java", "modules/testcases/jdkAll/Eap72x/ejb/src/main/java#7.2.1", "modules/testcases/jdkAll/Eap72x-Proposed/ejb/main/java#7.2.1"})
public class TwoPassivationStoresServerSetupTask extends SnapshotRestoreSetupTask {

    @Override
    public void doSetup(ManagementClient managementClient, String s) throws Exception {
        List<ModelNode> operations = new ArrayList<>();

        // /subsystem=ejb3/passivation-store=infinispanStore2:add(cache-container=ejb, bean-cache=passivation, max-size=1)
        ModelNode addPassivationStore = createOpNode("subsystem=ejb3/passivation-store=infinispanStore2", ADD);
        addPassivationStore.get("cache-container").set("ejb");
        addPassivationStore.get("bean-cache").set("passivation");
        addPassivationStore.get("max-size").set("1");
        operations.add(addPassivationStore);

        // /subsystem=ejb3/cache=infinispanCache2:add(passivation-store=infinispanStore2, aliases=[longlife])
        ModelNode addCache = createOpNode("subsystem=ejb3/cache=distributable2", ADD);
        addCache.get("passivation-store").set("infinispanStore2");
        addCache.get("aliases").get(0).set("longlife");
        operations.add(addCache);

        ModelNode updateOp = Operations.createCompositeOperation(operations);
        updateOp.get(OPERATION_HEADERS, ROLLBACK_ON_RUNTIME_FAILURE).set(false);
        updateOp.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
        CoreUtils.applyUpdate(updateOp, managementClient.getControllerClient());

        ModelNode passivationStoreAddress = new ModelNode();
        passivationStoreAddress.add(SUBSYSTEM, "ejb3").add("passivation-store", "infinispan");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("write-attribute");
        operation.get(OP_ADDR).set(passivationStoreAddress);
        operation.get("name").set("max-size");
        operation.get("value").set(1);
        managementClient.getControllerClient().execute(operation);
        ServerReload.reloadIfRequired(managementClient);
    }

}
