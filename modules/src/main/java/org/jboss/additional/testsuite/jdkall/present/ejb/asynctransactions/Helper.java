package org.jboss.additional.testsuite.jdkall.present.ejb.asynctransactions;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap72x/ejb/src/main/java#7.2.7","modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java#7.2.7","modules/testcases/jdkAll/Eap7/ejb/src/main/java#7.3.0.GA","modules/testcases/jdkAll/Wildfly/ejb/src/main/java#19.0.0.Beta1"})
public class Helper {

    static Transaction getCurrentTransaction() {
        try {
            InitialContext ctx = new InitialContext();
            TransactionManager transactionManager = (TransactionManager) ctx.lookup("java:/TransactionManager");
            return transactionManager.getTransaction();
        } catch (NamingException | SystemException e) {
            throw new RuntimeException(e);
        }
    }

}
