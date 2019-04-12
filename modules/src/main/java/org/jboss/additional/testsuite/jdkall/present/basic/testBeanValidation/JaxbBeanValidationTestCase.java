/*
 * JBoss, Home of Professional Open Source
 * Copyright 2018, Red Hat Inc., and individual contributors as indicated
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
package org.jboss.additional.testsuite.jdkall.present.basic.testBeanValidation;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.jboss.additional.testsuite.jdkall.present.basic.beanvalidation.BeanValidationApplication;
import org.jboss.additional.testsuite.jdkall.present.basic.beanvalidation.GreetingService;
import org.jboss.additional.testsuite.jdkall.present.basic.beanvalidation.BeanValidationTestResource;
import org.jboss.additional.testsuite.jdkall.present.basic.beanvalidation.custom.MyCustomConstraint;
import org.jboss.additional.testsuite.jdkall.present.basic.beanvalidation.custom.MyOtherBean;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.common.HttpRequest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;


@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/basic/src/main/java","modules/testcases/jdkAll/Eap72x/basic/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/basic/src/main/java"})
public class JaxbBeanValidationTestCase {

    @Deployment(testable = false)
    public static Archive<?> deploy() {
        WebArchive war = ShrinkWrap.create(WebArchive.class,"basicbeanvalidation.war");
        war.addPackage(HttpRequest.class.getPackage());
        war.addClasses(JaxbBeanValidationTestCase.class, BeanValidationTestResource.class, BeanValidationApplication.class, GreetingService.class, MyCustomConstraint.class, MyOtherBean.class);
        return war;
    }

    @ArquillianResource
    private URL url;

    private String performCall(String urlPattern) throws Exception {
        return HttpRequest.get(url + urlPattern, 10, TimeUnit.SECONDS);
    }

    @Test
    public void testBeanValidationBasicFeatures() throws Exception {
        String result = performCall("bean-validation/test/basic-features");

        try{
            StringBuilder expected = new StringBuilder();
            expected.append("failed: additionalEmails[0].<list element> (must be a well-formed email address)").append(", ")
                .append("categorizedEmails<K>[a].<map key> (length must be between 3 and 2147483647)").append(", ")
                .append("categorizedEmails[a].<map value>[0].<list element> (must be a well-formed email address)").append(", ")
                .append("email (must be a well-formed email address)").append(", ")
                .append("score (must be greater than or equal to 0)").append("\n");
            expected.append("passed");

            Assert.assertTrue("Response of basic-features bean validation was not expected.", result.equals(expected.toString()));
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void testBeanValidationLevelConstraint() throws Exception {
        String result = performCall("bean-validation/test/custom-class-level-constraint");

        try{
            StringBuilder expected = new StringBuilder();
            expected.append("failed:  ({MyCustomConstraint.message})").append("\n");
            expected.append("passed");

            Assert.assertTrue("Response of custom-class-level-constraint bean validation was not expected.", result.equals(expected.toString()));
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void testBeanValidationCdiBeanMethod() throws Exception {
        String result = performCall("bean-validation/test/cdi-bean-method-validation");

        try{
            StringBuilder expected = new StringBuilder();
            expected.append("passed").append("\n");
            expected.append("failed: greeting.arg0 (must not be null)");

            Assert.assertTrue("Response of cdi-bean-method-validation bean validation was not expected.", result.equals(expected.toString()));
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void testBeanValidationRestEndPointValidation() throws Exception {
        String result = null;

        try {
             result = performCall("bean-validation/test/rest-end-point-validation/plop");
        }catch(Exception e) {
             Assert.assertTrue("Response of rest-end-point-validation bean validation was not expected.", e.getMessage().contains("numeric value out of bounds"));
        }
        
        result = performCall("bean-validation/test/rest-end-point-validation/42");
        Assert.assertTrue("Response of rest-end-point-validation bean validation was not expected.", result.contains("42"));

    }

}
