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

package org.jboss.additional.testsuite.jdkall.present.web.classloading.ear;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/web/src/main/java#15.0.0","modules/testcases/jdkAll/WildflyRelease-17.0.0.Final/web/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/web/src/main/java#7.2.0.CR1","modules/testcases/jdkAll/Eap72x/web/src/main/java#7.2.0.CR1","modules/testcases/jdkAll/Eap71x-Proposed/web/src/main/java#7.1.5","modules/testcases/jdkAll/Eap71x/web/src/main/java#7.1.5","modules/testcases/jdkAll/Eap7/web/src/main/java"})
public class GreeterServiceClient extends Service {

    private final static URL WSDL_LOCATION = GreeterServiceClient.class.getResource("/META-INF/GreeterService.wsdl");
    private final static QName SERVICE_NAME =
        new QName("http://www.jboss.org/eap/additional/ts/Greeter", "GreeterService");

    private GreeterService greeterService;

    public GreeterServiceClient() throws MalformedURLException {
        super(WSDL_LOCATION, SERVICE_NAME);
        greeterService = super.getPort(GreeterService.class);
    }

    public String sayHello() {
        return greeterService.sayHello();
    }
}
