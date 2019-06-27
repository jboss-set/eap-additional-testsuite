package org.jboss.additional.testsuite.jdkall.present.jmx.remoting;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests if a memory leak occurs when a new JMX connection is unsuccessful.
 *
 * Test case for [ JBEAP-16931 ]
 */
@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/remoting/src/main/java#16.0.0.Beta1", "modules/testcases/jdkAll/Eap72x/remoting/src/main/java#7.2.3", "modules/testcases/jdkAll/Eap72x-Proposed/remoting/src/main/java#7.2.3"})
public class JMXRemotingMemoryLeakTestCase {

    private final Logger log = Logger.getLogger(JMXRemotingMemoryLeakTestCase.class);

    @ContainerResource
    private ManagementClient managementClient;

    @Deployment
    public static Archive<?> getDeployment() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class);
        archive.addClass(JMXRemotingMemoryLeakTestCase.class);
        return archive;
    }

    @Test
    public void testJMXRemotingMemoryLeak() throws Exception {
        final String address = managementClient.getMgmtAddress() + ":1234";
        String jmxUrl = "service:jmx:remote+http://" + address;
        log.info("Using jboss jmx remoting url: " + jmxUrl);

        JMXServiceURL url = new JMXServiceURL(jmxUrl);
        int i = 0;
        int nonNull = 0;
        int exceptionThrownClosing = 0;

        // Do an initial GC to get a baseline free memory.
        System.gc();

        JMXConnector client = JMXConnectorFactory.newJMXConnector(url, null);
        long initialBytesFree = Runtime.getRuntime().freeMemory();
        while (i <= 3000) {
            assertEquals(0, nonNull);
            JMXConnector connector = null;
            try {
                client.connect();
            } catch (Exception e) {
                if (i == 0) {
                    e.printStackTrace();
                }
                if (connector != null) {
                    nonNull++;
                    try {
                        connector.close();
                    } catch (Exception e1) {
                        exceptionThrownClosing++;
                    }
                }
            }
            i++;
            if (i % 1000 == 0) {
                // Do a full GC before measuring again
                System.gc();

                long bytesFree = Runtime.getRuntime().freeMemory();
                log.info(new Date() + " | tried " + i + " | returned non-null " + nonNull
                        + " | exception thrown closing " + exceptionThrownClosing + " bytes Free= " + bytesFree);
                if (((long)initialBytesFree)-((long)bytesFree) > 2000000) {
                    Thread.sleep(100);
                    System.gc();
                    bytesFree = Runtime.getRuntime().freeMemory();
                    if (((long)initialBytesFree)-((long)bytesFree) > 2000000)
                        fail(((long)initialBytesFree)-((long)bytesFree) + " bytes of the memory is gone, even after full garbage collecting.");
                    else
                        initialBytesFree = Runtime.getRuntime().freeMemory();
                }else{
                    initialBytesFree = Runtime.getRuntime().freeMemory();
                }
            }
        }
    }
}
