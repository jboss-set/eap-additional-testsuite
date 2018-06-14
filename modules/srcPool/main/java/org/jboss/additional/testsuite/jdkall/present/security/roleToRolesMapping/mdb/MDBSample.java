/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat Inc., and individual contributors as indicated
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
package org.jboss.additional.testsuite.jdkall.present.security.roleToRolesMapping.mdb;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.security.annotation.SecurityDomain;
 
@MessageDriven(name = "MessageMDBSample", activationConfig = {
@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
@ActivationConfigProperty(propertyName = "destination", propertyValue = "/jms/queue/sampleQueue"),
@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/security/src/main/java","modules/testcases/jdkAll/Eap7/security/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/security/src/main/java","modules/testcases/jdkAll/Eap71x/security/src/main/java","modules/testcases/jdkAll/Eap7.1.0.Beta/security/src/main/java","modules/testcases/jdkAll/Eap70x/security/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/security/src/main/java","modules/testcases/jdkAll/WildflyRelease-10.1.0.Final/security/src/main/java","modules/testcases/jdkAll/Wildfly/security/src/main/java","modules/testcases/jdkAll/Wildfly-Unmerged/security/src/main/java","modules/testcases/jdkAll/Eap64x/security/src/main/java","modules/testcases/jdkAll/Eap64x-Proposed/security/src/main/java"})
public class MDBSample implements MessageListener {

    public void onMessage(Message message) {
 
       TextMessage tm = (TextMessage) message;
       try {
           System.out.println("Received message : " + tm.getText());
       } catch (JMSException e) {
           e.printStackTrace();
       }
  
    }
 
}
