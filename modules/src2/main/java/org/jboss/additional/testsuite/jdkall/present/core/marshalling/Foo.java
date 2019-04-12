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

package org.jboss.additional.testsuite.jdkall.present.core.marshalling;

import java.io.Serializable;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/core/src/main/java","modules/testcases/jdkAll/Wildfly/core/src/main/java","modules/testcases/jdkAll/Eap72x/core/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/core/src/main/java","modules/testcases/jdkAll/Eap7/core/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/core/src/main/java","modules/testcases/jdkAll/Eap71x/core/src/main/java","modules/testcases/jdkAll/Eap7.1.0.Beta/core/src/main/java","modules/testcases/jdkAll/Eap70x/core/src/main/java"})
public class Foo implements Serializable {
    private static final long serialVersionUID = 1L;
    public String aString = "foo";
    public Bar bar;
    
    Foo(Bar bar) {
        this.bar = bar;
    }
}

