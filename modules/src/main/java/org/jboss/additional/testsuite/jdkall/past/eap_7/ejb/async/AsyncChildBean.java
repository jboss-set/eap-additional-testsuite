package org.jboss.additional.testsuite.jdkall.past.eap_7.ejb.async;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ejb.Stateless;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@Stateless
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap70x/ejb/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/WildflyRelease-10.1.0.Final/ejb/src/main/java","modules/testcases/jdkAll/Eap64x/ejb/src/main/java","modules/testcases/jdkAll/Eap64x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap63x/ejb/src/main/java","modules/testcases/jdkAll/Eap62x/ejb/src/main/java","modules/testcases/jdkAll/Eap61x/ejb/src/main/java"})
public class AsyncChildBean extends AsyncParentClass {

    public void asyncMethod(CountDownLatch latch, CountDownLatch latch2) throws InterruptedException {
        latch.await(5, TimeUnit.SECONDS);
        voidMethodCalled = true;
        latch2.countDown();
    }
    
}
