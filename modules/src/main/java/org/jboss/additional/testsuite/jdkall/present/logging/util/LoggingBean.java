/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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

package org.jboss.additional.testsuite.jdkall.present.logging.util;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.logging.Logger;

/**
 * 
 * @author Petr Křemenský <pkremens@redhat.com>
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/logging/src/main/java","modules/testcases/jdkAll/Eap72x/logging/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/logging/src/main/java","modules/testcases/jdkAll/Eap7/logging/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/logging/src/main/java","modules/testcases/jdkAll/Eap71x/logging/src/main/java","modules/testcases/jdkAll/Eap7.1.0.Beta/logging/src/main/java","modules/testcases/jdkAll/Eap70x/logging/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/logging/src/main/java","modules/testcases/jdkAll/WildflyRelease-10.1.0.Final/logging/src/main/java","modules/testcases/jdkAll/Wildfly/logging/src/main/java","modules/testcases/jdkAll/Eap64x/logging/src/main/java","modules/testcases/jdkAll/Eap64x-Proposed/logging/src/main/java","modules/testcases/jdkAll/Eap63x/logging/src/main/java","modules/testcases/jdkAll/Eap62x/logging/src/main/java","modules/testcases/jdkAll/Eap61x/logging/src/main/java"})
public class LoggingBean {

    public void log() {
        Logger log = Logger.getLogger(this.getClass().getName().toString());
        log.trace("Dummy - JBoss logging bean - trace");
        log.fatal("Dummy - JBoss logging bean - fatal");
    }

    public void myLog(String string) {
        Logger log = Logger.getLogger(this.getClass().getName().toString());
        log.warn(string);
    }
}
