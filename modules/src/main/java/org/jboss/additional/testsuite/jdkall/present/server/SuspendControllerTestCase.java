package org.jboss.additional.testsuite.jdkall.present.server;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.jboss.as.server.suspend.SuspendController;
import org.jboss.as.server.suspend.ServerActivity;
import org.jboss.as.server.suspend.ServerActivityCallback;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/server/src/main/java","modules/testcases/jdkAll/Wildfly/server/src/main/java", "modules/testcases/jdkAll/Eap71x/server/src/main/java"})
public class SuspendControllerTestCase {

    private static final String FAIL_MESSAGE = "test failure";

    @Test
    public void testFailedActivityIsLoggedOnResume() throws IOException {
        SuspendController suspendController = new SuspendController();
        suspendController.registerActivity(new TestActivicy());

        suspendController.suspend(1000);
        PrintStream oldOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(baos));
            suspendController.resume();
            System.setOut(oldOut);
            String output = new String(baos.toByteArray());
            Assert.assertTrue("Exception type of the resume failure is not included in the log message",
                    output.contains(RuntimeException.class.getSimpleName()));
            Assert.assertTrue("Cause of the resume failure is not included in the log message",
                    output.contains(FAIL_MESSAGE));
        } finally {
            System.setOut(oldOut);
        }
    }

    private static final class TestActivicy implements ServerActivity {

        @Override
        public void preSuspend(ServerActivityCallback listener) {
            //not required
        }

        @Override
        public void suspended(ServerActivityCallback listener) {
            //not required
        }

        @Override
        public void resume() {
            throw new RuntimeException(FAIL_MESSAGE);
        }
    }

}
