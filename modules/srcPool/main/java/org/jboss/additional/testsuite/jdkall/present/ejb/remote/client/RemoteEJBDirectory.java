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

package org.jboss.additional.testsuite.jdkall.present.ejb.remote.client;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;

import org.jboss.ejb.client.Affinity;
import org.jboss.ejb.client.ClusterAffinity;
import org.jboss.ejb.client.EJBClient;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * @author Paul Ferraro
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/ejb/src/main/java","modules/testcases/jdkAll/Wildfly/ejb/src/main/java","modules/testcases/jdkAll/Eap7/ejb/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap71x/ejb/src/main/java"})
public class RemoteEJBDirectory extends AbstractEJBDirectory {

    private static final String TX_CONTEXT_NAME = "txn:UserTransaction";

    private static Properties createEnvironment() {
        Properties env = new Properties();
        env.setProperty(Context.INITIAL_CONTEXT_FACTORY, org.wildfly.naming.client.WildFlyInitialContextFactory.class.getName());
        env.setProperty(Context.PROVIDER_URL,"remote+http://localhost:8080");
        // TODO UserTransaction lookup currently requires environment to be configured with provider URLs.
        // env.setProperty(Context.PROVIDER_URL, String.join(",", EJBClientContext.getCurrent().getConfiguredConnections().stream().map(EJBClientConnection::getDestination).map(URI::toString).collect(Collectors.toList())));
        return env;
    }

    private final String module;

    public RemoteEJBDirectory(String module) throws NamingException {
        super(TX_CONTEXT_NAME, createEnvironment());
        this.module = module;
    }

    @Override
    protected <T> String createJndiName(String beanName, Class<T> beanInterface, Type type) {
        return String.format("ejb:/%s/%s!%s%s", this.module, beanName, beanInterface.getName(), (type == Type.STATEFUL) ? "?stateful" : "");
    }

    @Override
    protected <T> T lookup(String beanName, Class<T> beanInterface, Type type) throws NamingException {
        T bean = super.lookup(beanName, beanInterface, type);
        Affinity affinity = new ClusterAffinity("ejb");
        switch (type) {
            case STATEFUL: {
                EJBClient.setStrongAffinity(bean, affinity);
                break;
            }
            case STATELESS: {
                EJBClient.setWeakAffinity(bean, affinity);
                break;
            }
            default: {
                // No need to set initial affinity
            }
        }
        return bean;
    }
}
