package org.jboss.additional.testsuite.jdkall.present.ejb.asynctransactions;

import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.clustering.controller.Operations;
import org.jboss.as.controller.client.helpers.ClientConstants;
import org.jboss.as.test.integration.management.util.ServerReload;
import org.jboss.as.test.integration.security.common.CoreUtils;
import org.jboss.dmr.ModelNode;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.jboss.as.cli.Util.ALLOW_RESOURCE_SERVICE_RESTART;
import static org.jboss.as.controller.client.helpers.ClientConstants.OPERATION_HEADERS;
import static org.jboss.as.controller.client.helpers.ClientConstants.ROLLBACK_ON_RUNTIME_FAILURE;
import static org.jboss.as.controller.client.helpers.ClientConstants.UNDEFINE_ATTRIBUTE_OPERATION;
import static org.jboss.as.controller.client.helpers.ClientConstants.WRITE_ATTRIBUTE_OPERATION;
import static org.jboss.as.test.integration.management.util.ModelUtil.createOpNode;


@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap72x/ejb/src/main/java#7.2.7","modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java#7.2.7","modules/testcases/jdkAll/Eap7/ejb/src/main/java#7.3.0.GA","modules/testcases/jdkAll/Wildfly/ejb/src/main/java#19.0.0.Beta1"})
public class ServerLogSetupTask implements ServerSetupTask {

    public static PrintStream oldOut;
    public static ByteArrayOutputStream baos;

    @Override
    public final void setup(ManagementClient managementClient, String containerId) throws Exception {
        oldOut = System.out;
        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        List<ModelNode> operations = new ArrayList<>();
        // /subsystem=logging/periodic-rotating-file-handler=FILE:write-attribute(name=encoding,value=UTF-8)
        ModelNode setLoggingAttribute = createOpNode("subsystem=logging/periodic-rotating-file-handler=FILE", WRITE_ATTRIBUTE_OPERATION);
        setLoggingAttribute.get(ClientConstants.NAME).set("encoding");
        setLoggingAttribute.get(ClientConstants.VALUE).set("UTF-8");
        operations.add(setLoggingAttribute);

        ModelNode updateOp = Operations.createCompositeOperation(operations);
        updateOp.get(OPERATION_HEADERS, ROLLBACK_ON_RUNTIME_FAILURE).set(false);
        updateOp.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
        CoreUtils.applyUpdate(updateOp, managementClient.getControllerClient());
    }

    @Override
    public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
        List<ModelNode> operations = new ArrayList<>();
        // /subsystem=logging/periodic-rotating-file-handler=FILE:undefine-attribute(name=encoding)
        ModelNode undefineLoggingAttribute = createOpNode("subsystem=logging/periodic-rotating-file-handler=FILE", UNDEFINE_ATTRIBUTE_OPERATION);
        undefineLoggingAttribute.get(ClientConstants.NAME).set("encoding");
        operations.add(undefineLoggingAttribute);

        ModelNode updateOp = Operations.createCompositeOperation(operations);
        updateOp.get(OPERATION_HEADERS, ROLLBACK_ON_RUNTIME_FAILURE).set(false);
        updateOp.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
        CoreUtils.applyUpdate(updateOp, managementClient.getControllerClient());
        ServerReload.executeReloadAndWaitForCompletion(managementClient.getControllerClient());
    }

}
