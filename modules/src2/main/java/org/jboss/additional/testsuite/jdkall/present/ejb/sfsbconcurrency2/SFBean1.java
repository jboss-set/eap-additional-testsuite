package org.jboss.additional.testsuite.jdkall.present.ejb.sfsbconcurrency2;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.enterprise.context.ApplicationScoped;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Produces;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java","modules/testcases/jdkAll/Eap72x/ejb/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap7/ejb/src/main/java"})
@Stateful
@ApplicationScoped
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class SFBean1 {

	public void foo() {
	}

	@Produces 
	public Foo produceString() {
		return new Foo();
	}
}
