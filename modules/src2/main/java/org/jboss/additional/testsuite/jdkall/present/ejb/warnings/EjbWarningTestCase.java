package org.jboss.additional.testsuite.jdkall.present.ejb.warnings;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;


import javax.ejb.EJB;
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
        List<String> logfile = new LinkedList<>();
        
        final String logDir = System.getProperty("server.dir")+"/standalone/log";
        if (logDir == null) {
            throw new RuntimeException("Could not resolve jboss.server.log.dir");
        }
        final java.nio.file.Path logFile = Paths.get(logDir, "server.log");
        if (!Files.notExists(logFile)) {
            logfile = Files.readAllLines(logFile, StandardCharsets.UTF_8);
        }

        boolean warnExists=false;
        for(String l : logfile) {
            if(l.contains("Potential Interface view not exposed...")) {
                warnExists=true;
                break;
            }
        }
        assertTrue("Interface view could be implemented at an EJB session bean ... Warning not logged...", warnExists);
    }
    
}
