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

import javax.enterprise.inject.Alternative;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * @author Stuart Douglas
 */
@Alternative
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/weld/src/main/java","modules/testcases/jdkAll/Eap7/weld/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/weld/src/main/java","modules/testcases/jdkAll/Eap71x/weld/src/main/java","modules/testcases/jdkAll/Eap7.1.0.Beta/weld/src/main/java","modules/testcases/jdkAll/Eap70x/weld/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/weld/src/main/java","modules/testcases/jdkAll/WildflyRelease-10.1.0.Final/weld/src/main/java","modules/testcases/jdkAll/Wildfly/weld/src/main/java","modules/testcases/jdkAll/Eap64x/weld/src/main/java","modules/testcases/jdkAll/Eap64x-Proposed/weld/src/main/java","modules/testcases/jdkAll/Eap63x/weld/src/main/java","modules/testcases/jdkAll/Eap62x/weld/src/main/java","modules/testcases/jdkAll/Eap61x/weld/src/main/java"})
public class AlternativeBean extends SimpleBean {

    @Override
    public String sayHello() {
        return "Hello World";
    }
}
