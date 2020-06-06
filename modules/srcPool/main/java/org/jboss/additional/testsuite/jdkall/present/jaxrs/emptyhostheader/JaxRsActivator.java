package org.jboss.additional.testsuite.jdkall.present.jaxrs.emptyhostheader;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * @author Ivo Studensky
 */
@ApplicationPath("/myjaxrs")
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/jaxrs/src/main/java#20.0.0.Beta1", "modules/testcases/jdkAll/Eap72x/jaxrs/src/main/java#7.2.7", "modules/testcases/jdkAll/Eap72x-Proposed/jaxrs/src/main/java#7.2.7", "modules/testcases/jdkAll/Eap7/jaxrs/src/main/java"})
public class JaxRsActivator extends Application {
}
