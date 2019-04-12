package org.jboss.additional.testsuite.jdkall.past.eap_7.ejb.async;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap70x/ejb/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/WildflyRelease-10.1.0.Final/ejb/src/main/java","modules/testcases/jdkAll/Eap64x/ejb/src/main/java","modules/testcases/jdkAll/Eap64x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap63x/ejb/src/main/java","modules/testcases/jdkAll/Eap62x/ejb/src/main/java","modules/testcases/jdkAll/Eap61x/ejb/src/main/java"})
@Stateless
@Asynchronous
public class AsyncBeanDescriptor {
    public static volatile boolean futureMethodCalled = false;

    public Future<Boolean> futureMethod(CountDownLatch latch) throws InterruptedException {
        futureMethodCalled = true;
        latch.countDown();
        return new AsyncResult<Boolean>(true);
    }
}
