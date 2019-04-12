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

package org.jboss.additional.testsuite.jdkall.present.clustering.ejb;

import javax.ejb.EJBHome;
import javax.ejb.SessionBean;
import javax.naming.NamingException;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * EJB lookup helper
 *
 * @author Paul Ferraro
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/clustering/src/main/java","modules/testcases/jdkAll/Wildfly/clustering/src/main/java","modules/testcases/jdkAll/WildflyRelease-10.1.0.Final/clustering/src/main/java","modules/testcases/jdkAll/Eap72x/clustering/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/clustering/src/main/java","modules/testcases/jdkAll/Eap7/clustering/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/clustering/src/main/java","modules/testcases/jdkAll/Eap71x/clustering/src/main/java","modules/testcases/jdkAll/Eap7.1.0.Beta/clustering/src/main/java","modules/testcases/jdkAll/Eap70x/clustering/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/clustering/src/main/java"})
public interface EJBDirectory extends AutoCloseable {
    <T> T lookupStateful(String beanName, Class<T> beanInterface) throws NamingException;

    <T> T lookupStateful(Class<? extends T> beanClass, Class<T> beanInterface) throws NamingException;

    <T> T lookupStateless(String beanName, Class<T> beanInterface) throws NamingException;

    <T> T lookupStateless(Class<? extends T> beanClass, Class<T> beanInterface) throws NamingException;

    <T> T lookupSingleton(String beanName, Class<T> beanInterface) throws NamingException;

    <T> T lookupSingleton(Class<? extends T> beanClass, Class<T> beanInterface) throws NamingException;

    <T extends EJBHome> T lookupHome(String beanName, Class<T> homeInterface) throws NamingException;

    <T extends EJBHome> T lookupHome(Class<? extends SessionBean> beanClass, Class<T> homeInterface) throws NamingException;

    @Override
    void close() throws NamingException;
}
