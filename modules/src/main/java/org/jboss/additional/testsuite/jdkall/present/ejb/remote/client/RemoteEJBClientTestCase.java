/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.additional.testsuite.jdkall.present.ejb.remote.client;

import java.io.File;
import org.jboss.additional.testsuite.jdkall.present.ejb.remote.stateful.RemoteCounter;
import org.jboss.additional.testsuite.jdkall.present.ejb.remote.stateless.RemoteCalculator;

import javax.naming.NamingException;
import org.jboss.additional.testsuite.jdkall.present.ejb.remote.stateful.CounterBean;
import org.jboss.additional.testsuite.jdkall.present.ejb.remote.stateless.CalculatorBean;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * A sample program which acts a remote client for a EJB deployed on JBoss EAP server. This program shows how to lookup stateful and
 * stateless beans via JNDI and then invoke on them
 *
 * @author Jaikiran Pai
 */
@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java","modules/testcases/jdkAll/Eap7/ejb/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap71x/ejb/src/main/java"})
public class RemoteEJBClientTestCase {

    private static final String ARCHIVE_NAME = "test";

    @Deployment
    public static Archive<?> deploy() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, ARCHIVE_NAME + ".jar");
        jar.addClass(RemoteEJBClientTestCase.class);
        jar.addPackage(RemoteCounter.class.getPackage());
        jar.addPackage(RemoteCalculator.class.getPackage());
        jar.addAsResource("META-INF/wildfly-config.xml");
        return jar;
    }
    

    @Test 
    public void testVoidAsyncStatelessMethod() throws Exception {
        // Invoke a stateless bean
        invokeStatelessBean();

        // Invoke a stateful bean
        invokeStatefulBean();
    }

    /**
     * Looks up a stateless bean and invokes on it
     *
     * @throws NamingException
     */
    private static void invokeStatelessBean() throws Exception {
        // Let's lookup the remote stateless calculator
        final RemoteCalculator statelessRemoteCalculator = lookupRemoteStatelessCalculator();
        // invoke on the remote calculator
        int a = 204;
        int b = 340;
        int sum = statelessRemoteCalculator.add(a, b);
        assertTrue("invokeStatelessBean error ...", sum == a + b);
        // try one more invocation, this time for subtraction
        int num1 = 3434;
        int num2 = 2332;
        int difference = statelessRemoteCalculator.subtract(num1, num2);

        assertTrue("invokeStatelessBean error ...", difference == num1 - num2);
    }

    /**
     * Looks up a stateful bean and invokes on it
     *
     * @throws NamingException
     */
    private static void invokeStatefulBean() throws Exception {
        // Let's lookup the remote stateful counter
        final RemoteCounter statefulRemoteCounter = lookupRemoteStatefulCounter();
        // invoke on the remote counter bean
        final int NUM_TIMES = 5;
        for (int i = 0; i < NUM_TIMES; i++) {
            statefulRemoteCounter.increment();
        }
        assertTrue("invokeStatefulBean error ...", statefulRemoteCounter.getCount() == NUM_TIMES);
        // now decrementing
        for (int i = NUM_TIMES; i > 0; i--) {
            statefulRemoteCounter.decrement();
        }
        assertTrue("invokeStatefulBean error ...", statefulRemoteCounter.getCount() == 0);
    }

    /**
     * Looks up and returns the proxy to remote stateless calculator bean
     *
     * @return
     * @throws NamingException
     */
    private static RemoteCalculator lookupRemoteStatelessCalculator() throws Exception {
        EJBDirectory context = new RemoteEJBDirectory(ARCHIVE_NAME);
        RemoteCalculator bean = context.lookupStateless(CalculatorBean.class, RemoteCalculator.class);
 
        return bean;
    }

    /**
     * Looks up and returns the proxy to remote stateful counter bean
     *
     * @return
     * @throws NamingException
     */
    private static RemoteCounter lookupRemoteStatefulCounter() throws NamingException {
        EJBDirectory context = new RemoteEJBDirectory(ARCHIVE_NAME);
        RemoteCounter bean = context.lookupStateful(CounterBean.class, RemoteCounter.class);
 
        return bean;
    }
}
