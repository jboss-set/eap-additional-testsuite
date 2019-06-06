/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2019, Red Hat, Inc., and individual contributors
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
package org.jboss.additional.testsuite.jdkall.present.web.security.identity.propagation;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.additional.testsuite.jdkall.present.web.security.identity.propagation.deployment.Hello;
import org.jboss.additional.testsuite.jdkall.present.web.security.identity.propagation.deployment.HelloBean;
import org.jboss.additional.testsuite.jdkall.present.web.security.identity.propagation.deployment.IdentityPropagationServlet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.shrinkwrap.api.asset.StringAsset;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test configures Elytron to use Identity Propagation.
 * Test deploys application with the secured servlet and checks if the identity sets by
 * HttpServletRequest.login() is propagated into the secured EJB.
 *
 * Test for [ JBEAP-16152 ].
 *
 * @author Daniel Cihak
 */
@RunWith(Arquillian.class)
@ServerSetup(IdentityPropagationServerSetupTask.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/web/src/main/java#15.0.0.Beta1","modules/testcases/jdkAll/WildflyRelease-17.0.0.Alpha1/web/src/main/java", "modules/testcases/jdkAll/Eap72x/web/src/main/java#7.2.1", "modules/testcases/jdkAll/Eap72x-Proposed/web/src/main/java#7.2.1"})
public class IdentityPropagationAuthenticationTestCase {

    private static final String DEPLOYMENT = "httpRequestLogin";
    public static final String USER = "user1";
    public static final String PASSWORD = "password1";

    @Deployment(name=DEPLOYMENT)
    public static Archive<?> createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, DEPLOYMENT + ".war");
        war.addClasses(HelloBean.class, Hello.class, IdentityPropagationServlet.class);
        war.addAsWebInfResource(new StringAsset("<?xml version='1.0' encoding='UTF-8'?>\n" +
                "\n" +
                "<web-app version=\"3.0\"\n" +
                "         xmlns=\"http://java.sun.com/xml/ns/javaee\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd\" >\n" +
                "\n" +
                "    <security-constraint>\n" +
                "        <web-resource-collection>\n" +
                "            <web-resource-name>Protect all application</web-resource-name>\n" +
                "            <url-pattern>/*</url-pattern>\n" +
                "        </web-resource-collection>\n" +
                "        <auth-constraint>\n" +
                "            <role-name>*</role-name>\n" +
                "        </auth-constraint>\n" +
                "    </security-constraint>\n" +
                "\n" +
                "    <login-config>\n" +
                "        <auth-method>BASIC</auth-method>\n" +
                "        <realm-name>Application Realm</realm-name>\n" +
                "    </login-config>\n" +
                "\n" +
                "    <security-role>\n" +
                "        <description>Role required to log in to the application</description>\n" +
                "        <role-name>guest</role-name>\n" +
                "    </security-role>\n" +
                "\n" +
                "</web-app>"), "web.xml");
        war.addAsWebInfResource(new StringAsset("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jboss-web>\n" +
                "   <security-domain>auth-test</security-domain>\n" +
                "</jboss-web>\n"), "jboss-web.xml");
        return war;
    }

    @Test
    public void testIdentityPropagationAuthentication(@ArquillianResource URL url) throws Exception {
        HttpGet httpGet = new HttpGet(url.toExternalForm() + "IdentityPropagationServlet/");
        HttpResponse response = null;

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(url.getHost(), url.getPort()), new UsernamePasswordCredentials(USER, PASSWORD));
        try (CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .build()) {
            response = httpclient.execute(httpGet);
        }

        assertNotNull("Response is 'null', we expected non-null response!", response);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }
}
