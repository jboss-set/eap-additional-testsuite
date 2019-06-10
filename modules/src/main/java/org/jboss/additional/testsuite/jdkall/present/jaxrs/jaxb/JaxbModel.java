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
package org.jboss.additional.testsuite.jdkall.present.jaxrs.jaxb;

import javax.xml.bind.annotation.XmlRootElement;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * @author Stuart Douglas
 */
@XmlRootElement
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/jaxrs/src/main/java","modules/testcases/jdkAll/Wildfly/jaxrs/src/main/java","modules/testcases/jdkAll/WildflyRelease-17.0.0.Final/jaxrs/src/main/java","modules/testcases/jdkAll/Eap72x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/jaxrs/src/main/java","modules/testcases/jdkAll/Eap7/jaxrs/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/jaxrs/src/main/java","modules/testcases/jdkAll/Eap71x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap7.1.0.Beta/jaxrs/src/main/java","modules/testcases/jdkAll/Eap70x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/jaxrs/src/main/java","modules/testcases/jdkAll/WildflyRelease-10.1.0.Final/jaxrs/src/main/java","modules/testcases/jdkAll/Eap64x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap64x-Proposed/jaxrs/src/main/java","modules/testcases/jdkAll/Eap63x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap62x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap61x/jaxrs/src/main/java"})
public class JaxbModel {

    private String first;
    private String last;

    public JaxbModel(String first, String last) {
        this.first = first;
        this.last = last;
    }

    public JaxbModel() {

    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public void setLast(String last) {
        this.last = last;
    }
}
