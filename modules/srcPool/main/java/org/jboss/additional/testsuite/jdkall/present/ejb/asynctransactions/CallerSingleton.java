package org.jboss.additional.testsuite.jdkall.present.ejb.asynctransactions;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap72x/ejb/src/main/java#7.2.7","modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java#7.2.7","modules/testcases/jdkAll/Eap7/ejb/src/main/java#7.3.0.GA","modules/testcases/jdkAll/Wildfly/ejb/src/main/java#19.0.0.Beta1"})
public interface CallerSingleton {
    void call() throws Exception;
}
