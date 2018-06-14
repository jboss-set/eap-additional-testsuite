package org.jboss.additional.testsuite.jdkall.present.ejb.async;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/ejb/src/main/java","modules/testcases/jdkAll/Eap7/ejb/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap71x/ejb/src/main/java","modules/testcases/jdkAll/Eap7.1.0.Beta/ejb/src/main/java","modules/testcases/jdkAll/Wildfly/ejb/src/main/java"})
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
