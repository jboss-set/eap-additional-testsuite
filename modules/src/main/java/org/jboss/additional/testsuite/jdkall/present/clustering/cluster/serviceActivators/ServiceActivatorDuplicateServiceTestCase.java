/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
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
package org.jboss.additional.testsuite.jdkall.present.clustering.cluster.serviceActivators;

import java.io.File;
import java.io.FileInputStream;
import org.apache.commons.io.IOUtils;
import org.jboss.additional.testsuite.jdkall.present.clustering.cluster.ClusterAbstractTestCase;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertFalse;

@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/clustering/src/main/java#11.0.0-Alpha1*13.0.0","modules/testcases/jdkAll/Eap7/clustering/src/main/java#7.1.0-Alpha1","modules/testcases/jdkAll/Eap71x/clustering/src/main/java#7.1.0-Alpha1"})
public class ServiceActivatorDuplicateServiceTestCase extends ClusterAbstractTestCase {
    
    private final String serverLogPath = "target/surefire-reports/org.jboss.additional.testsuite.jdkall.present.clustering.cluster.serviceActivators.ServiceActivatorDuplicateServiceTestCase-output.txt";
    
    @Deployment(name = DEPLOYMENT_1, managed=false, testable=false)
    @TargetsContainer(CONTAINER_1)
    public static Archive<?> getDeployment1() {
       File earFile = new File("testClusterDublicateServiceException.ear");
       EnterpriseArchive archive = ShrinkWrap.createFromZipFile(EnterpriseArchive.class, earFile);
       return archive;
    }

    @Deployment(name = DEPLOYMENT_2, managed=false, testable=false)
    @TargetsContainer(CONTAINER_2)
    public static Archive<?> getDeployment2() {
       File earFile = new File("testClusterDublicateServiceException.ear");
       EnterpriseArchive archive = ShrinkWrap.createFromZipFile(EnterpriseArchive.class, earFile);
       return archive;
    }

    @Test
    @OperateOnDeployment(DEPLOYMENT_1)
    public void testServerStart() throws Exception {
        String path = new File("").getAbsolutePath();

        FileInputStream inputStream = new FileInputStream(path + "/" + serverLogPath);
        try {
            String everything = IOUtils.toString(inputStream);
            assertFalse("Testing ClusterDublicateServiceException", everything.contains("DuplicateServiceException"));
        } finally {
            inputStream.close();
        }
    }
}
