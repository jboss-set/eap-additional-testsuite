/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
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

package org.jboss.additional.testsuite.jdkall.present.web.servlet.buffersize;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@WebServlet(name = "ResponseBufferSizeServlet", urlPatterns = {"/ResponseBufferSizeServlet"})
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/web/src/main/java","modules/testcases/jdkAll/Wildfly/web/src/main/java","modules/testcases/jdkAll/WildflyRelease-17.0.0.Final/web/src/main/java","modules/testcases/jdkAll/Eap72x/web/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/web/src/main/java","modules/testcases/jdkAll/Eap7/web/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/web/src/main/java","modules/testcases/jdkAll/Eap71x/web/src/main/java","modules/testcases/jdkAll/Eap7.1.0.Beta/web/src/main/java","modules/testcases/jdkAll/Eap70x/web/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/web/src/main/java","modules/testcases/jdkAll/Eap64x/web/src/main/java","modules/testcases/jdkAll/Eap64x-Proposed/web/src/main/java","modules/testcases/jdkAll/WildflyRelease-10.1.0.Final/web/src/main/java"})
public class ResponseBufferSizeServlet extends HttpServlet {

    public static final String SIZE_CHANGE_PARAM_NAME = "sizeChange";

    public static final String DATA_LENGTH_IN_PERCENTS_PARAM_NAME = "dataLengthInPercents";

    public static final String RESPONSE_COMMITED_MESSAGE = "Response committed";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sizeChangeAsStr = request.getParameter(SIZE_CHANGE_PARAM_NAME);
        String dataLengthInPercentsAsStr = request.getParameter(DATA_LENGTH_IN_PERCENTS_PARAM_NAME);
        double sizeChange = 1.0;
        double dataLengthModifier = 1.0;
        if (sizeChangeAsStr != null) {
            sizeChange = Double.parseDouble(sizeChangeAsStr);
        }
        if (sizeChangeAsStr != null) {
            dataLengthModifier = Double.parseDouble(dataLengthInPercentsAsStr);
        }
        int origBufferSize = response.getBufferSize();
        int newBufferSize = (int) (origBufferSize * sizeChange);
        int dataLength = (int) (newBufferSize * dataLengthModifier);

        int lineLength = 160; // setting line length to create nicer output

        // generating output of specified size
        response.setBufferSize(newBufferSize);
        StringBuffer outputBuffer = new StringBuffer(dataLength);
        for (int i = 0; i < dataLength; i++) {
            outputBuffer.append("X");
            if ((dataLength % lineLength) == 0) {
                outputBuffer.append('\n');
                i++;
            }
        }

        response.getWriter().write(outputBuffer.toString());
        if (response.isCommitted()) {
            response.getWriter().println(RESPONSE_COMMITED_MESSAGE);
        }
    }
}
