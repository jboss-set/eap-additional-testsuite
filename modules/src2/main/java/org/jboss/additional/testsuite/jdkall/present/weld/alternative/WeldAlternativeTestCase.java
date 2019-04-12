/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.additional.testsuite.jdkall.present.weld.alternative;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * A test of the CDI alternatives. This tests that the alternative
 * information in the beans.xml file is being parsed correctly.
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/weld/src/main/java","modules/testcases/jdkAll/Eap72x/weld/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/weld/src/main/java","modules/testcases/jdkAll/Eap7/weld/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/weld/src/main/java","modules/testcases/jdkAll/Eap71x/weld/src/main/java","modules/testcases/jdkAll/Eap7.1.0.Beta/weld/src/main/java","modules/testcases/jdkAll/Eap70x/weld/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/weld/src/main/java","modules/testcases/jdkAll/WildflyRelease-10.1.0.Final/weld/src/main/java","modules/testcases/jdkAll/Wildfly/weld/src/main/java","modules/testcases/jdkAll/Eap64x/weld/src/main/java","modules/testcases/jdkAll/Eap64x-Proposed/weld/src/main/java","modules/testcases/jdkAll/Eap63x/weld/src/main/java","modules/testcases/jdkAll/Eap62x/weld/src/main/java","modules/testcases/jdkAll/Eap61x/weld/src/main/java"})
public class WeldAlternativeTestCase {

    @Deployment
    public static Archive<?> deploy() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class);
        jar.addPackage(WeldAlternativeTestCase.class.getPackage());
        jar.addAsManifestResource(new StringAsset("<beans><alternatives><class>" + AlternativeBean.class.getName() + "</class></alternatives></beans>"), "beans.xml");
        return jar;
    }

    @Inject
    private SimpleBean bean;

    @Test
    public void testAlternatives() {
        Assert.assertEquals("Hello World", bean.sayHello());
    }


}
