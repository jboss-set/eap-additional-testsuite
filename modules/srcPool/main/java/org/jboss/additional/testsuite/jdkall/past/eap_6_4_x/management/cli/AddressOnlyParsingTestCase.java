/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.additional.testsuite.jdkall.past.eap_6_4_x.management.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.cli.CommandFormatException;

import org.jboss.as.cli.operation.CommandLineParser;
import org.jboss.as.cli.operation.OperationRequestAddress;
import org.jboss.as.cli.operation.OperationRequestAddress.Node;
import org.jboss.as.cli.operation.impl.DefaultCallbackHandler;
import org.jboss.as.cli.operation.impl.DefaultOperationRequestAddress;
import org.jboss.as.cli.operation.impl.DefaultOperationRequestParser;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author panos
 */
@RunWith(Arquillian.class)
@RunAsClient
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap64x/management/src/main/java","modules/testcases/jdkAll/Eap64x-Proposed/management/src/main/java"})
public class AddressOnlyParsingTestCase {

    private CommandLineParser parser = DefaultOperationRequestParser.INSTANCE;

    @Deployment
    public static Archive<?> getDeployment() {
        JavaArchive ja = ShrinkWrap.create(JavaArchive.class, "dummy.jar");
        ja.addClass(AddressOnlyParsingTestCase.class);
        return ja;
    }
    
    @Test
    public void testRootCharInTheMiddle() throws Exception {
        OperationRequestAddress prefix = new DefaultOperationRequestAddress();
        prefix.toNode("a", "b");
        DefaultCallbackHandler handler = new DefaultCallbackHandler(prefix);
        parser.parse("/", handler);
        assertTrue(handler.hasAddress());
        assertFalse(handler.hasOperationName());
        assertFalse(handler.hasProperties());
        assertFalse(handler.endsOnAddressOperationNameSeparator());
        assertFalse(handler.endsOnPropertyListStart());
        assertFalse(handler.endsOnPropertySeparator());
        assertFalse(handler.endsOnPropertyValueSeparator());
        assertTrue(handler.endsOnNodeSeparator());
        assertFalse(handler.endsOnNodeTypeNameSeparator());
        assertFalse(handler.isRequestComplete());
        OperationRequestAddress address = handler.getAddress();
        address.isEmpty();
        try {
            handler.reset();
            parser.parse("//", handler);
            Assert.fail("Shouldn't allow root character in the middle of the path");
        } catch (CommandFormatException e) {
// expected
        }
        try {
            handler.reset();
            parser.parse("./a/", handler);
            Assert.fail("Shouldn't allow root character in the middle of the path");
        } catch (CommandFormatException e) {
// expected
        }
        handler.reset();
        parser.parse("./a=b/", handler);
        try {
            handler.reset();
            parser.parse("./a=b//", handler);
            Assert.fail("Shouldn't allow root character in the middle of the path");
        } catch (CommandFormatException e) {
// expected
        }
        try {
            handler.reset();
            parser.parse("./a=b//a=b", handler);
            Assert.fail("Shouldn't allow root character in the middle of the path");
        } catch (CommandFormatException e) {
// expected
        }
    }
}
