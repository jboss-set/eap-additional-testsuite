/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.additional.testsuite.jdkall.general.security.roleToRolesMapping;

import java.io.FileInputStream;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.additional.testsuite.jdkall.general.security.roleToRolesMapping.mdb.JMSClientServlet;
import org.jboss.additional.testsuite.jdkall.general.security.roleToRolesMapping.mdb.MDBSample;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:psotirop@redhat.com">Panos</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly-Unmerged/security/src/main/java","modules/testcases/jdkAll/Eap/security/src/main/java"})
public class RoleToRolesMappingTestCase {

    private final String serverLogPath = "surefire-reports/org.jboss.additional.testsuite.jdkall.general.security.roleToRolesMapping.RoleToRolesMappingTestCase-output.txt";

    @Deployment
    public static WebArchive deployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class,"securityRoles.war");
        war.addClasses(JMSClientServlet.class);
        war.addClasses(MDBSample.class);
        war.addAsResource("WEB-INF/jboss-web.xml");
        war.setWebXML("WEB-INF/web.xml");
        return war;
    }

    @Test
    public void roleToRolesMappingServletTest(@ArquillianResource URL url) throws Exception {
        URL testURL = new URL(url.toString() + "JMSClientServlet");

        final HttpGet request = new HttpGet(testURL.toString());
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;

        try {
            response = httpClient.execute(request);
            System.out.println("Response : " + response.toString());

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            IOUtils.closeQuietly(response);
            httpClient.close();
            
            Thread.sleep(1000);
            
            String path = this.getClass().getClassLoader().getResource("").getPath();

            FileInputStream inputStream = new FileInputStream(path + "../" + serverLogPath);
            try {
                String everything = IOUtils.toString(inputStream);
                assertFalse("Autorization has failed ... ", everything.contains("MessageMDBSample is not allowed"));
            } finally {
                inputStream.close();
            }
        }
    }

}
