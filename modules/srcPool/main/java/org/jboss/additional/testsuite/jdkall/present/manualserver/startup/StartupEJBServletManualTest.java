package org.jboss.additional.testsuite.jdkall.present.manualserver.startup;

import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * For this test the server should be started manually using -Dfail param ...
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/manualserver/src/main/java", "modules/testcases/jdkAll/Eap72x/manualserver/src/main/java", "modules/testcases/jdkAll/Eap72x-Proposed/manualserver/src/main/java", "modules/testcases/jdkAll/Eap7/manualserver/src/main/java"})
public class StartupEJBServletManualTest {

    @Test
    public void testServerSuspentionMode() throws Exception {

        try {
            URL testURL = new URL("http://127.0.0.1:8080/one/servlet");

            final HttpGet request = new HttpGet(testURL.toString());
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            CloseableHttpResponse response = null;

            response = httpClient.execute(request);
            Assert.assertTrue("Servlet should not be available when initiating the server with param -Dfail...", !response.getStatusLine().toString().contains("200 OK"));
            IOUtils.closeQuietly(response);
            httpClient.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }

    }

}
