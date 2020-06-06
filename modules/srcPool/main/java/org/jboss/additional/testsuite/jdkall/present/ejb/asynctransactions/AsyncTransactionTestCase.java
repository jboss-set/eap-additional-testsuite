package org.jboss.additional.testsuite.jdkall.present.ejb.asynctransactions;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.runner.RunWith;
import org.jboss.as.arquillian.api.ServerSetup;
import org.junit.AfterClass;
import org.junit.Assert;


@RunWith(Arquillian.class)
@ServerSetup(ServerLogSetupTask.class)
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap72x/ejb/src/main/java#7.2.7","modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java#7.2.7","modules/testcases/jdkAll/Eap7/ejb/src/main/java#7.3.0.GA","modules/testcases/jdkAll/Wildfly/ejb/src/main/java#19.0.0.Beta1"})
public class AsyncTransactionTestCase {

    @EJB
    CallerSingleton callerSingleton;

    private static final String ARCHIVE_NAME = "AsyncTransactionTestCase";

    @Deployment
    public static Archive<?> deploy() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, ARCHIVE_NAME + ".jar");
        jar.addClasses(AsyncTransactionTestCase.class, AsyncSingletonI.class, AsyncSingletonImpl.class, CallerSingleton.class, AsyncTransaction.class, Helper.class);
        return jar;
    }

    @Test
    public void checkException() {
	try {
	    callerSingleton.call();
	} catch(Exception ex) {
            fail("Exception with async transactions...");
	} 
    }

    @AfterClass
    public static void after() {
        try {
            Thread.sleep(5000);
            System.setOut(ServerLogSetupTask.oldOut);
            String output = new String(ServerLogSetupTask.baos.toByteArray());
            Assert.assertFalse(output, output.contains("ERROR"));
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            System.setOut(ServerLogSetupTask.oldOut);
        }
    }
}
