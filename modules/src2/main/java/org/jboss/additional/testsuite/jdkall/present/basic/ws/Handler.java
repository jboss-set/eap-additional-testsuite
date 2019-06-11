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

import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;


@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/basic/src/main/java#16.0.0.Final","modules/testcases/jdkAll/Eap7/basic/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/basic/src/main/java#7.2.1","modules/testcases/jdkAll/Eap72x/basic/src/main/java#7.2.1","modules/testcases/jdkAll/Eap71x-Proposed/basic/src/main/java#7.1.6","modules/testcases/jdkAll/Eap71x/basic/src/main/java#7.1.6"})
public class Handler implements LogicalHandler<LogicalMessageContext> {

    public static boolean containsKey = false;

    @Override
    public boolean handleMessage(LogicalMessageContext context) {
        boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (!outbound) {
            containsKey = context.containsKey(MessageContext.HTTP_REQUEST_HEADERS);
        }
        return true;
    }

    @Override
    public boolean handleFault(LogicalMessageContext context) {
        return false;
    }

    @Override
    public void close(MessageContext context) {

    }
}
