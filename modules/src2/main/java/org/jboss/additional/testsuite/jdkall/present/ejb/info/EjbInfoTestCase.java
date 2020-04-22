package org.jboss.additional.testsuite.jdkall.present.ejb.info;

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
public class EjbInfoTestCase {

    @EJB
    Hello bean;

    private static final String ARCHIVE_NAME = "EjbInfoTestCase";

    @Deployment
    public static Archive<?> deploy() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, ARCHIVE_NAME + ".jar");
        jar.addClasses(EjbInfoTestCase.class, Hello.class, Hello3.class, Hello4.class, HelloBean.class, HelloBean2.class, HelloBean3.class, HelloBean4.class);

        return jar;
    }

    @Test
    public void checkPotentialInterfaceExposureInfo() throws Exception {
        List<String> logfile = serverLog();

        boolean infoExists=false;
        for(String l : logfile) {
            if(l.contains("SessionBean org.jboss.additional.testsuite.jdkall.present.ejb.info.HelloBean2 does not expose potential Interface view...")) {
                infoExists=true;
                break;
            }
        }
        assertTrue("Interface view could be implemented at an EJB session bean ... Info not logged...", infoExists);
    }

    @Test
    public void checkPotentialInterfaceExposureInfo2() throws Exception {
        List<String> logfile = serverLog();

        boolean infoExists=false;
        for(String l : logfile) {
            if(l.contains("SessionBean org.jboss.additional.testsuite.jdkall.present.ejb.info.HelloBean3 does not expose potential Interface view...")) {
                infoExists=true;
                break;
            }
        }
        assertTrue("Interface view could be implemented at an EJB session bean ... Info not logged...", infoExists);
    }

    private List<String> serverLog() throws Exception {
        List<String> logfile = new LinkedList<>();
        
        final String logDir = System.getProperty("server.dir")+"/standalone/log";
        if (logDir == null) {
            throw new RuntimeException("Could not resolve jboss.server.log.dir");
        }
        final java.nio.file.Path logFile = Paths.get(logDir, "server.log");
        if (!Files.notExists(logFile)) {
            logfile = Files.readAllLines(logFile, StandardCharsets.UTF_8);
        }

        return logfile;
    }
    
}
