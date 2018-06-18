/*
 * JBoss, Home of Professional Open Source.
 * Copyleft 2017, Red Hat, Inc., and individual contributors
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

package org.jboss.additional.testsuite.jdkall.present.core.marshalling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InvalidObjectException;
import java.io.Serializable;
import java.util.ArrayList;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.eap.additional.testsuite.annotations.ATTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import org.jboss.marshalling.InputStreamByteInput;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.OutputStreamByteOutput;
import org.jboss.marshalling.cloner.ClonerConfiguration;
import org.jboss.marshalling.cloner.ObjectCloner;
import org.jboss.marshalling.cloner.ObjectClonerFactory;
import org.jboss.marshalling.cloner.ObjectCloners;
import org.jboss.marshalling.river.RiverMarshaller;
import org.jboss.marshalling.river.RiverMarshallerFactory;
import org.jboss.marshalling.river.RiverUnmarshaller;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

@RunWith(Arquillian.class)
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/core/src/main/java","modules/testcases/jdkAll/Wildfly/core/src/main/java","modules/testcases/jdkAll/Eap7/core/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/core/src/main/java","modules/testcases/jdkAll/Eap71x/core/src/main/java","modules/testcases/jdkAll/Eap7.1.0.Beta/core/src/main/java","modules/testcases/jdkAll/Eap70x/core/src/main/java"})
public class MarshallingTestCase {

    public static final String DEPLOYMENT = "marshallingTestCase.war";

    @Deployment
    public static Archive<?> getDeployment() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class);
        archive.addClass(Foo.class);
        archive.addClass(Bar.class);
        archive.addPackage("org.jboss.marshalling");
        archive.addPackage("org.jboss.marshalling.util");
        archive.addPackage("org.jboss.marshalling.river");
        archive.addPackage("org.jboss.marshalling.reflect");
        archive.addPackage("org.jboss.marshalling.cloner");
        return archive;
    }

    @ATTest({"modules/testcases/jdkAll/Wildfly/core/src/main/java#10.0.0.Final*11.0.0.Beta1"})
    public void deserializationTest() throws Exception {
        RiverMarshallerFactory factory = new RiverMarshallerFactory();  
	    MarshallingConfiguration configuration = new MarshallingConfiguration();  
	  
	    configuration.setVersion(2); 
	  
	    // Create a marshaller on some stream we have  
	    RiverMarshaller marshaller = (RiverMarshaller) factory.createMarshaller(configuration);  
            final ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
	    marshaller.start(new OutputStreamByteOutput(fileOutputStream));  
	  
            Bar bar = new Bar("Hello");
            Foo foo = new Foo(bar);
	    // Write lots of stuff  
	    marshaller.writeObject(foo);  
          
	    // Done  
	    marshaller.finish();  
            
            RiverUnmarshaller unmarshaller = (RiverUnmarshaller) factory.createUnmarshaller(configuration);  
            ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileOutputStream.toByteArray());
            unmarshaller.start(new InputStreamByteInput(fileInputStream));  
            
            try {
                Foo f = unmarshaller.readObject(Foo.class);
                Assert.assertEquals(f.bar.aString,"Hello");
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            
            unmarshaller.finish();
    }
    
    @Test
    public void testNoDefaultConstructor() throws Throwable {
        final SerializableWithNonSerializableChildWithNoPublicConstructor object = new SerializableWithNonSerializableChildWithNoPublicConstructor();        
        final ObjectClonerFactory clonerFactory = ObjectCloners.getCloneableObjectClonerFactory();
        final ClonerConfiguration configuration = new ClonerConfiguration();
        final ObjectCloner cloner = clonerFactory.createCloner(configuration);
        try {
        	cloner.clone(object);
        } catch(InvalidObjectException ioe) {
        	// This is expected behavior
    	} catch(Exception e) {
            Assert.assertTrue("testNoDefaultConstructor has failed.",false);
    	}         
    }  
    
    public static final class SerializableWithNonSerializableChildWithNoPublicConstructor implements Serializable {
        private static final long serialVersionUID = 1L;
        private List<Object> children = new ArrayList();
        public SerializableWithNonSerializableChildWithNoPublicConstructor() {
        	children.add(new TestNotSerializableNoDefaultConstructor("Test"));	
        }  
        
    }
 
    public static interface TestNotSerializableNoDefaultConstructorInterface {
    	public String getName();
    }
    
    public static class TestNotSerializableNoDefaultConstructor implements TestNotSerializableNoDefaultConstructorInterface {
       private String name;
       public TestNotSerializableNoDefaultConstructor(String name) {
          this.name = name;
       }
       public String getName() {
    	   return name;
       }
    }
}
