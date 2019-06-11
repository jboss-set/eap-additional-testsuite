/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2019, Red Hat, Inc., and individual contributors
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

package org.jboss.additional.testsuite.jdkall.present.ejb.stateful.passivation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.InitialContext;

/**
 * Test configures two passivation stores per deployment and calls the passivation bean. The call should succeed.
 * Test for [ JBEAP-16144 ].
 *
 * @author Daniel Cihak
 */
@RunWith(Arquillian.class)
@ServerSetup(TwoPassivationStoresServerSetupTask.class)
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java#16.0.0.Beta1","modules/testcases/jdkAll/WildflyRelease-17.0.0.Final/ejb/src/main/java", "modules/testcases/jdkAll/Eap72x/ejb/src/main/java#7.2.1", "modules/testcases/jdkAll/Eap72x-Proposed/ejb/main/java#7.2.1"})
public class TwoPassivationStoresTestCase {

    private static final String DEPLOYMENT = "DEPLOYMENT";

    private static final String persistence_xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<persistence xmlns=\"http://java.sun.com/xml/ns/persistence\" version=\"1.0\">\n" +
                    "    <persistence-unit name=\"main\">\n" +
                    "        <jta-data-source>java:jboss/datasources/ExampleDS</jta-data-source>\n" +
                    "        <properties>\n" +
                    "            <property name=\"hibernate.hbm2ddl.auto\" value=\"create-drop\"/>\n" +
                    "        </properties>\n" +
                    "    </persistence-unit>\n" +
                    "</persistence>\n";

    @ArquillianResource
    private InitialContext ctx;

    @Deployment
    public static Archive<?> deploy() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, DEPLOYMENT + ".jar");
        jar.addClasses(DifferentCachePassivationBean.class, TwoPassivationStoresServerSetupTask.class, PassivationEnabledBean.class, Bean.class);
        jar.addAsManifestResource(new StringAsset("Dependencies: org.jboss.as.controller-client, org.jboss.dmr \n"), "MANIFEST.MF");
        jar.addAsResource(new StringAsset(persistence_xml), "META-INF/persistence.xml");
        return jar;
    }

    @Test
    public void testTwoPassivationStores() throws Exception {

        try (Bean bean = (Bean) ctx.lookup("java:module/" + PassivationEnabledBean.class.getSimpleName() + "!" + Bean.class.getName())) {
            bean.doNothing();

            try (Bean differentCacheBean1 = (Bean) ctx.lookup("java:module/" + DifferentCachePassivationBean.class.getSimpleName() + "!" + Bean.class.getName())) {
                differentCacheBean1.doNothing();

                // Create a 2nd set of beans, forcing the first set to passivate
                try (Bean bean2 = (Bean) ctx.lookup("java:module/" + PassivationEnabledBean.class.getSimpleName() + "!" + Bean.class.getName())) {
                    bean2.doNothing();

                    try (Bean differentCacheBean2 = (Bean) ctx.lookup("java:module/" + DifferentCachePassivationBean.class.getSimpleName() + "!" + Bean.class.getName())) {
                        differentCacheBean2.doNothing();

                        Assert.assertTrue("(Annotation based) Stateful bean marked as passivation enabled was not passivated", bean.wasPassivated());
                        Assert.assertTrue("(Annotation based) Stateful bean marked as passivation enabled was not activated", bean.wasActivated());

                        Assert.assertTrue("(Deployment descriptor based) Stateful bean marked as passivation enabled was not passivated", differentCacheBean1.wasPassivated());
                        Assert.assertTrue("(Deployment descriptor based) Stateful bean marked as passivation enabled was not activated", differentCacheBean1.wasActivated());
                    }
                }
            }
        }
    }
}
