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
    private final long bytesLim = 2500000;
    private final int GCNUM = 5;
    // This test fails intermittently, so artificially repeating
    // the test increases the odd of running into the issue
    private int NB_REPEAT_TEST = 1;
    private static final String REPEAT_TEST_PROPERTY_NAME = "eat.remoting.jmx.remote.memoryleak.test.repeat";

    @ContainerResource
    private ManagementClient managementClient;

    public JMXRemotingMemoryLeakTestCase() {
        NB_REPEAT_TEST = retrievePropertyValueIfSet(System.getProperty(REPEAT_TEST_PROPERTY_NAME));
    }

    private int retrievePropertyValueIfSet(String property) {
        return ! "".equals(property) ? convertPropertyValueToInt(property) : NB_REPEAT_TEST;
	}

	private int convertPropertyValueToInt(String property) {
        try {
            return Integer.valueOf(property);
        } catch ( NumberFormatException e) {
            throw new IllegalArgumentException("Property " + REPEAT_TEST_PROPERTY_NAME + " required a valid integer:"+ property);
        }
    }

    @Deployment
    public static Archive<?> getDeployment() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class);
        archive.addClass(JMXRemotingMemoryLeakTestCase.class);
        return archive;
    }

    @Test
    public void testJMXRemotingMemoryLeak() throws Exception {
        for ( int i = 0; i < NB_REPEAT_TEST ; i++ )
            runTestCase();
    }

    public void runTestCase() throws Exception {
        final String address = managementClient.getMgmtAddress() + ":1234";
        String jmxUrl = "service:jmx:remote+http://" + address;
        log.info("Using jboss jmx remoting url: " + jmxUrl);

        JMXServiceURL url = new JMXServiceURL(jmxUrl);
        int i = 0;
        int nonNull = 0;
        int exceptionThrownClosing = 0;

        // Do an initial GC to get a baseline free memory.
        System.gc();

     //   JMXConnector connector = JMXConnectorFactory.newJMXConnector(url, null);
        long initialBytesFree = Runtime.getRuntime().freeMemory();
        while (i <= 3000) {
            JMXConnector connector = null;
            assertEquals(0, nonNull);
            try {
                connector = JMXConnectorFactory.connect(url);
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
                System.out.println("initialBytesFree : " + initialBytesFree + " bytesFree : " + bytesFree + " diff : " + (((long)initialBytesFree)-((long)bytesFree)));
                System.out.println(new Date() + " | tried " + i + " | returned non-null " + nonNull
                        + " | exception thrown closing " + exceptionThrownClosing + " bytes Free= " + bytesFree);
                if (((long)initialBytesFree)-((long)bytesFree) > bytesLim) {
                    Thread.sleep(1000);
                    bytesFree = Runtime.getRuntime().freeMemory();
                    System.out.println("initialBytesFree : " + initialBytesFree + " bytesFree : " + bytesFree + " diff : " + (((long)initialBytesFree)-((long)bytesFree)));
                    int num=0;
                    while (((long)initialBytesFree)-((long)bytesFree) > bytesLim && num<GCNUM) {
                        System.gc();
                        Thread.sleep(1000);
                        bytesFree = Runtime.getRuntime().freeMemory();
                        num++;
                    }
                    if (((long)initialBytesFree)-((long)bytesFree) > bytesLim)
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
