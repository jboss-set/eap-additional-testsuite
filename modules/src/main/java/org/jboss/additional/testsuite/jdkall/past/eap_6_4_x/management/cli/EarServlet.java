package org.jboss.additional.testsuite.jdkall.past.eap_6_4_x.management.cli;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * @author Stuart Douglas
 */
@WebServlet(urlPatterns = "/EarServlet")
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap64x/management/src/main/java","modules/testcases/jdkAll/Eap64x-Proposed/management/src/main/java","modules/testcases/jdkAll/Eap63x/management/src/main/java","modules/testcases/jdkAll/Eap62x/management/src/main/java","modules/testcases/jdkAll/Eap61x/management/src/main/java"})
public class EarServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        InputStream resource = getClass().getClassLoader().getResourceAsStream("jar-info.txt");
        try {
            byte[] data = new byte[100];
            int res;
            while ((res = resource.read(data)) > 0) {
                resp.getOutputStream().write(data, 0, res);
            }
        } finally {
            resource.close();
        }
    }
}
