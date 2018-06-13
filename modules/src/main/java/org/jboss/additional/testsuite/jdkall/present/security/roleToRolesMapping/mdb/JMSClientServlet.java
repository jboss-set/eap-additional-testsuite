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

import java.io.IOException;
import java.io.PrintWriter;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * @author <a href="mailto:psotirop@redhat.com">Panos</a>
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/security/src/main/java","modules/testcases/jdkAll/Eap7/security/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/security/src/main/java","modules/testcases/jdkAll/Eap71x/security/src/main/java","modules/testcases/jdkAll/Eap7.1.0.Beta/security/src/main/java","modules/testcases/jdkAll/Eap70x/security/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/security/src/main/java","modules/testcases/jdkAll/WildflyRelease-10.1.0.Final/security/src/main/java","modules/testcases/jdkAll/Wildfly/security/src/main/java","modules/testcases/jdkAll/Wildfly-Unmerged/security/src/main/java","modules/testcases/jdkAll/Eap64x/security/src/main/java","modules/testcases/jdkAll/Eap64x-Proposed/security/src/main/java"})
@WebServlet(name = "JMSClientServlet", urlPatterns = {"/JMSClientServlet"})
@ServletSecurity(@HttpConstraint(rolesAllowed = "Support" ))
public class JMSClientServlet extends HttpServlet {
    
    /** The String returned in the HTTP response body. */
    public static final String RESPONSE_BODY = "GOOD";
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String destinationName = "/jms/queue/sampleQueue";
        PrintWriter out = response.getWriter();
        Context ic = null;
        ConnectionFactory cf = null;
        Connection connection = null;

        try {
            ic = new InitialContext();

            cf = (ConnectionFactory) ic.lookup("/ConnectionFactory");
            Queue queue = (Queue) ic.lookup(destinationName);

            connection = cf.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer publisher = session.createProducer(queue);

            connection.start();

            TextMessage message = session.createTextMessage("Hello AS 7 !");
            publisher.send(message);

            out.write(RESPONSE_BODY);
       //     out.println("Message sent to the JMS Provider");

        } catch (Exception exc) {
            response.addHeader("OUTCOME", "ERROR");
            exc.printStackTrace();
        } finally {

            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
