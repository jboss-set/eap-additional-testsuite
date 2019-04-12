package org.jboss.additional.testsuite.jdkall.present.jaxrs.client.resource;

import javax.ws.rs.core.Response;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/jaxrs/src/main/java","modules/testcases/jdkAll/Eap7/jaxrs/src/main/java","modules/testcases/jdkAll/Eap72x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/jaxrs/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/jaxrs/src/main/java","modules/testcases/jdkAll/Eap71x/jaxrs/src/main/java"})
public class ApacheHttpClient43ResourceImpl implements ApacheHttpClient43Resource {
    public Response get() {
    /*    try {
            Thread.sleep(7000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }*/
        return Response.ok().build();
    }
}
