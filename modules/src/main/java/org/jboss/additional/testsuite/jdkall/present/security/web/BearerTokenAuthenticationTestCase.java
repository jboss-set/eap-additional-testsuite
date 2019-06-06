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

package org.jboss.additional.testsuite.jdkall.present.security.web;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

/**
 * Test web resources secured using the BEARER_TOKEN authentication mechanism.
 *
 * @author <a href="mailto:pmackay@redhat.com">Peter Mackay</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(BearerAuthServerSetup.class)
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/security/src/main/java#13.0.0","modules/testcases/jdkAll/WildflyRelease-17.0.0.Alpha1/security/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/security/src/main/java#7.2.0.CD14","modules/testcases/jdkAll/Eap72x/security/src/main/java#7.2.0.CD14","modules/testcases/jdkAll/Eap71x-Proposed/security/src/main/java#7.1.5","modules/testcases/jdkAll/Eap71x/security/src/main/java#7.1.5"})
public class BearerTokenAuthenticationTestCase {

    public static final String WEB_XML =
        "<?xml version=\"1.0\"?>\n" +
        "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\"\n" +
        "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "         xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee web-app_3_0.xsd\"\n" +
        "         version=\"3.0\">\n" +
        "\n" +
        "    <login-config>\n" +
        "        <auth-method>BEARER_TOKEN</auth-method>\n" +
        "        <realm-name>testRealm</realm-name>\n" +
        "    </login-config>\n" +
        "\n" +
        "</web-app>";

    @Deployment
    public static WebArchive getDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "bearer-token.war");
        war.setWebXML(new StringAsset(WEB_XML));
        war.addClass(UnsecuredServlet.class);
        return war;
    }

    /**
     * Test that it is still possible to access unsecured resources even without any authentication.
     * https://issues.jboss.org/browse/JBEAP-15265
     */
    @Test
    public void testUnsecuredAccess(@ArquillianResource URL url) throws Exception {
        URL testUrl = new URL(url.toExternalForm() + UnsecuredServlet.URL_PATTERN);
        HttpGet request = new HttpGet(testUrl.toURI());
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(request);

        Assert.assertNotEquals("This shouldn't require authentication. See https://issues.jboss.org/browse/JBEAP-15265",
            HTTP_UNAUTHORIZED, response.getStatusLine().getStatusCode());

        Assert.assertEquals(HTTP_OK ,response.getStatusLine().getStatusCode());

        String responseContent = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        Assert.assertEquals("Hello world!", responseContent);

        IOUtils.closeQuietly(client);
    }
}
