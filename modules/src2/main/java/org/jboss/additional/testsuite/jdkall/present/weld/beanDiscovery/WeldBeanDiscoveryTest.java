package org.jboss.additional.testsuite.jdkall.present.weld.beanDiscovery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/weld/src/main/java","modules/testcases/jdkAll/Wildfly/weld/src/main/java#14.0.0.Final","modules/testcases/jdkAll/Eap72x/weld/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/weld/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/weld/src/main/java#7.1.3","modules/testcases/jdkAll/Eap71x/weld/src/main/java#7.1.3"})
public class WeldBeanDiscoveryTest {

    private final String serverLogPath = "target/surefire-reports/org.jboss.additional.testsuite.jdkall.present.weld.beanDiscovery.WeldBeanDiscoveryTest-output.txt";
    
    @Deployment
    public static Archive<?> deploy() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class);
        return jar;
    }

    @Test
    public void testServerStart() throws FileNotFoundException, IOException {
        String path = new File("").getAbsolutePath();

        FileInputStream inputStream = new FileInputStream(path + "/" + serverLogPath);
        try {
            String everything = IOUtils.toString(inputStream);
            assertFalse("Testing DeploymentException: WELD-001408", everything.contains("DeploymentException: WELD-001408"));
        } finally {
            inputStream.close();
        }
    }


}
