package org.jboss.additional.testsuite.jdkall.present.jaxrs.client.resource;

import org.jboss.resteasy.spi.NoLogWebApplicationException;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap7/jaxrs/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/jaxrs/src/main/java","modules/testcases/jdkAll/Eap71x/jaxrs/src/main/java"})
public class ApacheHttpClient4ResourceImpl implements ApacheHttpClient4Resource {
    public String get() {
    /*    try {
            Thread.sleep(7000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }*/
        return "hello world";
    }

    public String error() {
    /*    try {
            Thread.sleep(7000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }*/
        throw new NoLogWebApplicationException(HttpResponseCodes.SC_NOT_FOUND);
    }

    public String getData(String data) {
    /*    try {
            Thread.sleep(7000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }*/
        return "Here is your string:" + data;
    }
}
