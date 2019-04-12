/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.additional.testsuite.jdkall.present.clustering.cluster.web;

import static org.jboss.additional.testsuite.jdkall.present.clustering.ClusterTestUtil.waitForReplication;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.additional.testsuite.jdkall.present.clustering.cluster.ClusterAbstractTestCase;
import org.jboss.additional.testsuite.jdkall.present.clustering.single.web.Mutable;
import org.jboss.additional.testsuite.jdkall.present.clustering.single.web.SimpleServlet;
import org.jboss.as.test.http.util.TestHttpClientUtils;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * Validate that <distributable/> works for a two-node cluster.
 *
 * @author Paul Ferraro
 * @author Radoslav Husar
 */
@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/clustering/src/main/java","modules/testcases/jdkAll/Eap72x/clustering/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/clustering/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/clustering/src/main/java","modules/testcases/jdkAll/Eap71x/clustering/src/main/java","modules/testcases/jdkAll/Eap70x/clustering/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/clustering/src/main/java"})
public class ClusteredWebSimpleTestCase extends ClusterAbstractTestCase {

    private static final int REQUEST_DURATION = 10000;
    private static final String MODULE_NAME = "ClusteredWebSimpleTestCase";

    @Deployment(name = DEPLOYMENT_1, managed=false, testable=false)
    @TargetsContainer(CONTAINER_1)
    public static Archive<?> deployment0() {
        WebArchive war = (WebArchive)getDeployment();
        return war;
    }

    @Deployment(name = DEPLOYMENT_2, managed=false, testable=false)
    @TargetsContainer(CONTAINER_2)
    public static Archive<?> deployment1() {
        WebArchive war = (WebArchive)getDeployment();
        return war;
    }

    private static Archive<?> getDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, MODULE_NAME + ".war");
        war.addClasses(SimpleServlet.class, Mutable.class);
        war.setWebXML("web.xml");
        return war;
    }

    @Test
    @OperateOnDeployment(DEPLOYMENT_1)
    public void testSerialized(@ArquillianResource(SimpleServlet.class) URL baseURL) throws IOException, URISyntaxException {
        
        // returns the URL of the deployment (http://127.0.0.1:8180/distributable)
        URI uri = SimpleServlet.createURI(baseURL);

        try (CloseableHttpClient client = TestHttpClientUtils.promiscuousCookieHttpClient()) {
            HttpResponse response = client.execute(new HttpGet(uri));
            try {
                Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
                Assert.assertEquals(1, Integer.parseInt(response.getFirstHeader("value").getValue()));
                Assert.assertFalse(Boolean.valueOf(response.getFirstHeader("serialized").getValue()));
            } finally {
                HttpClientUtils.closeQuietly(response);
            }

            response = client.execute(new HttpGet(uri));
            try {
                Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
                Assert.assertEquals(2, Integer.parseInt(response.getFirstHeader("value").getValue()));
                // This won't be true unless we have somewhere to which to replicate
                Assert.assertTrue(Boolean.valueOf(response.getFirstHeader("serialized").getValue()));
            } finally {
                HttpClientUtils.closeQuietly(response);
            }
        }
        
    }

    @Test
    @OperateOnDeployment(DEPLOYMENT_2) // For change, operate on the 2nd deployment first
    public void testSessionReplication(
            @ArquillianResource(SimpleServlet.class) @OperateOnDeployment(DEPLOYMENT_1) URL baseURL1,
            @ArquillianResource(SimpleServlet.class) @OperateOnDeployment(DEPLOYMENT_2) URL baseURL2)
            throws IOException, URISyntaxException {

        URI url1 = SimpleServlet.createURI(baseURL1);
        URI url2 = SimpleServlet.createURI(baseURL2);

        try (CloseableHttpClient client = TestHttpClientUtils.promiscuousCookieHttpClient()) {
            HttpResponse response = client.execute(new HttpGet(url1));
            try {
                Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
                Assert.assertEquals(1, Integer.parseInt(response.getFirstHeader("value").getValue()));
            } finally {
                HttpClientUtils.closeQuietly(response);
            }

            // Lets do this twice to have more debug info if failover is slow.
            response = client.execute(new HttpGet(url1));
            try {
                Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
                Assert.assertEquals(2, Integer.parseInt(response.getFirstHeader("value").getValue()));
            } finally {
                HttpClientUtils.closeQuietly(response);
            }

            // Lets wait for the session to replicate
            waitForReplication(GRACE_TIME_TO_REPLICATE);

            // Now check on the 2nd server
            response = client.execute(new HttpGet(url2));
            try {
                Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
                Assert.assertEquals(3, Integer.parseInt(response.getFirstHeader("value").getValue()));
            } finally {
                HttpClientUtils.closeQuietly(response);
            }

            // Lets do one more check.
            response = client.execute(new HttpGet(url2));
            try {
                Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
                Assert.assertEquals(4, Integer.parseInt(response.getFirstHeader("value").getValue()));
            } finally {
                HttpClientUtils.closeQuietly(response);
            }
        }
    }
}