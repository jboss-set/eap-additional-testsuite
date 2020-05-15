package org.jboss.additional.testsuite.jdkall.present.ejb.sfsbconcurrency;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import java.io.FileInputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertTrue;
import javax.inject.Inject;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@RunWith(Arquillian.class)
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java","modules/testcases/jdkAll/Eap72x/ejb/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap7/ejb/src/main/java"})
public class SfsbTestCase {

	private static final String ARCHIVE_NAME = "SfsbTestCase";
        private static final String DEPLOYMENT = ARCHIVE_NAME + ".jar";

        @Inject
	private SFBean3 bean3;

        @Deployment(name = DEPLOYMENT)
        public static Archive<?> deploy() {
            JavaArchive jar = ShrinkWrap.create(JavaArchive.class, ARCHIVE_NAME + ".jar");
            jar.addPackage("org.jboss.additional.testsuite.jdkall.present.ejb.sfsbconcurrency");
            return jar;
        }

    @Test
    public void ejbTest() {
        try {
            bean3.test();
        }catch(Exception e) {
            System.out.println("+++++++ " + e.getMessage());
            assertTrue("There should be an ejb concurrency exception ...", !e.getMessage().contains("Could not instantiate a proxy for a session bean:  Session bean [class org.jboss.additional.testsuite.jdkall.present.ejb.sfsbconcurrency.SFBean2 with qualifiers [@Any @Default]"));
        }
    }

}
