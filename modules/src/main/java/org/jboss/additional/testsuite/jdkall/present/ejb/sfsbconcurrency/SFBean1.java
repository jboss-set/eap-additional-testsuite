package org.jboss.additional.testsuite.jdkall.present.ejb.sfsbconcurrency;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.ejb.TransactionAttribute;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java","modules/testcases/jdkAll/Eap72x/ejb/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap7/ejb/src/main/java"})
@Stateful
@RequestScoped
public class SFBean1 {

	public void foo() {
	}

	@Produces 
	public Foo produceString() {
		return new Foo();
	}
}
