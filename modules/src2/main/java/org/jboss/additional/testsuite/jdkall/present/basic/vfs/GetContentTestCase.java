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
package org.jboss.additional.testsuite.jdkall.present.basic.vfs;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.vfs.VirtualFile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.awt.image.ImageProducer;
import java.io.IOException;
import java.net.URL;

/**
 * Test that calling URL.getContent() return correct objects.
 *
 * JBEAP-15028 / WFLY-10706
 *
 * @author Peter Mackay
 */
@RunWith(Arquillian.class)
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/basic/src/main/java#14.0.0","modules/testcases/jdkAll/Eap72x-Proposed/basic/src/main/java#7.2.0.CD14","modules/testcases/jdkAll/Eap72x/basic/src/main/java#7.2.0.CD14","modules/testcases/jdkAll/Eap71x-Proposed/basic/src/main/java#7.1.5","modules/testcases/jdkAll/Eap71x/basic/src/main/java#7.1.5"})
public class GetContentTestCase {

    private static final String ERROR_MSG = "URL.getContent call returned an object of incorrect class. " +
        "See https://issues.jboss.org/browse/JBEAP-15028 for details";

    @Deployment
    public static WebArchive getDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, "getContentTestCase.war");
        archive.addClass(GetContentTestCase.class);
        archive.addAsWebInfResource("image.png", "classes/image.png");
        return archive;
    }

    /**
     * Test that getContent returns an instance of ImageProducer, not VirtualFile
     * https://issues.jboss.org/browse/JBEAP-15028
     */
    @Test
    public void testCastToImageProducer() throws IOException, InterruptedException {
        URL resource = GetContentTestCase.class.getResource("/image.png");
        Object content = resource.getContent();
        Assert.assertFalse(ERROR_MSG, content instanceof VirtualFile);
        Assert.assertTrue(ERROR_MSG, content instanceof ImageProducer);
        ImageProducer producer = (ImageProducer) content;
    }

}
