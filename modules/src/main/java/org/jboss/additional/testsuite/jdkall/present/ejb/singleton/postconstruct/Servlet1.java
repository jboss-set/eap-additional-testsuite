package org.jboss.additional.testsuite.jdkall.present.ejb.singleton.postconstruct;

import javax.servlet.annotation.*;
import javax.management.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@WebServlet(name = "Servlet1", urlPatterns = { "/servlet1" }, loadOnStartup = 1)
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java", "modules/testcases/jdkAll/Eap7/ejb/src/main/java", "modules/testcases/jdkAll/Eap72x/ejb/src/main/java", "modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java"})
public class Servlet1 extends HttpServlet
{
    private MBeanServer platformMBeanServer;
    
    public void init(final ServletConfig config) throws ServletException {
        System.out.println("***** INIT ******");
    }
    
    public void destroy() {
        System.out.println("***** DESTROY ******");
    }
    
    protected void doGet(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        final PrintWriter pw = res.getWriter();
        pw.println("remoteHost: " + req.getRemoteHost());
        pw.println("queryString: " + req.getQueryString());
        pw.println("servletPath: " + req.getServletPath());
        pw.println("getRequestURL: " + (Object)req.getRequestURL());
        pw.println("jboss.node.name: " + System.getProperty("jboss.node.name"));
    }
    
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
