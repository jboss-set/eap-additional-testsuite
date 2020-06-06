package org.jboss.additional.testsuite.jdkall.present.ejb.spec;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import java.io.FileInputStream;
import org.apache.commons.io.IOUtils;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java","modules/testcases/jdkAll/Eap7/ejb/src/main/java#7.3.0*7.3.9"})
public class Spec2TestCase {

	private static final String ARCHIVE_NAME = "Spec2TestCase";
        private static final String ARCHIVE_NAME2 = "Spec22TestCase";
        private static final String DEPLOYMENT = ARCHIVE_NAME + ".jar";
        private static final String DEPLOYMENT2 = ARCHIVE_NAME2 + ".jar";
        private final String serverLogPath = "surefire-reports/org.jboss.additional.testsuite.jdkall.present.ejb.spec.Spec2TestCase-output.txt";

        @Deployment(name = DEPLOYMENT)
        public static Archive<?> deploy() {
            WebArchive war = ShrinkWrap.create(WebArchive.class, ARCHIVE_NAME + ".war");
            war.addClasses(org.jboss.additional.testsuite.jdkall.present.ejb.spec.Spec2TestCase.class, org.jboss.additional.testsuite.jdkall.present.ejb.spec.TestEjb.class);
            war.addAsWebInfResource("ejb-jar.xml","ejb-jar.xml");
            return war;
        }

        @Deployment(name = DEPLOYMENT2)
        public static Archive<?> deploy2() {
            WebArchive war = ShrinkWrap.create(WebArchive.class, ARCHIVE_NAME2 + ".war");
            war.addClasses(org.jboss.additional.testsuite.jdkall.present.ejb.spec.Spec2TestCase.class, org.jboss.additional.testsuite.jdkall.present.ejb.spec.TestEjb.class);
            war.addAsWebInfResource("ejb-jar2.xml","ejb-jar.xml");
            return war;
        }


    @Test
    public void ejbSpecTest() throws Exception {
        checkLog();
    }

    private void checkLog() throws Exception {
        String path = this.getClass().getClassLoader().getResource("").getPath();

        FileInputStream inputStream = new FileInputStream(path + "../" + serverLogPath);
        try {
            String everything = IOUtils.toString(inputStream);
            assertFalse("SessionBeans should have only one of the following types : Stateful, Stateless, Singleton", everything.contains("SessionBeans should have only one of the following types : Stateful, Stateless, Singleton"));
        } finally {
            inputStream.close();
        }
    }


}
