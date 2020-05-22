package org.jboss.additional.testsuite.jdkall.present.ejb.singleton.postconstruct;

import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

@RunAsClient
@RunWith(Arquillian.class)
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java", "modules/testcases/jdkAll/Eap7/ejb/src/main/java", "modules/testcases/jdkAll/Eap72x/ejb/src/main/java", "modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java"})
public class PostconstructSingletonTestCase {

    public static final String DEPLOYMENT = "PostconstructSingletonTestCase.ear";
    public static final String DEPLOYMENT2 = "PostconstructSingletonTestCase2.ear";
    private static final String APPLICATION_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"\n" +
"<application xmlns=\"http://java.sun.com/xml/ns/javaee\"\n" +
"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
"  xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/application_6.xsd\"\n" +
"  version=\"6\">\n" +
"\n" +
"	<module>\n" +
"    <web>\n" +
"      <web-uri>one.war</web-uri>\n" +
"			<context-root>one</context-root>\n" +
"    </web>\n" +
"  </module>\n" +
"\n" +
"	<module>\n" +
"		<ejb>two.jar</ejb>\n" +
"	</module>\n" +
"\n" +
"	<initialize-in-order>true</initialize-in-order>\n" +
"</application>";
    
    private static final String APPLICATION_XML2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"\n" +
"<application xmlns=\"http://java.sun.com/xml/ns/javaee\"\n" +
"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
"  xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/application_6.xsd\"\n" +
"  version=\"6\">\n" +
"\n" +
"	<module>\n" +
"		<ejb>two.jar</ejb>\n" +
"	</module>\n" +
"\n" +
"	<module>\n" +
"    <web>\n" +
"      <web-uri>three.war</web-uri>\n" +
"			<context-root>three</context-root>\n" +
"    </web>\n" +
"  </module>\n" +
"\n" +
"	<initialize-in-order>false</initialize-in-order>\n" +
"</application>";

    @Deployment(name = DEPLOYMENT)
    public static Archive<?> getDeployment() {
        EnterpriseArchive ear = ShrinkWrap
                .create(EnterpriseArchive.class, DEPLOYMENT);

        ear.setApplicationXML(new StringAsset(APPLICATION_XML));
        
        WebArchive war = ShrinkWrap.create(WebArchive.class, "one.war");
        war.addClass(Servlet1.class);
        
        JavaArchive ejb = ShrinkWrap
                .create(JavaArchive.class, "two.jar")
                .addClass(ExampleSingleton.class);
        
        ear.addAsModules(war,ejb);

        return ear;
    }
    
    @Deployment(name = DEPLOYMENT2)
    public static Archive<?> getDeployment2() {
        EnterpriseArchive ear = ShrinkWrap
                .create(EnterpriseArchive.class, DEPLOYMENT2);

        ear.setApplicationXML(new StringAsset(APPLICATION_XML2));
        
        JavaArchive ejb = ShrinkWrap
                .create(JavaArchive.class, "two.jar")
                .addClass(ExampleSingleton.class);
        
        WebArchive war2 = ShrinkWrap.create(WebArchive.class, "three.war");
        war2.addClass(Servlet2.class);
        war2.addClass(ExampleSingleton.class);
        
        ear.addAsModules(ejb,war2);

        return ear;
    }

    @Test
    @OperateOnDeployment(DEPLOYMENT)
    public void postconstructTest(@ArquillianResource URL url) throws Exception {
        URL testURL = new URL(url.toString() + "servlet1");

        final HttpGet request = new HttpGet(testURL.toString());
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;

        try {
            response = httpClient.execute(request);
            Assert.assertEquals("Failed to access " + testURL, HttpURLConnection.HTTP_OK, response.getStatusLine().getStatusCode());
        }finally {
            IOUtils.closeQuietly(response);
            httpClient.close();
        }
    }
    
    @Test
    @OperateOnDeployment(DEPLOYMENT2)
    public void postconstructTest2(@ArquillianResource URL url) {
        URL testURL=null;
        try {
            testURL = new URL(url.toString() + "servlet2");
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        final HttpGet request = new HttpGet(testURL.toString());
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;

        try {
            response = httpClient.execute(request);
            Assert.assertEquals("Failed to access " + testURL, HttpURLConnection.HTTP_OK, response.getStatusLine().getStatusCode());
        } catch (IOException ex) {
            ex.printStackTrace();
        }finally {
            IOUtils.closeQuietly(response);
            try {
                httpClient.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
