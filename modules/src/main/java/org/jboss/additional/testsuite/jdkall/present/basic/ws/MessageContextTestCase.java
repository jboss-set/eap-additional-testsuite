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

package org.jboss.additional.testsuite.jdkall.present.basic.ws;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test that Jax-WS MessageContext works correctly.
 *
 * @author <a href="mailto:pmackay@redhat.com">Peter Mackay</a>
 */
// TODO: Enable once https://issues.jboss.org/browse/WFLY-11204 gets resolved
//@apAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/basic/src/main/java#15.0.0","modules/testcases/jdkAll/Eap72x-Proposed/basic/src/main/java#7.2.1","modules/testcases/jdkAll/Eap72x/basic/src/main/java#7.2.1","modules/testcases/jdkAll/Eap71x-Proposed/basic/src/main/java#7.1.6","modules/testcases/jdkAll/Eap71x/basic/src/main/java#7.1.6"})
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap72x/basic/src/main/java#7.2.1","modules/testcases/jdkAll/Eap72x-Proposed/basic/src/main/java#7.2.1","modules/testcases/jdkAll/Eap71x-Proposed/basic/src/main/java#7.1.6","modules/testcases/jdkAll/Eap71x/basic/src/main/java#7.1.6"})
@RunWith(Arquillian.class)
@RunAsClient
public class MessageContextTestCase {

    private static final String DEPLOYMENT_NAME = "JaxWSMessageContextTest";

    @Deployment
    public static WebArchive getDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, DEPLOYMENT_NAME + ".war");
        war.addClasses(ContainsKeyService.class, ContainsKeyServiceImpl.class, Handler.class);
        war.addAsWebInfResource("handlers.xml", "classes/handlers.xml");
        return war;
    }

    /**
     * Test that the WrappedMessageContext#containsKey method is consistent.
     * See https://issues.jboss.org/browse/JBEAP-15389
     */
    @Test
    public void testContainsKey(@ArquillianResource URL deploymentUrl) throws MalformedURLException {
        QName serviceName = new QName(ContainsKeyServiceImpl.NAMESPACE, ContainsKeyServiceImpl.SERVICE_NAME);
        URL wsdlUrl = new URL(deploymentUrl.toExternalForm() + ContainsKeyService.class.getSimpleName() + "?wsdl");
        Service service = Service.create(wsdlUrl, serviceName);
        ContainsKeyService containsKeyService = service.getPort(ContainsKeyService.class);

        Map<String, Object> requestContext = ((BindingProvider) containsKeyService).getRequestContext();
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("header", Arrays.asList("value"));
        requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, headers);

        Assert.assertTrue("The containsKey method returned false. See https://issues.jboss.org/browse/JBEAP-15389",
            containsKeyService.testContainsKey());
    }

}
