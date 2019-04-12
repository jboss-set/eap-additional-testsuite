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

package org.jboss.additional.testsuite.jdkall.past.eap_7.ejb.async;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jboss.ejb.client.ContextSelector;
import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * @author Paul Ferraro
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap70x/ejb/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/WildflyRelease-10.1.0.Final/ejb/src/main/java","modules/testcases/jdkAll/Eap64x/ejb/src/main/java","modules/testcases/jdkAll/Eap64x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap63x/ejb/src/main/java","modules/testcases/jdkAll/Eap62x/ejb/src/main/java","modules/testcases/jdkAll/Eap61x/ejb/src/main/java"})
public class EJBClientContextSelector {
    public static ContextSelector<EJBClientContext> setup(String file) throws IOException {
        return setup(file, null);
    }

    public static ContextSelector<EJBClientContext> setup(String file, Properties propertiesToReplace) throws IOException {
        // setUp the selector
        final InputStream inputStream = EJBClientContextSelector.class.getClassLoader().getResourceAsStream(file);
        if (inputStream == null) {
            throw new IllegalStateException("Could not find " + file + " in classpath");
        }
        final Properties properties = new Properties();
        properties.load(inputStream);

        // add or replace properties passed from file
        if (propertiesToReplace != null) {
            for (Object key: propertiesToReplace.keySet()) {
                properties.put(key, propertiesToReplace.get(key));
            }
        }

        final EJBClientConfiguration ejbClientConfiguration = new PropertiesBasedEJBClientConfiguration(properties);
        final ConfigBasedEJBClientContextSelector selector = new ConfigBasedEJBClientContextSelector(ejbClientConfiguration);

        return EJBClientContext.setSelector(selector);
    }
}
