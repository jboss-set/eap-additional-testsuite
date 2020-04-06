package org.jboss.additional.testsuite.jdkall.present.ejb.warnings;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

import javax.ejb.Stateless;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java#20.0.0.Beta1", "modules/testcases/jdkAll/Eap72x/ejb/src/main/java#7.2.9", "modules/testcases/jdkAll/Eap72x-Proposed/ejb/main/java#7.2.9","modules/testcases/jdkAll/Eap7/ejb/src/main/java#7.3.2"})
@Stateless
public class HelloBean implements Hello {

  public String hello() {
	return "Hello...";
  }
}
