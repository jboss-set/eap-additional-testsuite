package org.jboss.additional.testsuite.jdkall.past.eap_7.ejb.async;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Remote;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * @author Ondrej Chaloupka
 */
@Remote
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap70x/ejb/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/WildflyRelease-10.1.0.Final/ejb/src/main/java","modules/testcases/jdkAll/Eap64x/ejb/src/main/java","modules/testcases/jdkAll/Eap64x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap63x/ejb/src/main/java","modules/testcases/jdkAll/Eap62x/ejb/src/main/java","modules/testcases/jdkAll/Eap61x/ejb/src/main/java"})
public interface AsyncBeanRemoteInterface {
    public void asyncMethod() throws InterruptedException;
    public Future<Boolean> futureMethod() throws InterruptedException, ExecutionException;
}
