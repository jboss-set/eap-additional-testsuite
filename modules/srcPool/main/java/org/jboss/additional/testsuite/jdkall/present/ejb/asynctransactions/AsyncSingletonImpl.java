package org.jboss.additional.testsuite.jdkall.present.ejb.asynctransactions;

import javax.ejb.Asynchronous;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap72x/ejb/src/main/java#7.2.7","modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java#7.2.7","modules/testcases/jdkAll/Eap7/ejb/src/main/java#7.3.0.GA","modules/testcases/jdkAll/Wildfly/ejb/src/main/java#19.0.0.Beta1"})
@Singleton
@Remote(AsyncSingletonI.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
// @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AsyncSingletonImpl implements AsyncSingletonI {

    @Override
    @Asynchronous
    // @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void doAsyncAction() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignore) {
        }
    }

}
