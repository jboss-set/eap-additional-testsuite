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

package org.jboss.additional.testsuite.jdkall.present.clustering.cluster.ejb.xpc.bean;

import javax.ejb.Remove;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * Interface to see if that helps avoid the
 * error:
 * <p/>
 * WFLYEJB0034: EJB Invocation failed on component StatefulBean for method
 * public org.jboss.as.test.clustering.unmanaged.ejb3.xpc.bean.Employee
 * org.jboss.as.test.clustering.unmanaged.ejb3.xpc.bean.StatefulBean.getEmployee(int):
 * java.lang.IllegalArgumentException: object is not an instance of declaring class
 *
 * @author Scott Marlow
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/clustering/src/main/java","modules/testcases/jdkAll/Wildfly/clustering/src/main/java","modules/testcases/jdkAll/WildflyRelease-17.0.0.Alpha1/clustering/src/main/java","modules/testcases/jdkAll/Eap72x/clustering/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/clustering/src/main/java","modules/testcases/jdkAll/Eap7/clustering/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/clustering/src/main/java","modules/testcases/jdkAll/Eap71x/clustering/src/main/java"})
public interface Stateful {
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    void createEmployee(String name, String address, int id);

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    Employee getEmployee(int id);

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    Employee getSecondBeanEmployee(int id);

    @Remove
    void destroy();

    void flush();

    void clear();

    void deleteEmployee(int id);

    void echo(String message);

    int executeNativeSQL(String nativeSql);

    String getVersion();

    long getEmployeesInMemory();
}
