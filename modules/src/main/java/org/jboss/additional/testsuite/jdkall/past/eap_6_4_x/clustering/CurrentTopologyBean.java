/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.additional.testsuite.jdkall.past.eap_6_4_x.clustering;

import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.transport.Address;
import org.jboss.as.clustering.msc.ServiceContainerHelper;
import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.msc.service.ServiceContainer;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistry;
import org.jboss.msc.service.StartException;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.security.AccessController;
import java.util.Set;
import java.util.TreeSet;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * @author Tomas Hofman (thofman@redhat.com)
 */
@Stateless
@Remote(CurrentTopology.class)
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap64x/clustering/src/main/java","modules/testcases/jdkAll/Eap64x-Proposed/clustering/src/main/java","modules/testcases/jdkAll/Eap63x/clustering/src/main/java","modules/testcases/jdkAll/Eap62x/clustering/src/main/java"})
public class CurrentTopologyBean implements CurrentTopology {

    @Override
    public Set<String> getClusterMembers(String cluster) {
        try {
            ServiceRegistry registry = currentServiceContainer();
            ServiceController<?> controller = registry.getService(ServiceName.JBOSS.append("infinispan", cluster));
            if (controller == null) {
                throw new IllegalStateException(String.format("Failed to locate service for cluster '%s'", cluster));
            }
            EmbeddedCacheManager manager = ServiceContainerHelper.getValue(controller, EmbeddedCacheManager.class);
            return this.getMembers(manager);
        } catch (StartException e) {
            throw new IllegalStateException(e);
        }
    }

    private Set<String> getMembers(EmbeddedCacheManager manager) {
        Set<String> members = new TreeSet<String>();
        for (Address address: manager.getMembers()) {
            members.add(address.toString());
        }
        return members;
    }

    private static ServiceContainer currentServiceContainer() {
        if(System.getSecurityManager() == null) {
            return CurrentServiceContainer.getServiceContainer();
        }
        return AccessController.doPrivileged(CurrentServiceContainer.GET_ACTION);
    }

}
