package org.jboss.additional.testsuite.jdkall.present.jaxrs.client.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@Path("/test")
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap7/jaxrs/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/jaxrs/src/main/java","modules/testcases/jdkAll/Eap71x/jaxrs/src/main/java"})
public interface ApacheHttpClient4Resource {
    @GET
    @Produces("text/plain")
    String get();

    @GET
    @Path("error")
    @Produces("text/plain")
    String error();

    @POST
    @Path("data")
    @Produces("text/plain")
    @Consumes("text/plain")
    String getData(String data);
}
