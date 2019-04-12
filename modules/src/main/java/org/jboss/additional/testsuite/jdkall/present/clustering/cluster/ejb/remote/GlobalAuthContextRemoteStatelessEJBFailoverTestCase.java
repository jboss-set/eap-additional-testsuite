/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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

package org.jboss.additional.testsuite.jdkall.present.clustering.cluster.ejb.remote;

import java.util.function.UnaryOperator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.wildfly.security.auth.client.AuthenticationContext;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * Validates failover behavior of a remotely accessed secure @Stateless EJB using a global authentication context.
 * @author Farah Juma
 * @author Paul Ferraro
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/clustering/src/main/java","modules/testcases/jdkAll/Wildfly/clustering/src/main/java","modules/testcases/jdkAll/Eap72x/clustering/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/clustering/src/main/java","modules/testcases/jdkAll/Eap7/clustering/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/clustering/src/main/java","modules/testcases/jdkAll/Eap71x/clustering/src/main/java"})
public class GlobalAuthContextRemoteStatelessEJBFailoverTestCase extends AuthContextRemoteStatelessEJBFailoverTestCase {
    private static AuthenticationContext previousContext;

    @BeforeClass
    public static void before() {
        previousContext = AuthenticationContext.captureCurrent();
        AuthenticationContext.getContextManager().setGlobalDefault(AUTHENTICATION_CONTEXT);
    }

    @AfterClass
    public static void after() {
        AuthenticationContext.getContextManager().setGlobalDefault(previousContext);
    }

    public GlobalAuthContextRemoteStatelessEJBFailoverTestCase() {
        super(UnaryOperator.identity());
    }
}
