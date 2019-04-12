/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
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

package org.jboss.additional.testsuite.jdkall.present.basic.ws;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

import javax.jws.HandlerChain;
import javax.jws.WebService;

// TODO: Enable once https://issues.jboss.org/browse/WFLY-11204 gets resolved
//@apAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/basic/src/main/java#15.0.0","modules/testcases/jdkAll/Eap72x-Proposed/basic/src/main/java#7.2.1","modules/testcases/jdkAll/Eap72x/basic/src/main/java#7.2.1","modules/testcases/jdkAll/Eap71x-Proposed/basic/src/main/java#7.1.6","modules/testcases/jdkAll/Eap71x/basic/src/main/java#7.1.6"})
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap72x-Proposed/basic/src/main/java#7.2.1","modules/testcases/jdkAll/Eap72x/basic/src/main/java#7.2.1","modules/testcases/jdkAll/Eap71x-Proposed/basic/src/main/java#7.1.6","modules/testcases/jdkAll/Eap71x/basic/src/main/java#7.1.6"})
@WebService(
    serviceName = ContainsKeyServiceImpl.SERVICE_NAME,
    portName = "ContainsKey",
    name = "ContainsKey",
    endpointInterface = "org.jboss.additional.testsuite.jdkall.present.basic.ws.ContainsKeyService",
    targetNamespace = ContainsKeyServiceImpl.NAMESPACE)
@HandlerChain(file = "/handlers.xml")
public class ContainsKeyServiceImpl implements ContainsKeyService {

    public static final String SERVICE_NAME = "ContainsKeyService";
    public static final String NAMESPACE = "http://www.jboss.org/eap/additional/ts/ContainsKey";

    @Override
    public boolean testContainsKey() {
        return Handler.containsKey;
    }
}
