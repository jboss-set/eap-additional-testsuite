/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2019, Red Hat, Inc., and individual contributors
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

package org.jboss.additional.testsuite.jdkall.present.basic.websockets;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;


@EapAdditionalTestsuite({"modules/testcases/jdkAll/Protean/basic/releases/test-configurations2/src/test/java#0.10.0*0.12.0","modules/testcases/jdkAll/Protean/basic/upstream/test-configurations/src/test/java#999.0.0"})
@QuarkusTest
public class WebsocketApplicationScopedTestCase {
    private static final HashMap<String, LinkedBlockingDeque<String>> queues = new HashMap<>();

    @TestHTTPResource("/chat/user1")
    URI uriUser1;

    @TestHTTPResource("/chat/user2")
    URI uriUser2;

    @Test
    public void testWebsocketChat() throws Exception {
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uriUser1)) {
            //Wait until the client is initialized e.q. the OnOpen is executed
            Thread.sleep(10);
            Assertions.assertTrue(queues.size() > 0);

            //Check if the user1 is connected
            Assertions.assertEquals("CONNECT", queues.get(session.getId()).poll(10, TimeUnit.SECONDS));
            Assertions.assertEquals("User user1 joined", queues.get(session.getId()).poll(10, TimeUnit.SECONDS));
            session.getAsyncRemote().sendText("hello world");
            Assertions.assertEquals(">> user1: hello world", queues.get(session.getId()).poll(10, TimeUnit.SECONDS));

            try (Session sessioUser2 = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uriUser2)) {
                //Wait until the client is initialized e.q. the OnOpen is executed
                Thread.sleep(10);
                Assertions.assertTrue(queues.size() > 1);

                //Assert that the sessioUser2 got the messages
                Assertions.assertEquals("CONNECT", queues.get(sessioUser2.getId()).poll(10, TimeUnit.SECONDS));
                Assertions.assertEquals("User user2 joined", queues.get(sessioUser2.getId()).poll(10, TimeUnit.SECONDS));
                sessioUser2.getAsyncRemote().sendText("hello world");
                Assertions.assertEquals(">> user2: hello world", queues.get(sessioUser2.getId()).poll(10, TimeUnit.SECONDS));

                //Assert that the user1's session got user2's messages
                Assertions.assertEquals("User user2 joined", queues.get(session.getId()).poll(10, TimeUnit.SECONDS));
                Assertions.assertEquals(">> user2: hello world", queues.get(session.getId()).poll(10, TimeUnit.SECONDS));
            }
        }
    }

    @ClientEndpoint
    public static class Client {

        @OnOpen
        public void open(Session session) {
            queues.put(session.getId(), new LinkedBlockingDeque<>());
            queues.get(session.getId()).add("CONNECT");
        }

        @OnMessage
        void message(Session session, String msg) {
            queues.get(session.getId()).add(msg);
        }

    }
}
