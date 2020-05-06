package org.jboss.additional.testsuite.jdkall.present.ejb.spec;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import java.io.FileInputStream;
import org.apache.commons.io.IOUtils;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java","modules/testcases/jdkAll/Eap72x/ejb/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap7/ejb/src/main/java"})
public class SpecTestCase {

	private static final String ARCHIVE_NAME = "SpecTestCase";
        private static final String DEPLOYMENT = ARCHIVE_NAME + ".jar";
        private final String serverLogPath = "surefire-reports/org.jboss.additional.testsuite.jdkall.present.ejb.spec.SpecTestCase-output.txt";

        @Deployment(name = DEPLOYMENT)
        public static Archive<?> deploy() {
            JavaArchive jar = ShrinkWrap.create(JavaArchive.class, ARCHIVE_NAME + ".jar");
            jar.addClasses(org.jboss.additional.testsuite.jdkall.present.ejb.spec.SpecTestCase.class, org.jboss.additional.testsuite.jdkall.present.ejb.spec.ApplicationStoreBean.class, org.jboss.additional.testsuite.jdkall.present.ejb.spec.ApplicationStore.class,org.jboss.additional.testsuite.jdkall.present.ejb.spec.InvocationBean.class);
            return jar;
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
            assertFalse("Session EJBs should have only one of the following types : Stateful, Stateless, Singleton", everything.contains("Session EJBs should have only one of the following types : Stateful, Stateless, Singleton"));
        } finally {
            inputStream.close();
        }
    }


}
