package  org.jboss.additional.testsuite.jdkall.present.shared;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * @author Kabir Khan
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/ejb/src/main/java","modules/testcases/jdkAll/Eap7/ejb/src/main/java","modules/testcases/jdkAll/Eap72x/ejb/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap71x/ejb/src/main/java","modules/testcases/jdkAll/Eap7.1.0.Beta/ejb/src/main/java","modules/testcases/jdkAll/Wildfly/ejb/src/main/java","modules/testcases/jdkAll/WildflyRelease-17.0.0.Alpha1/ejb/src/main/java"})
public class DisableInvocationTestUtil {
    public static void disable() {
        AssumeTestGroupUtil.assumeInvocationTestsEnabled();
    }
}
