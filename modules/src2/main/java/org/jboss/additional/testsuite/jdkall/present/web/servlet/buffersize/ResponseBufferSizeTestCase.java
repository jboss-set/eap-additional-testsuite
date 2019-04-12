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

package org.jboss.additional.testsuite.jdkall.present.web.servlet.buffersize;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

@RunAsClient
@RunWith(Arquillian.class)
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/web/src/main/java","modules/testcases/jdkAll/Wildfly/web/src/main/java","modules/testcases/jdkAll/Eap72x/web/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/web/src/main/java","modules/testcases/jdkAll/Eap7/web/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/web/src/main/java","modules/testcases/jdkAll/Eap71x/web/src/main/java","modules/testcases/jdkAll/Eap7.1.0.Beta/web/src/main/java","modules/testcases/jdkAll/Eap70x/web/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/web/src/main/java","modules/testcases/jdkAll/Eap64x/web/src/main/java","modules/testcases/jdkAll/Eap64x-Proposed/web/src/main/java","modules/testcases/jdkAll/WildflyRelease-10.1.0.Final/web/src/main/java"})
public class ResponseBufferSizeTestCase {

    public static final String DEPLOYMENT = "response-buffer-size.war";

    @Deployment(name = DEPLOYMENT)
    public static Archive<?> getDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, DEPLOYMENT);
        war.addClass(ResponseBufferSizeServlet.class);
        return war;
    }

    @Test
    @OperateOnDeployment(DEPLOYMENT)
    public void increaseBufferSizeTest(@ArquillianResource URL url) throws Exception {
        URL testURL = new URL(url.toString() + "ResponseBufferSizeServlet?"
                + ResponseBufferSizeServlet.SIZE_CHANGE_PARAM_NAME + "=1.5"
                + "&" + ResponseBufferSizeServlet.DATA_LENGTH_IN_PERCENTS_PARAM_NAME + "=0.8"); // more than original size, less than new buffer size

        final HttpGet request = new HttpGet(testURL.toString());
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;

        try {
            response = httpClient.execute(request);
            Assert.assertEquals("Failed to access " + testURL, HttpURLConnection.HTTP_OK, response.getStatusLine().getStatusCode());
            String content = EntityUtils.toString(response.getEntity());
            Assert.assertFalse(content.contains(ResponseBufferSizeServlet.RESPONSE_COMMITED_MESSAGE));
            final Header[] transferEncodingHeaders = response.getHeaders("Transfer-Encoding");
            final Header[] contentLengthHeader = response.getHeaders("Content-Length");

            for (Header transferEncodingHeader : transferEncodingHeaders) {
                Assert.assertNotEquals("Transfer-Encoding shouldn't be chunked as set BufferSize shouldn't be filled yet, "
                        + "probably caused due https://bugzilla.redhat.com/show_bug.cgi?id=1212566", "chunked", transferEncodingHeader.getValue());
            }

            Assert.assertFalse("Content-Length header not specified", contentLengthHeader.length == 0);
        } finally {
            IOUtils.closeQuietly(response);
            httpClient.close();
        }
    }
}
