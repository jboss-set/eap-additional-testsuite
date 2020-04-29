package org.jboss.additional.testsuite.jdkall.present.ejb.spec;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Local;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;


@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java","modules/testcases/jdkAll/Eap72x/ejb/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap7/ejb/src/main/java"})
@Local
@Stateless
public class InvocationBean {
	
	@EJB(beanName="ApplicationStoreBean")
	ApplicationStore applicationStore;

	public void getDateSessionTest() {		
		applicationStore.getDate();
	}

}
