package org.jboss.additional.testsuite.jdkall.present.ejb.spec;

import java.io.Serializable;

import javax.ejb.Local;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@Local
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java","modules/testcases/jdkAll/Eap72x/ejb/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap7/ejb/src/main/java"})
public interface ApplicationStore extends Serializable{

	public void postconstruct();
	
	public void remove();

        public String getDate();

}
