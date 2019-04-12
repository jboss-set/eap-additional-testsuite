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
package org.jboss.additional.testsuite.jdkall.present.web.threads;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.common.HttpRequest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import org.jboss.as.threads.ManagedJBossThreadPoolExecutorService;
import org.jboss.as.threads.TimeSpec;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.threads.JBossThreadPoolExecutorReuseIdleThreads;
import static org.junit.Assert.assertEquals;

/**
 * Tests the use of a custom thread pool with servlet deployments.
 * <p/>
 * This creates an executor with a single thread, and then invokes RaceyServlet
 * multiple times from several threads. If it is not using the correct
 */
@RunWith(Arquillian.class)
@RunAsClient
@apAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly-Unmerged/web/src/main/java"})
public class ServletThreadPoolSelectionTestCase {

    @ArquillianResource
    private URL url;


    @Deployment
    public static WebArchive deployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "war-example.war");
        war.addClasses(HttpRequest.class, SimpleServlet.class);
        return war;
    }

    @Test
    public void testExecutor() throws Exception {
        int maxThreads = 10;
        TimeSpec keepAliveSpec = new TimeSpec(SECONDS, 10);
        long keepAliveTime = keepAliveSpec.getUnit().toNanos(keepAliveSpec.getDuration());
        final JBossThreadPoolExecutorReuseIdleThreads jbossExecutor = new JBossThreadPoolExecutorReuseIdleThreads((int)(maxThreads/2), maxThreads, keepAliveTime, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>());
        
        ExecutorService executor = new ManagedJBossThreadPoolExecutorService(jbossExecutor);
        try {
            final List<Future<?>> results = new ArrayList<Future<?>>();
            for (int i = 1; i<100000; i++) {
                results.add(executor.submit(new Callable<Object>() {

                    @Override
                    public Object call() throws Exception {
                        HttpRequest.get(url.toExternalForm() + "/testNewThreadPool", 10, SECONDS);
                        return null;
                    }
                }));
            }
            for (Future<?> res : results) {
                res.get();
            }
            String result = HttpRequest.get(url.toExternalForm() + "/testNewThreadPool", 10, SECONDS);
            assertEquals("100000", result);
        } finally {
            executor.shutdown();
        }
    }
}
