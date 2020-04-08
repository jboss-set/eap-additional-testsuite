package org.jboss.additional.testsuite.jdkall.present.ejb.warnings;

import java.io.File;
import java.io.FileInputStream;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;


import javax.ejb.EJB;
import javax.ejb.EJBException;
import org.apache.commons.io.IOUtils;
import static org.junit.Assert.fail;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.arquillian.container.test.api.RunAsClient;

@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java#20.0.0.Beta1", "modules/testcases/jdkAll/Eap72x/ejb/src/main/java#7.2.9", "modules/testcases/jdkAll/Eap72x-Proposed/ejb/main/java#7.2.9","modules/testcases/jdkAll/Eap7/ejb/src/main/java#7.3.2"})
public class EjbWarningTestCase {

    @EJB
    Hello bean;

    private static final String ARCHIVE_NAME = "EjbWarningTestCase";
     private final String serverLogPath = "target/surefire-reports/org.jboss.additional.testsuite.jdkall.present.ejb.warnings.EjbWarningTestCase-output.txt";

    @Deployment
    public static Archive<?> deploy() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, ARCHIVE_NAME + ".jar");
        jar.addClasses(EjbWarningTestCase.class, Hello.class, HelloBean.class, HelloBean2.class);

        return jar;
    }

    @Test
    public void checkWarning() throws Exception {
        String path = new File("").getAbsolutePath();

        FileInputStream inputStream = new FileInputStream(path + "/" + serverLogPath);
        try {
            String everything = IOUtils.toString(inputStream);
            assertTrue("Interface view could be implemented at a EJB session bean ...", everything.contains("WARN : Interface view could be implemented at the EJB Session Bean ..."));
        } finally {
            inputStream.close();
        }
    }
    
}
