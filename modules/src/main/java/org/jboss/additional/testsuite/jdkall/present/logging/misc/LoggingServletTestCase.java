/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2016, Red Hat, Inc., and individual contributors
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
package org.jboss.additional.testsuite.jdkall.present.logging.misc;

import org.jboss.additional.testsuite.jdkall.present.logging.misc.log.LogResource;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.common.HttpRequest;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.osgi.metadata.ManifestBuilder;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import org.jboss.additional.testsuite.jdkall.present.logging.misc.log.LoggingUtil;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/logging/src/main/java","modules/testcases/jdkAll/Wildfly/logging/src/main/java","modules/testcases/jdkAll/WildflyRelease-17.0.0.Final/logging/src/main/java","modules/testcases/jdkAll/Eap72x/logging/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/logging/src/main/java","modules/testcases/jdkAll/Eap7/logging/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/logging/src/main/java","modules/testcases/jdkAll/Eap71x/logging/src/main/java"})
public class LoggingServletTestCase {
    @ArquillianResource
    private URL url;

    @Deployment(name="DEPLOYMENT1", order=1)
    public static WebArchive deployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, LoggingServletTestCase.class.getSimpleName() + ".war");
        war.addClasses(HttpRequest.class, LoggingTestServlet.class, LogResource.class, JaxRsActivator.class, LoggingUtil.class);
        war.addAsWebInfResource(new StringAsset("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "\n" +
                "<web-app version=\"3.0\"\n" +
                "         xmlns=\"http://java.sun.com/xml/ns/javaee\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd\"\n" +
                "         metadata-complete=\"false\">\n" +
                "<servlet-mapping>\n"
                + "        <servlet-name>javax.ws.rs.core.Application</servlet-name>\n"
                + "        <url-pattern>/*</url-pattern>\n"
                + "    </servlet-mapping>\n"
                + "\n"
                + "</web-app>"), "web.xml");
        war.setManifest(new Asset() {
            @Override
            public InputStream openStream() {
                ManifestBuilder builder = ManifestBuilder.newInstance();
                StringBuffer dependencies = new StringBuffer();
                builder.addManifestHeader("Dependencies",
                        dependencies.toString());
                builder.addManifestHeader("Logging-Profile", "profile-one");
                return builder.openStream();
            }
        });
        return war;
    }

    @Deployment(name="DEPLOYMENT2",order=2)
    public static WebArchive deployment2() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, LoggingServletTestCase.class.getSimpleName() + "2.war");
        war.addClasses(HttpRequest.class, LoggingTestServlet2.class, LogResource.class, JaxRsActivator.class, LoggingUtil.class);
        war.addAsWebInfResource(new StringAsset("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "\n" +
                "<web-app version=\"3.0\"\n" +
                "         xmlns=\"http://java.sun.com/xml/ns/javaee\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd\"\n" +
                "         metadata-complete=\"false\">\n" +
                "<servlet-mapping>\n"
                + "        <servlet-name>javax.ws.rs.core.Application</servlet-name>\n"
                + "        <url-pattern>/*</url-pattern>\n"
                + "    </servlet-mapping>\n"
                + "\n"
                + "</web-app>"), "web.xml");
        war.setManifest(new Asset() {
            @Override
            public InputStream openStream() {
                ManifestBuilder builder = ManifestBuilder.newInstance();
                StringBuffer dependencies = new StringBuffer();
                builder.addManifestHeader("Dependencies",
                        dependencies.toString());
                builder.addManifestHeader("Logging-Profile", "profile-two");
                return builder.openStream();
            }
        });
        return war;
    }

    @Test
    @OperateOnDeployment("DEPLOYMENT1")
    public void testServlet() throws Exception {
        HttpRequest.get(url.toExternalForm() + "simple", 10, SECONDS);
    }

    @Test
    @OperateOnDeployment("DEPLOYMENT2")
    public void testServlet2() throws Exception {
        HttpRequest.get(url.toExternalForm() + "simple2", 10, SECONDS);
    }

    private String performCall(String urlPattern) throws Exception {
        return HttpRequest.get(url + urlPattern, 10, TimeUnit.SECONDS);
    }

    @Test
    @OperateOnDeployment("DEPLOYMENT1")
    public void testProfiles1() throws Exception {
        performCall("rest/log");
        
        List<String> logfile = new LinkedList<>();
        
        final String logDir = System.getProperty("server.dir")+"/standalone/log";
        if (logDir == null) {
            throw new RuntimeException("Could not resolve jboss.server.log.dir");
        }
        final java.nio.file.Path logFile = Paths.get(logDir, "profile-one.log");
        if (!Files.notExists(logFile)) {
            logfile = Files.readAllLines(logFile, StandardCharsets.UTF_8);
        }
        
        assertTrue("profile-one.log has no content..." + logfile.size(), logfile.size()!=0);
    }
    
    @Test
    @OperateOnDeployment("DEPLOYMENT2")
    public void testProfiles2() throws Exception {
        performCall("rest/log");
        
        List<String> logfile = new LinkedList<>();
        
        final String logDir = System.getProperty("server.dir")+"/standalone/log";
        if (logDir == null) {
            throw new RuntimeException("Could not resolve jboss.server.log.dir");
        }
        final java.nio.file.Path logFile = Paths.get(logDir, "profile-two.log");
        if (!Files.notExists(logFile)) {
            logfile = Files.readAllLines(logFile, StandardCharsets.UTF_8);
        }
        
        assertTrue("profile-two.log has no content..." + logfile.size(), logfile.size()!=0);
    }

}
