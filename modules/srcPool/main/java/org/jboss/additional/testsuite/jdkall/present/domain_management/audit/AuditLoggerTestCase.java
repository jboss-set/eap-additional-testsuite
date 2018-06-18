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
package org.jboss.additional.testsuite.jdkall.present.domain_management.audit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OUTCOME;
import org.jboss.as.test.integration.management.base.AbstractCliTestBase;
import org.jboss.as.test.integration.management.util.CLIOpResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.Assert.assertThat;

/**
 * 
 * @author Panagiotis Sotiropoulos
 */
@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/domain_management/src/main/java","modules/testcases/jdkAll/Eap7/domain_management/src/main/java#7.0.2-GA","modules/testcases/jdkAll/Wildfly/domain_management/src/main/java#11.0.0-Alpha1"})
public class AuditLoggerTestCase extends AbstractCliTestBase {

    @Deployment
    public static Archive<?> getDeployment() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "AuditLoggerTestCase.war");
        archive.addClass(AuditLoggerTestCase.class);
        return archive;
    }

    @Test
    public void testJaxrs() throws Exception {
        AbstractCliTestBase.initCLI(System.getProperty("node0","127.0.0.1"));

        cli.sendLine("/core-service=management/access=audit/logger=audit-log:remove()");
        cli.sendLine("/core-service=management/access=audit/logger=audit-log:add(enabled=true, log-boot=true, log-read-only=false)");
        cli.sendLine("/core-service=management/access=audit/file-handler=file:remove()");
        cli.sendLine("/core-service=management/access=audit/syslog-handler=my-syslog-handler:remove()");
        CLIOpResult result = cli.readAllAsOpResult();
        assertThat(result, is(notNullValue()));
        assertThat(result.getFromResponse(OUTCOME).toString(), is("success"));
    }

}
