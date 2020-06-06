/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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

package org.jboss.additional.testsuite.jdkall.present.microprofile;

import static org.jboss.shrinkwrap.api.ShrinkWrap.create;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;  
import io.smallrye.config.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/microprofile/src/main/java#20.0.0.Beta2","modules/testcases/jdkAll/Eap72x/microprofile/src/main/java#7.2.8","modules/testcases/jdkAll/Eap72x-Proposed/microprofile/src/main/java#7.2.8","modules/testcases/jdkAll/Eap7/microprofile/src/main/java#7.3.1.GA"})
public class SmallryeConfigTestCase {

    @Deployment
    public static WebArchive createArchive() {
        return create(WebArchive.class, "SmallryeConfigTestCase.war")
                .addClass(SmallryeConfigTestCase.class)
                .addPackage("io.smallrye.config");
    }

    @Test
    public void testSecuritySupport() throws Exception {
        try {
            Class<?> c = Class.forName("io.smallrye.config.SecuritySupport");
            if (Modifier.toString(c.getModifiers()).compareTo("public") == 0)
                fail("SecuritySupport class modifier should not be public ...");
            Method mthd[] = c.getDeclaredMethods();  
            for(int i = 0; i < mthd.length; i++) {  
                if(mthd[i].getName().compareTo("getContextClassLoader") == 0 && Modifier.toString(mthd[i].getModifiers()).compareTo("public") == 0)  
                    fail("getContextClassLoader() should not be accessible ...");
            }  
            
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
  
}
