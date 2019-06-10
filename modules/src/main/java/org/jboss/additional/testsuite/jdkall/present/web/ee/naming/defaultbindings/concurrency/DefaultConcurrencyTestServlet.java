/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2016, Red Hat, Inc., and individual contributors
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

package org.jboss.additional.testsuite.jdkall.present.web.ee.naming.defaultbindings.concurrency;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ContextService;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * @author Eduardo Martins
 */
@WebServlet(name = "SimpleServlet", urlPatterns = {"/simple"})
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/web/src/main/java","modules/testcases/jdkAll/Wildfly/web/src/main/java","modules/testcases/jdkAll/WildflyRelease-17.0.0.Final/web/src/main/java","modules/testcases/jdkAll/Eap72x/web/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/web/src/main/java","modules/testcases/jdkAll/Eap7/web/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/web/src/main/java","modules/testcases/jdkAll/Eap71x/web/src/main/java","modules/testcases/jdkAll/Eap7.1.0.Beta/web/src/main/java","modules/testcases/jdkAll/Eap70x/web/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/web/src/main/java","modules/testcases/jdkAll/WildflyRelease-10.1.0.Final/web/src/main/java"})
public class DefaultConcurrencyTestServlet extends HttpServlet {

    @Resource
    private ContextService contextService;

    @Resource
    private ManagedExecutorService managedExecutorService;

    @Resource
    private ManagedScheduledExecutorService managedScheduledExecutorService;

    @Resource
    private ManagedThreadFactory managedThreadFactory;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // check injected resources
            if (contextService == null) {
                throw new NullPointerException("contextService");
            }
            if (managedExecutorService == null) {
                throw new NullPointerException("managedExecutorService");
            }
            if (managedScheduledExecutorService == null) {
                throw new NullPointerException("managedScheduledExecutorService");
            }
            if (managedThreadFactory == null) {
                throw new NullPointerException("managedThreadFactory");
            }
            // checked jndi lookup
            new InitialContext().lookup("java:comp/DefaultContextService");
            new InitialContext().lookup("java:comp/DefaultManagedExecutorService");
            new InitialContext().lookup("java:comp/DefaultManagedScheduledExecutorService");
            new InitialContext().lookup("java:comp/DefaultManagedThreadFactory");
        } catch (Throwable e) {
            throw new ServletException(e);
        }
    }

}
