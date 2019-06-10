package org.jboss.additional.testsuite.jdkall.present.server;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.junit.Assert;
import org.junit.Test;

import org.jboss.as.server.suspend.SuspendController;
import org.jboss.as.server.suspend.ServerActivity;
import org.jboss.as.server.suspend.ServerActivityCallback;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/server/src/main/java","modules/testcases/jdkAll/Wildfly/server/src/main/java","modules/testcases/jdkAll/WildflyRelease-17.0.0.Final/server/src/main/java","modules/testcases/jdkAll/Eap71x/server/src/main/java","modules/testcases/jdkAll/Eap72x/server/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/server/src/main/java"})
public class SuspendControllerTestCase {

    private static final String FAIL_MESSAGE = "test failure";

    @Test
    public void testFailedActivityIsLoggedOnResume() throws IOException {
        SuspendController suspendController = new SuspendController();
        suspendController.registerActivity(new TestActivity());

        suspendController.suspend(1000);         
        suspendController.resume();

        String output = new String(Files.readAllBytes(Paths.get("target/server.log")));
        
        Assert.assertTrue("Exception type of the resume failure is not included in the log message",
                output.contains(RuntimeException.class.getSimpleName()));
        Assert.assertTrue("Cause of the resume failure is not included in the log message",
                output.contains(FAIL_MESSAGE));
    }

    private static final class TestActivity implements ServerActivity {

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
