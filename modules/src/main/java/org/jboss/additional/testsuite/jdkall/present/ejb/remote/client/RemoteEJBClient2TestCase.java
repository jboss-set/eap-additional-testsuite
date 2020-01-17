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


import javax.naming.NamingException;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

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
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java","modules/testcases/jdkAll/Eap72x/ejb/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap7/ejb/src/main/java"})
public class RemoteEJBClient2TestCase {

    private static final String ARCHIVE_NAME = "test";

    @Deployment
    public static Archive<?> deploy() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, ARCHIVE_NAME + ".jar");
        jar.addClass(RemoteEJBClient2TestCase.class);
        jar.addPackage(RemoteEJBClient2TestCase.class.getPackage());
        jar.addAsResource("META-INF/wildfly-config.xml");
        return jar;
    }
    

    @Test 
    public void testStatelessMethod() throws Exception {
        // Invoke a stateless bean
        invokeStatelessBean();
    }

    /**
     * Looks up a stateless bean and invokes on it
     *
     * @throws NamingException
     */
    private static void invokeStatelessBean() throws Exception {
        Properties env = new Properties();
	env.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
	Context ctx = new InitialContext(env);

   	UserTransaction utx = null;
   	utx = (UserTransaction) ctx.lookup("txn:UserTransaction");

	final TestRemote ejb = lookupRemoteStatelessTest();

        if(utx != null)
	    utx.begin();

        ejb.test();

	if(utx != null)
	    utx.commit();
    }

    /**
     * Looks up and returns the proxy to remote stateless bean
     *
     * @return
     * @throws NamingException
     */
    private static TestRemote lookupRemoteStatelessTest() throws Exception {
        EJBDirectory context = new RemoteEJBDirectory(ARCHIVE_NAME);
        TestRemote bean = context.lookupStateless(TestEJB.class, TestRemote.class);
 
        return bean;
    }

}
