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

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

@RunWith(Arquillian.class)
@Ignore // Ignore the test becaust it can run only on localhost
@EapAdditionalTestsuite({"modules/testcases/jdk8/WildflyRelease-13.0.0.Final/ws/src/main/java","modules/testcases/jdk8/Wildfly/ws/src/main/java","modules/testcases/jdk8/WildflyRelease-17.0.0.Alpha1/ws/src/main/java","modules/testcases/jdk8/Eap7/ws/src/main/java","modules/testcases/jdk8/Eap71x-Proposed/ws/src/main/java","modules/testcases/jdk8/Eap71x/ws/src/main/java","modules/testcases/jdk8/Eap70x/ws/src/main/java","modules/testcases/jdk8/Eap70x-Proposed/ws/src/main/java","modules/testcases/jdk8/WildflyRelease-10.1.0.Final/ws/src/main/java"})
public class WsTest {

    @Deployment(name = "jax-ws-test-pojo", testable = false)
    public static WebArchive deploymentWar() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, "jax-ws-test-pojo.war");
        archive.addClass(GreeterSEI.class);
        archive.addClass(GreeterImpl.class);
        archive.addClass(DefaultInterface.class);
        archive.setManifest(new StringAsset("Dependencies: org.jboss.logging"));
        return archive;
    }

    @Test
    @RunAsClient
    public void testPojoDefaultMethod() throws MalformedURLException {
        Service greeterService = Service.create(
                new URL("http://localhost:8080/jax-ws-test-pojo/GreeterImpl?wsdl"),
                new QName("http://ws.jdk8.qe.redhat.com/", "GreeterImplService"));

        Assert.assertNotNull(greeterService);
        GreeterSEI greeter = greeterService.getPort(GreeterSEI.class);

        //test defautl method implementation
        Assert.assertEquals("Hello, Default", greeter.sayHello());
    }

    @Test
    @RunAsClient
    public void testPojoInheritedDefaultMethod() throws MalformedURLException {
        Service greeterService = Service.create(
                new URL("http://localhost:8080/jax-ws-test-pojo/GreeterImpl?wsdl"),
                new QName("http://ws.jdk8.qe.redhat.com/", "GreeterImplService"));

        Assert.assertNotNull(greeterService);
        GreeterSEI greeter = greeterService.getPort(GreeterSEI.class);

        //test defautl method implementation
        Assert.assertEquals("Hi, Default", greeter.sayHi());
    }
}
