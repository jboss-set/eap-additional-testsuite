package org.jboss.additional.testsuite.jdkall.present.ejb.sfsbconcurrency3;

import javax.ejb.Stateful;
import javax.ejb.DependsOn;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.enterprise.inject.Produces;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java","modules/testcases/jdkAll/Eap72x/ejb/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap7/ejb/src/main/java"})
@Stateful
@RequestScoped
public class SFBean2 {

	public void foo() {
            foo.foo();
	}

	@Inject
	private Foo foo;
       
}
