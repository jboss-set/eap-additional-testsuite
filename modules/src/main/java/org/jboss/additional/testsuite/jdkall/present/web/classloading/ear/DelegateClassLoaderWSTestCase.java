/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
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

package org.jboss.additional.testsuite.jdkall.present.web.classloading.ear;

import org.apache.commons.io.IOUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Test that DelegateClassLoader works correctly for WS deployments.
 *
 * @author <a href="mailto:pmackay@redhat.com">Peter Mackay</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/web/src/main/java#15.0.0","modules/testcases/jdkAll/WildflyRelease-17.0.0.Alpha1/web/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/web/src/main/java#7.2.0.CR1","modules/testcases/jdkAll/Eap72x/web/src/main/java#7.2.0.CR1","modules/testcases/jdkAll/Eap71x-Proposed/web/src/main/java#7.1.5","modules/testcases/jdkAll/Eap71x/web/src/main/java#7.1.5"})
public class DelegateClassLoaderWSTestCase {

    private static final String EAR_NAME = "test";
    private static final String EJB_MODULE_NAME = "ejb";
    private static final String SERVICE_MODULE_NAME = "service";

    @Deployment()
    public static EnterpriseArchive getDeployment() throws IOException {
        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, EAR_NAME + ".ear");
        ear.addAsModule(getServicesJar());
        ear.addAsModule(getEJBJar());
        return ear;
    }

    private static JavaArchive getServicesJar() throws IOException {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, SERVICE_MODULE_NAME + ".jar");
        jar.addClass(GreeterService.class);
        jar.addClass(GreeterServiceImpl.class);
        jar.addClass(GreeterServiceClient.class);
        jar.addAsManifestResource(createFilteredAsset("/GreeterService.wsdl"), "GreeterService.wsdl");
        jar.addAsManifestResource("beans.xml");
        return jar;
    }

    private static JavaArchive getEJBJar() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, EJB_MODULE_NAME + ".jar");
        jar.addClass(GreeterEJB.class);
        jar.addClass(GreeterEJBImpl.class);
        return jar;
    }

    /**
     * Test that no NPE is thrown when the parent class loader is null causing the WS deployment to fail.
     * Instead he class loading should be correctly delegated instead.
     * See https://issues.jboss.org/browse/JBEAP-15169
     */
    @Test
    public void testParentClassLoader(@ArquillianResource URL deploymentUrl) throws Exception {
        GreeterEJB greeterEJBService = getClient(deploymentUrl);
        Assert.assertEquals("Hello World!", greeterEJBService.sayHello());
    }

    /**
     * Test that the TCCL for WS deployments is correctly set to the DelegateClassLoader
     * and is not null during initialization.
     * See https://issues.jboss.org/browse/JBEAP-15236
     */
    @Test
    public void testTCCL(@ArquillianResource URL deploymentUrl) throws Exception {
        GreeterEJB greeterEJBService = getClient(deploymentUrl);
        Assert.assertFalse("TCCL for the web service was null.", greeterEJBService.wasTCCLNull());
    }

    private static GreeterEJB getClient(URL deploymentUrl) throws MalformedURLException {
        String wsdlUrlString = deploymentUrl.toExternalForm().replace(EAR_NAME, EJB_MODULE_NAME) + GreeterEJBImpl.CLASS_NAME + "?wsdl";
        URL wsdlUrl = new URL(wsdlUrlString);
        QName serviceName = new QName(GreeterEJBImpl.NAMESPACE, GreeterEJBImpl.SERVICE_NAME);
        return Service.create(wsdlUrl, serviceName).getPort(GreeterEJB.class);
    }

    private static StringAsset createFilteredAsset(String resourceName) throws IOException {
        String content = IOUtils.toString(DelegateClassLoaderWSTestCase.class.getResourceAsStream(resourceName), "UTF-8");
        return new StringAsset(content.replace("@serverAddress@", TestSuiteEnvironment.getServerAddress()));
    }
}
