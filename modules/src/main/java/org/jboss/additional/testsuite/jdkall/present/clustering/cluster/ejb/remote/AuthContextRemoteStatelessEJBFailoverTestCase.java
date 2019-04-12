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

import java.util.PropertyPermission;
import java.util.concurrent.Callable;
import java.util.function.UnaryOperator;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.additional.testsuite.jdkall.present.clustering.cluster.ejb.remote.bean.Incrementor;
import org.jboss.additional.testsuite.jdkall.present.clustering.cluster.ejb.remote.bean.IncrementorBean;
import org.jboss.additional.testsuite.jdkall.present.clustering.cluster.ejb.remote.bean.Result;
import org.jboss.additional.testsuite.jdkall.present.clustering.cluster.ejb.remote.bean.SecureStatelessIncrementorBean;
import org.jboss.additional.testsuite.jdkall.present.clustering.cluster.ejb.EJBDirectory;
import org.jboss.as.test.shared.integration.ejb.security.PermissionUtils;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.wildfly.security.auth.client.AuthenticationConfiguration;
import org.wildfly.security.auth.client.AuthenticationContext;
import org.wildfly.security.auth.client.MatchRule;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * Validates failover behavior of a remotely accessed secure @Stateless EJB.
 * @author Paul Ferraro
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/clustering/src/main/java","modules/testcases/jdkAll/Wildfly/clustering/src/main/java","modules/testcases/jdkAll/Eap72x/clustering/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/clustering/src/main/java","modules/testcases/jdkAll/Eap7/clustering/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/clustering/src/main/java","modules/testcases/jdkAll/Eap71x/clustering/src/main/java"})
public abstract class AuthContextRemoteStatelessEJBFailoverTestCase extends AbstractRemoteStatelessEJBFailoverTestCase {
    private static final String MODULE_NAME = "secure-remote-stateless-ejb-failover-test";

    static final AuthenticationContext AUTHENTICATION_CONTEXT = AuthenticationContext.captureCurrent().with(
            MatchRule.ALL.matchAbstractType("ejb", "jboss"),
            AuthenticationConfiguration.empty().useName("user1").usePassword("password1")
        );

    @Deployment(name = DEPLOYMENT_1, managed = false, testable = false)
    @TargetsContainer(CONTAINER_1)
    public static Archive<?> createDeploymentForContainer1() {
        return createDeployment();
    }

    @Deployment(name = DEPLOYMENT_2, managed = false, testable = false)
    @TargetsContainer(CONTAINER_2)
    public static Archive<?> createDeploymentForContainer2() {
        return createDeployment();
    }

    private static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, MODULE_NAME + ".jar")
                .addPackage(EJBDirectory.class.getPackage())
                .addClasses(Result.class, Incrementor.class, IncrementorBean.class, SecureStatelessIncrementorBean.class)
                .addAsManifestResource(PermissionUtils.createPermissionsXmlAsset(new PropertyPermission(NODE_NAME_PROPERTY, "read")), "permissions.xml")
                ;
    }

    public AuthContextRemoteStatelessEJBFailoverTestCase(UnaryOperator<Callable<Void>> configurator) {
        super(MODULE_NAME, SecureStatelessIncrementorBean.class, configurator);
    }
}

