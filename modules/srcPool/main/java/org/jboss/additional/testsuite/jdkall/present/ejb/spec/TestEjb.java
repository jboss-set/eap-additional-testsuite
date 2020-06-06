package org.jboss.additional.testsuite.jdkall.present.ejb.spec;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java","modules/testcases/jdkAll/Eap7/ejb/src/main/java#7.3.0*7.3.9"})

public class TestEjb {
    public static final String MESSAGE = "test";

    public String test() {
        return MESSAGE;
    }
}
