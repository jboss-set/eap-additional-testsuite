package org.server.test.infinispan.distributable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Simple servlet as from Wildfly TS
 */
@WebServlet(urlPatterns = { SimpleServlet.SERVLET_PATH })
public class SimpleServlet extends HttpServlet {

    private static final long serialVersionUID = -592774116315946908L;
    private static final String SERVLET_NAME = "simple";
    static final String SERVLET_PATH = "/" + SERVLET_NAME;
    public static final String REQUEST_DURATION_PARAM = "requestduration";
    public static final String HEADER_SERIALIZED = "serialized";
    public static final String VALUE_HEADER = "value";
    public static final String SESSION_ID_HEADER = "sessionId";
    public static final String ATTRIBUTE = "test";
    public static final String HEADER_NODE_NAME = "nodename";

    public static URI createURI(URL baseURL) throws URISyntaxException {
        return createURI(baseURL.toURI());
    }

    public static URI createURI(URI baseURI) {
        return baseURI.resolve(SERVLET_NAME);
    }

    public static URI createURI(URL baseURL, int requestDuration) throws URISyntaxException {
        return createURI(baseURL.toURI(), requestDuration);
    }

    public static URI createURI(URI baseURI, int requestDuration) {
        return baseURI.resolve(SERVLET_NAME + '?' + REQUEST_DURATION_PARAM + '=' + requestDuration);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(true);
        resp.addHeader(SESSION_ID_HEADER, session.getId());
        Mutable custom = (Mutable) session.getAttribute(ATTRIBUTE);
        if (custom == null) {
            if (!session.isNew()) throw new IllegalStateException();
            custom = new Mutable(1);
            session.setAttribute(ATTRIBUTE, custom);
        } else {
            if (session.isNew()) throw new IllegalStateException();
            custom.increment();
        }
        resp.setIntHeader(VALUE_HEADER, custom.getValue());
        resp.setHeader(HEADER_SERIALIZED, Boolean.toString(custom.wasSerialized()));

        try {
            String nodeName = System.getProperty("jboss.node.name");
            resp.setHeader(HEADER_NODE_NAME, nodeName);
        } catch (Exception ignore) {
        }

        this.getServletContext().log(req.getRequestURI() + ", value = " + custom.getValue());

        // Long running request?
        if (req.getParameter(REQUEST_DURATION_PARAM) != null) {
            int duration = Integer.parseInt(req.getParameter(REQUEST_DURATION_PARAM));
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        resp.getWriter().write("Success");
    }
}
