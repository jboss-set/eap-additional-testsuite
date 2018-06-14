/*
 * JBoss, Home of Professional Open Source.
 * Copyleft 2017, Red Hat, Inc., and individual contributors
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
package org.jboss.additional.testsuite.jdkall.present.jaxrs.client;

import org.apache.http.impl.client.CloseableHttpClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jboss.additional.testsuite.jdkall.present.jaxrs.client.resource.ApacheHttpClient43Resource;
import org.jboss.additional.testsuite.jdkall.present.jaxrs.client.resource.ApacheHttpClient43ResourceImpl;
import org.jboss.additional.testsuite.jdkall.present.jaxrs.packaging.war.WebXml;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;

@RunAsClient
@RunWith(Arquillian.class)
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap7/jaxrs/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/jaxrs/src/main/java","modules/testcases/jdkAll/Eap71x/jaxrs/src/main/java"})
public class ApacheHttpClient432TestCase {

    public static final String DEPLOYMENT = "ApacheHttpClient432TestCase.war";
    static Logger logger = Logger.getLogger(ApacheHttpClient432TestCase.class);

    @Deployment(name = DEPLOYMENT)
    public static Archive<?> getDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, DEPLOYMENT);
        war.addClass(ApacheHttpClient43Resource.class);
        war.addClass(ApacheHttpClient43ResourceImpl.class);
        war.addAsWebInfResource(WebXml.get("<servlet-mapping>\n"
                + "        <servlet-name>javax.ws.rs.core.Application</servlet-name>\n"
                + "        <url-pattern>/*</url-pattern>\n"
                + "    </servlet-mapping>\n"
                + "\n"), "web.xml");
        return war;
    }

    @Test
    @OperateOnDeployment(DEPLOYMENT)
    public void apacheHttpClient4EngineServletTest(@ArquillianResource URL url) throws Exception {
        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .setSoKeepAlive(true)
                .setSoReuseAddress(true)
                .build();

        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

        connManager.setMaxTotal(100);

        connManager.setDefaultMaxPerRoute(100);

        connManager.setDefaultSocketConfig(socketConfig);

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(2000)
                .setConnectTimeout(100)
                .setConnectionRequestTimeout(3000)
                .setStaleConnectionCheckEnabled(true)
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .setConnectionManager(connManager)
                .build();

        final ClientHttpEngine executor;

        executor = new ApacheHttpClient43Engine(httpClient);

        ResteasyClient client = new ResteasyClientBuilder().httpEngine(executor).build();

        final ApacheHttpClient43Resource proxy = client.target("http://127.0.0.1:8080/" + ApacheHttpClient432TestCase.class.getSimpleName()).proxy(ApacheHttpClient43Resource.class);

        WebTarget target = client.target("http://127.0.0.1:8080/" + ApacheHttpClient432TestCase.class.getSimpleName() + "/test2");

        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

        try {
            Response s = proxy.get();

            assertEquals(200, s.getStatus());
        } catch (ProcessingException e) {
            logger.warn("Exception occured." + e);

        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
