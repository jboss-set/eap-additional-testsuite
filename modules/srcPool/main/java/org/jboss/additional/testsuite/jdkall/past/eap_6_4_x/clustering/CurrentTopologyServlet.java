package org.jboss.additional.testsuite.jdkall.past.eap_6_4_x.clustering;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * Utility servlet that provides information on current members of the cluster.
 *
 * @author Tomas Hofman (thofman@redhat.com)
 */
@WebServlet(urlPatterns = {CurrentTopologyServlet.SERVLET_PATH})
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap64x/clustering/src/main/java","modules/testcases/jdkAll/Eap64x-Proposed/clustering/src/main/java","modules/testcases/jdkAll/Eap63x/clustering/src/main/java","modules/testcases/jdkAll/Eap62x/clustering/src/main/java","modules/testcases/jdkAll/Eap61x/clustering/src/main/java"})
public class CurrentTopologyServlet extends HttpServlet {

    private static final String SERVLET_NAME = "topology";
    public static final String SERVLET_PATH = "/" + SERVLET_NAME;
    public static final String CLUSTER = "cluster";
    public static final String MEMBERS = "members";

    @EJB
    CurrentTopology currentTopologyBean;

    public static URI createURI(URL baseURL, String cluster) throws URISyntaxException {
        StringBuilder builder = new StringBuilder(baseURL.toURI().resolve(SERVLET_NAME).toString());
        builder.append('?').append(CLUSTER).append('=').append(cluster);
        return URI.create(builder.toString());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cluster = req.getParameter(CLUSTER);
        if (cluster == null) {
            throw new ServletException(String.format("No '%s' parameter specified", CLUSTER));
        }
        Set<String> clusterMembers = this.currentTopologyBean.getClusterMembers(cluster);
        for (String member: clusterMembers) {
            resp.getWriter().write(member);
            resp.getWriter().write(",");
        }
    }
}
