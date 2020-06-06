/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.additional.testsuite.jdkall.present.security.cli;

import org.jboss.as.test.integration.domain.management.util.DomainTestSupport;
import org.jboss.as.test.integration.management.base.AbstractCliTestBase;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/security/src/main/java","modules/testcases/jdkAll/Wildfly/security/src/main/java","modules/testcases/jdkAll/WildflyRelease-17.0.0.Final/security/src/main/java","modules/testcases/jdkAll/Eap72x/security/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/security/src/main/java","modules/testcases/jdkAll/Eap7/security/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/security/src/main/java","modules/testcases/jdkAll/Eap71x/security/src/main/java"})
public class CliTestCase extends AbstractCliTestBase {

    @BeforeClass
    public static void before() throws Exception {
        AbstractCliTestBase.initCLI(DomainTestSupport.masterAddress);
    }

    @AfterClass
    public static void after() throws Exception {
        AbstractCliTestBase.closeCLI();
    }


    @Test
    @Ignore
    public void jaccPolicyTest() throws Exception {
        cli.sendLine("/subsystem=elytron/policy=jacc:add(jacc-policy=[{name => jacc}])");
        cli.sendLine("reload");
        cli.sendLine("/subsystem=elytron/policy=jacc:list-add(name=custom-policy, value={name=foo, class-name=bar})");
        cli.sendLine("reload");
        cli.sendLine("/subsystem=elytron/policy=jacc:list-remove(name=custom-policy, index=0)");
        cli.sendLine("reload");
        cli.sendLine("/subsystem=elytron/policy=jacc:remove()");
        cli.sendLine("reload");
        try {
            cli.sendLine("/subsystem=elytron/policy=jacc:add(jacc-policy=[{name => jacc}])");
        }catch(Exception e){ // This part is not needed as the assertion which fails in inside sendLine method
            assertTrue("StackOverflowError when adding and removing jacc policy in the elytron subsystem.",false);
        }
    }

    @Test
    @Ignore
    public void invalidAttributeTest() throws Exception {
        cli.sendLine("/subsystem=remoting/http-connector=random:add(connector-ref=default)");
        cli.sendLine("/subsystem=remoting/http-connector=random/security=sasl:add()");
        cli.sendLine("/subsystem=remoting/http-connector=random/security=sasl:write-attribute(name=qop,value=[aaa])");
        try {
            cli.sendLine("reload");
        }catch(Exception e){ // This part is not needed as the assertion which fails in inside sendLine method
            assertTrue("Server could not start.",false);
        }
    }
    
}
