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

package org.jboss.additional.testsuite.jdkall.present.manualmode.logging;

import org.apache.log4j.Logger;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.jboss.additional.testsuite.jdkall.present.manualmode.logging.Log4jLoggingLevelServlet.URL_PATTERN;

/**
 * @author <a href="mailto:pmackay@redhat.com">Peter Mackay</a>
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap72x/manualmode/src/main/java", "modules/testcases/jdkAll/Eap72x-Proposed/manualmode/src/main/java", "modules/testcases/jdkAll/Eap71x/manualmode/src/main/java#7.1.5", "modules/testcases/jdkAll/Eap71x-Proposed/manualmode/src/main/java#7.1.5", "modules/testcases/jdkAll/Wildfly/manualmode/src/main/java#14.0.0"})
@WebServlet(urlPatterns = URL_PATTERN)
public class Log4jLoggingLevelServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(Log4jLoggingLevelServlet.class);
    public static final String URL_PATTERN = "Log4JLogging";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = LOGGER.getEffectiveLevel().toString();
        PrintWriter writer = resp.getWriter();
        writer.print(msg);
        writer.close();
    }
}
