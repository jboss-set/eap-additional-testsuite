/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017, Red Hat Inc., and individual contributors as indicated
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

import java.io.IOException;
import java.rmi.RemoteException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.aries.transaction.jms.internal.XaPooledConnectionFactory;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@Path("jaxb")
@Produces({"application/json"})
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/jaxrs/src/main/java","modules/testcases/jdkAll/Eap72x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/jaxrs/src/main/java","modules/testcases/jdkAll/Eap7/jaxrs/src/main/java"})
public class JaxbResourceDeserializationSecurityCheck7 {
    @GET
    @Path("xapooledconnectionfactory")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDatasource() throws RemoteException, IOException {
        XaPooledConnectionFactory obj = new XaPooledConnectionFactory();

        return Response.ok().entity(obj).build();
    }

}
