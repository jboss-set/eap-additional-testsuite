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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mchange.v2.c3p0.jacksonTest.ComboPooledDataSource;
import org.springframework.jacksontest.BogusApplicationContext;
import org.springframework.jacksontest.BogusPointcutAdvisor;
import java.io.IOException;
import java.rmi.RemoteException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.ibatis.datasource.jndi.JndiDataSourceFactory;
import org.hibernate.jmx.StatisticsService;
import org.apache.openjpa.ee.JNDIManagedRuntime;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@Path("jaxb")
@Produces({"application/json"})
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/jaxrs/src/main/java","modules/testcases/jdkAll/Wildfly/jaxrs/src/main/java","modules/testcases/jdkAll/WildflyRelease-17.0.0.Alpha1/jaxrs/src/main/java","modules/testcases/jdkAll/Eap72x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/jaxrs/src/main/java","modules/testcases/jdkAll/Eap7/jaxrs/src/main/java","modules/testcases/jdkAll/Eap71x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/jaxrs/src/main/java"})
public class JaxbResourceDeserializationSecurityCheck {
    @GET
    @Path("advisor")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdvisor() throws RemoteException, IOException {
        BogusPointcutAdvisor obj = new BogusPointcutAdvisor();

        return Response.ok().entity(obj).build();
    }
    
    @GET
    @Path("mchange")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComboPooledDataSource() throws RemoteException, IOException {
        ComboPooledDataSource obj = new ComboPooledDataSource();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // do various things, perhaps:
        String objJsonString = mapper.writeValueAsString(obj);

        return Response.ok().entity(objJsonString).build();
    }


    @GET
    @Path("appcontext")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAppContext() throws RemoteException, IOException {
        BogusApplicationContext obj = new BogusApplicationContext();

        return Response.ok().entity(obj).build();
    }

    @GET
    @Path("statistics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatisticsService() throws RemoteException, IOException {
        StatisticsService obj = new StatisticsService();

        return Response.ok().entity(obj).build();
    }

    @GET
    @Path("datasource")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDatasource() throws RemoteException, IOException {
        JndiDataSourceFactory obj = new JndiDataSourceFactory();

        return Response.ok().entity(obj).build();
    }

    @GET
    @Path("openjpa")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJNDIManagedRuntimeService() throws RemoteException, IOException {
        JNDIManagedRuntime obj = new JNDIManagedRuntime();

        return Response.ok().entity(obj).build();
    }

}
