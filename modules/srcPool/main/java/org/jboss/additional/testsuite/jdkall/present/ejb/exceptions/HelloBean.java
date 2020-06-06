package org.jboss.additional.testsuite.jdkall.present.ejb.exceptions;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

import javax.annotation.Resource;

import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import javax.transaction.UserTransaction;

import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import javax.transaction.Synchronization;
import javax.transaction.TransactionSynchronizationRegistry;

/**
 * @author bmaxwell
 *
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java#20.0.0.Beta1", "modules/testcases/jdkAll/Eap72x/ejb/src/main/java#7.2.7", "modules/testcases/jdkAll/Eap72x-Proposed/ejb/main/java#7.2.7","modules/testcases/jdkAll/Eap7/ejb/src/main/java#7.3.1.GA"})
@Stateless
public class HelloBean implements Hello {

  @Resource(mappedName = "java:comp/TransactionSynchronizationRegistry")
  private TransactionSynchronizationRegistry txSyncReg;

  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public String hello() {

		txSyncReg.registerInterposedSynchronization(
			new Synchronization() {
				public void afterCompletion(int status) { 
				}
				public void beforeCompletion() { 
					throw new RuntimeException("Fail the tx"); 
				}
			});

		throw new MyApplicationExceptionNoRollbackRuntimeException("Client should not see this, instead should see an EJBException");
  }
}
