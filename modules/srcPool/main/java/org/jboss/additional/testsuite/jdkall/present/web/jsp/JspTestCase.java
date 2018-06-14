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

package org.jboss.additional.testsuite.jdkall.present.web.jsp;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.common.HttpRequest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import org.junit.runner.RunWith;
import java.io.File;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@RunWith(Arquillian.class)
@RunAsClient
//@apAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/web/src/main/java","modules/testcases/jdkAll/Wildfly/web/src/main/java","modules/testcases/jdkAll/Eap7x/web/src/main/java","modules/testcases/jdkAll/Eap71x/web/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/web/src/main/java","modules/testcases/jdkAll/Eap70x/web/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/web/src/main/java"})
public class JspTestCase {

    @Deployment
    public static WebArchive deploy() {
        return ShrinkWrap.createFromZipFile(WebArchive.class, new File("src/test/resources/testTag-1.0-SNAPSHOT.war"));
    }

    @Test
    public void testJspTags() throws Exception {
        try {
            final String responseBody = HttpRequest.get("http://localhost:8080/testTag-1.0-SNAPSHOT/test.jsp", 10, TimeUnit.SECONDS);      
            assertTrue("Error when calling test.jsp ...", !responseBody.contains("org.apache.jasper.JasperException"));
        }catch(Exception e) {
            assertTrue("Error when calling test.jsp ...",false);
        }
    }

  
}
