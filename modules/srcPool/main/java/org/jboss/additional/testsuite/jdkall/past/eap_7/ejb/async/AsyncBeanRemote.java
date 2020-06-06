package org.jboss.additional.testsuite.jdkall.past.eap_7.ejb.async;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * Bean with asynchronous methods.
 * 
 * @author Ondrej Chaloupka
 */
@Stateless
@Asynchronous
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap70x/ejb/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/WildflyRelease-10.1.0.Final/ejb/src/main/java","modules/testcases/jdkAll/Eap64x/ejb/src/main/java","modules/testcases/jdkAll/Eap64x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap63x/ejb/src/main/java","modules/testcases/jdkAll/Eap62x/ejb/src/main/java","modules/testcases/jdkAll/Eap61x/ejb/src/main/java"})
public class AsyncBeanRemote implements AsyncBeanRemoteInterface {   
    @EJB
    AsyncBean asyncBean;

    @Override
    public void asyncMethod() throws InterruptedException {
        AsyncBean.voidMethodCalled = false;
        final CountDownLatch latch = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        asyncBean.asyncMethod(latch, latch2);
        latch.countDown();
        latch2.await();
        if(!AsyncBean.voidMethodCalled) {
            throw new IllegalArgumentException("voidMethodCalled");
        }
    }

    @Override
    public Future<Boolean> futureMethod() throws InterruptedException, ExecutionException {
        AsyncBean.futureMethodCalled = false;
        final CountDownLatch latch = new CountDownLatch(1);
        final Future<Boolean> future = asyncBean.futureMethod(latch);
        latch.countDown();
        return new AsyncResult<Boolean>(future.get());
    }  
}
