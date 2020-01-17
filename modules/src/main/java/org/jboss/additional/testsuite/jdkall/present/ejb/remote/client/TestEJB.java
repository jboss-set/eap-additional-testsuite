package org.jboss.additional.testsuite.jdkall.present.ejb.remote.client;

import javax.ejb.Stateless;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java","modules/testcases/jdkAll/Eap72x/ejb/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap7/ejb/src/main/java"})
@Stateless
public class TestEJB implements TestRemote {

	public String test() { 
		return "Hello";
	}
}
