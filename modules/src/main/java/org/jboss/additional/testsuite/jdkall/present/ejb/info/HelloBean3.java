package org.jboss.additional.testsuite.jdkall.present.ejb.info;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

import javax.ejb.Stateless;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java#20.0.0.Beta1", "modules/testcases/jdkAll/Eap72x/ejb/src/main/java#7.2.9", "modules/testcases/jdkAll/Eap72x-Proposed/ejb/main/java#7.2.9","modules/testcases/jdkAll/Eap7/ejb/src/main/java#7.3.2"})
@Stateless
public class HelloBean3 extends HelloBean4 implements Hello3 {

  public String hello3() {
	return "Hello once more...";
  }
}
