package org.jboss.additional.testsuite.jdkall.present.basic.beanvalidation;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/basic/src/main/java","modules/testcases/jdkAll/Eap72x/basic/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/basic/src/main/java","modules/testcases/jdkAll/Protean/basic/releases/test-configurations/src/main/java#0.9.1*0.9.1","modules/testcases/jdkAll/Protean/basic/releases/test-configurations2/src/main/java#0.10.0*0.12.0"})
@ApplicationPath("/bean-validation")
public class BeanValidationApplication extends Application {

}
