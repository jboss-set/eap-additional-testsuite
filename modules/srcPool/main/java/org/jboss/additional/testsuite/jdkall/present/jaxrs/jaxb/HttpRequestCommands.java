/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.additional.testsuite.jdkall.present.jaxrs.jaxb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/OpenLiberty/jaxrs/src/main/java","modules/testcases/jdkAll/Wildfly/jaxrs/src/main/java","modules/testcases/jdkAll/WildflyRelease-17.0.0.Final/jaxrs/src/main/java","modules/testcases/jdkAll/Eap72x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/jaxrs/src/main/java","modules/testcases/jdkAll/Eap7/jaxrs/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/jaxrs/src/main/java","modules/testcases/jdkAll/Eap71x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap7.1.0.Beta/jaxrs/src/main/java","modules/testcases/jdkAll/Eap70x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/jaxrs/src/main/java","modules/testcases/jdkAll/WildflyRelease-10.1.0.Final/jaxrs/src/main/java","modules/testcases/jdkAll/Eap64x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap64x-Proposed/jaxrs/src/main/java","modules/testcases/jdkAll/Eap63x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap62x/jaxrs/src/main/java","modules/testcases/jdkAll/Eap61x/jaxrs/src/main/java"})
public class HttpRequestCommands {
    private static String execute(final Callable<String> task, final long timeout, final TimeUnit unit) throws TimeoutException, ExecutionException {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<String> result = executor.submit(task);
        try {
            return result.get(timeout, unit);
        } catch (TimeoutException e) {
            result.cancel(true);
            throw e;
        } catch (InterruptedException e) {
            // should not happen
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw e;
        } finally {
            executor.shutdownNow();
            try {
                executor.awaitTermination(timeout, unit);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    public static String get(final String spec, final long timeout, final TimeUnit unit) throws MalformedURLException, ExecutionException, TimeoutException {
        final URL url = new URL(spec);
        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                return processResponse(conn);
            }
        };
        return execute(task, timeout, unit);
    }

    private static String read(final InputStream in) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        int b;
        while((b = in.read()) != -1) {
            out.write(b);
        }
        return out.toString();
    }

    private static String processResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            final InputStream err = conn.getErrorStream();
            try {
                throw new IOException(read(err));
            }
            finally {
                err.close();
            }
        }
        final InputStream in = conn.getInputStream();
        try {
            return read(in);
        }
        finally {
            in.close();
        }
    }

    public static String put(final String spec, final String message, final long timeout, final TimeUnit unit) throws MalformedURLException, ExecutionException, TimeoutException {
        final URL url = new URL(spec);
        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                final OutputStream out = conn.getOutputStream();
                try {
                    write(out, message);
                    return processResponse(conn);
                }
                finally {
                    out.close();
                }
            }
        };
        return execute(task, timeout, unit);
    }

    private static void write(OutputStream out, String message) throws IOException {
        final OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        writer.write(message);
        writer.flush();
    }
}
