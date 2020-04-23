package org.jboss.additional.testsuite.jdkall.present.jaxrs.jaxb;

import com.fasterxml.jackson.databind.JsonMappingException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.common.HttpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.apache.aries.transaction.jms.internal.XaPooledConnectionFactory;


@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/jaxrs/src/main/java#20.0.0.Beta1","modules/testcases/jdkAll/Eap72x/jaxrs/src/main/java#7.2.9","modules/testcases/jdkAll/Eap72x-Proposed/jaxrs/src/main/java#7.2.9","modules/testcases/jdkAll/Eap7/jaxrs/src/main/java#7.3.2"})
public class JaxbProviderDeserializationSecurityCheck7TestCase {

    @Deployment(testable = false)
    public static Archive<?> deploy() {
        WebArchive war = ShrinkWrap.create(WebArchive.class,"jaxrssecurity7.war");
        war.addPackage(HttpRequest.class.getPackage());
        war.addClasses(JaxbProviderDeserializationSecurityCheck7TestCase.class, JaxbResourceDeserializationSecurityCheck7.class,ExampleApplication.class,
                       org.apache.aries.transaction.jms.internal.XaPooledConnectionFactory.class);
        return war;
    }

    @ArquillianResource
    private URL url;

    private String performCall(String urlPattern) throws Exception {
        return HttpRequest.get(url + urlPattern, 10, TimeUnit.SECONDS);
    }

    @Test
    public void testXaPooledConnectionFactoryService() throws Exception{
        String result = performCall("rest/jaxb/xapooledconnectionfactory");

        try{
            XaPooledConnectionFactory jaxbModel = new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).readValue(result, XaPooledConnectionFactory.class);
            Assert.fail("Should prevente json deserialization because of security reasons.");
        }catch(JsonMappingException e){
            Assert.assertTrue("Should prevente json deserialization because of security reasons.", e.getMessage().contains("Illegal type"));
        }
    }

}
