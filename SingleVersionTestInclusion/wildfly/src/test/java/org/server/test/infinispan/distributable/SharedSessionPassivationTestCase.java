package org.server.test.infinispan.distributable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
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
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.http.util.TestHttpClientUtils;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.Test;

import org.server.test.infinispan.distributable.log.LogChecker;
import org.server.test.infinispan.distributable.log.ModelNodeLogChecker;
import org.jboss.arquillian.container.test.api.Deployer;

/**
 * Validate that sessions aren't passivated when {@code <distributable/>} is used.
 * Default HA configuration is used, i.e. <b>standalone-ha.xml</b>, default Inifinispan subsytem configuration,
 * secifically defaul file store path.
 * The only configuration is represented by {@code <distributable/>}, which is added to the <b>web.xml</b>
 *
 * See https://issues.redhat.com/browse/JBEAP-18447
 */
@RunWith(Arquillian.class)
public class SharedSessionPassivationTestCase {

    private static final String MODULE_NAME = SharedSessionPassivationTestCase.class.getSimpleName();
    private static final String MANAGED_DEPLOYMENT = "deployment";
    private static final String APPLICATION_NAME = MODULE_NAME + ".war";
    private static final String WEB_XML =
            "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\"\n" +
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd\"\n" +
            "         version=\"3.0\">\n" +
            "    <distributable/>\n" +
            "</web-app>\n";

    @ArquillianResource
    private Deployer deployer;

    @Deployment(name = MANAGED_DEPLOYMENT, managed = false, testable = false)
    public static Archive<?> deployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, APPLICATION_NAME);
        war.addClasses(SimpleServlet.class, Mutable.class);
        war.setWebXML(new StringAsset(WEB_XML));
        return war;
    }

    private String getDefaultCacheFileStorePath() {
        final String separator = FileSystems.getDefault().getSeparator();
        return System.getProperty("jboss.home")
                .concat(separator)
                .concat("standalone")
                .concat(separator)
                .concat("data")
                .concat(separator)
                .concat("infinispan")
                .concat(separator)
                .concat("web")
                .concat(separator)
                .concat(APPLICATION_NAME + ".dat");
    }

    /**
     * Deploy all deployments so it's possible to get their URLs in other tests. This is limitation of Arquillian
     * when deployments are deployed manually (in tests) and thus their URL is not known in time when test is started.
     * The workaround for this issue is to deploy all deployments in first test which allows to inject
     * deployment URL as params in other test methods.
     */
    @Test
    @InSequence(1)
    public void deployAll() {
        deployer.deploy(MANAGED_DEPLOYMENT);
    }

    /**
     * Deploys the WAR archive to the server and then performs http calls in order to create sessions.
     * Intermediate and final verifications - i.e. before and after undeployment - are executed to assess that file
     * store contents aren't changed. Finally the log is checked to verify that no messages were logged by the
     * Infinispan subsystem withh respect to entries passivation.
     *
     * @param baseURL Base URL to reach the deployment
     * @param managementClient Instance of ManagmentClient provided by Arquillian.
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    @InSequence(10)
    @OperateOnDeployment(MANAGED_DEPLOYMENT)
    public void test(@ArquillianResource(SimpleServlet.class) URL baseURL,
                     @ArquillianResource @OperateOnDeployment(MANAGED_DEPLOYMENT) ManagementClient managementClient)
            throws IOException, URISyntaxException {

        final String cacheFileStorePath = getDefaultCacheFileStorePath();
        final byte[] cacheFileStoreInitialContent = Files.readAllBytes(Paths.get(cacheFileStorePath));

        URI uri = SimpleServlet.createURI(baseURL);

        try (CloseableHttpClient client = TestHttpClientUtils.promiscuousCookieHttpClient()) {
            HttpResponse response = client.execute(new HttpGet(uri));
            try {
                Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
                Assert.assertEquals(1, Integer.parseInt(response.getFirstHeader("value").getValue()));
            } finally {
                HttpClientUtils.closeQuietly(response);
            }

            response = client.execute(new HttpGet(uri));
            try {
                Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
                Assert.assertEquals(2, Integer.parseInt(response.getFirstHeader("value").getValue()));
            } finally {
                HttpClientUtils.closeQuietly(response);
            }
        }

        final byte[] cacheFileStoreIntermediateContent = Files.readAllBytes(Paths.get(cacheFileStorePath));
        Assert.assertArrayEquals("Initial and intermediate file store contents differ",
                cacheFileStoreInitialContent, cacheFileStoreIntermediateContent);

        //  udeployment will cause passivation before JBEAP-18447
        deployer.undeploy(MANAGED_DEPLOYMENT);

        final byte[] cacheFileStoreFinalContent = Files.readAllBytes(Paths.get(cacheFileStorePath));
        Assert.assertArrayEquals("Initial and final file store contents differ",
                cacheFileStoreInitialContent, cacheFileStoreFinalContent);

        LogChecker logChecker = new ModelNodeLogChecker(managementClient, 30, true);
        Assert.assertFalse("Log contains message ID \"ISPN000029\", related to entries about to be passivated",
                logChecker.logContains("ISPN000029"));
        Assert.assertFalse("Log contains message ID \"ISPN000030\", related to successfully passivated entries",
                logChecker.logContains("ISPN000030"));
    }
}
