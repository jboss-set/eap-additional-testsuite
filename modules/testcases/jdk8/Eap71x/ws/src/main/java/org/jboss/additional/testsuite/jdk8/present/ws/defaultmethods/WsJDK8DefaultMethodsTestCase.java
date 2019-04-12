/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.additional.testsuite.jdk8.present.ws.defaultmethods;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;

/**
 * @author baranowb
 *
 */
@RunWith(Arquillian.class)
@Ignore // Ignore the test becaust it can run only on localhost
@EapAdditionalTestsuite({"modules/testcases/jdk8/Wildfly/ws/src/main/java","modules/testcases/jdk8/Eap7/ws/src/main/java","modules/testcases/jdk8/Eap71x/ws/src/main/java","modules/testcases/jdk8/Eap70x/ws/src/main/java","modules/testcases/jdk8/Eap70x-Proposed/ws/src/main/java","modules/testcases/jdk8/WildflyRelease-10.1.0.Final/ws/src/main/java"})
public class WsJDK8DefaultMethodsTestCase {

    private static final String DEPLOYMENT_NAME = "jax-ws-test-pojo";
    private static final String DEPLOYMENT = DEPLOYMENT_NAME + ".war";

    @Deployment(name = DEPLOYMENT_NAME, testable = false)
    public static WebArchive deploymentWar() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, DEPLOYMENT);
        archive.addClass(GreeterSEI.class);
        archive.addClass(GreeterImpl2.class);
        archive.addClass(DefaultInterface.class);
        archive.setManifest(new StringAsset("Dependencies: org.jboss.logging"));
        return archive;
    }

    @Test
    @RunAsClient
    public void testPojoDefaultMethod() throws MalformedURLException {
        Service greeterService = Service.create(
                new URL("http://localhost:8080/jax-ws-test-pojo/GreeterImpl2?wsdl"),
                new QName("http://defaultmethods.ws.jdk8.testsuite.additional.jboss.org/", "GreeterImpl2Service"));

        Assert.assertNotNull(greeterService);
        GreeterSEI greeter = greeterService.getPort(GreeterSEI.class);

        //test default method implementation
        Assert.assertEquals("Hello, Default", greeter.sayHello());
    }

    @Test
    @RunAsClient
    public void testPojoInheritedDefaultMethod() throws MalformedURLException {
        Service greeterService = Service.create(
                new URL("http://localhost:8080/jax-ws-test-pojo/GreeterImpl2?wsdl"),
                new QName("http://defaultmethods.ws.jdk8.testsuite.additional.jboss.org/", "GreeterImpl2Service"));

        Assert.assertNotNull(greeterService);
        GreeterSEI greeter = greeterService.getPort(GreeterSEI.class);
 
        //test default method implementation
        Assert.assertEquals("Hi, Default", greeter.sayHi());
    }
}
