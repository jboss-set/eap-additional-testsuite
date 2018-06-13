/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2017 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.additional.testsuite.jdkall.present.logging.misc.log;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/logging/src/main/java","modules/testcases/jdkAll/Wildfly/logging/src/main/java","modules/testcases/jdkAll/Eap7/logging/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/logging/src/main/java","modules/testcases/jdkAll/Eap71x/logging/src/main/java","modules/testcases/jdkAll/Eap70x/logging/src/main/java"})
public class LoggingUtil {
    private static final org.jboss.logging.Logger JBOSS_LOGGER = org.jboss.logging.Logger.getLogger(LoggingUtil.class);
    private static final org.apache.commons.logging.Log JCL_LOGGER = org.apache.commons.logging.LogFactory.getLog(LoggingUtil.class);
    private static final org.apache.log4j.Logger LOG4J_LOGGER = org.apache.log4j.Logger.getLogger(LoggingUtil.class);
    private static final java.util.logging.Logger JUL_LOGGER = java.util.logging.Logger.getLogger(LoggingUtil.class.getName());
    private static final org.slf4j.Logger SLF4J_LOGGER = org.slf4j.LoggerFactory.getLogger(LoggingUtil.class);

    public static void infoJBossLogging(final String category, final String message) {
        org.jboss.logging.Logger.getLogger(category).info(message + " TCCL: " + getClassLoader());
        org.jboss.logging.Logger.getLogger(category).info(message + " LoggingUtil CL: " + LoggingUtil.class.getClassLoader());
        JBOSS_LOGGER.infof("Test from static JBoss Logging TCCL: %s", getClassLoader());
        JBOSS_LOGGER.infof("Test from static JBoss Logging LoggingUtil CL: %s", LoggingUtil.class.getClassLoader());
    }

    public static void infoCommonsLogging(final String category, final String message) {
        org.apache.commons.logging.LogFactory.getLog(category).info(message + " TCCL: " + getClassLoader());
        org.apache.commons.logging.LogFactory.getLog(category).info(message + " LoggingUtil CL: " + LoggingUtil.class.getClassLoader());
        JCL_LOGGER.info("Test from static commons-logging: TCCL: " + getClassLoader());
        JCL_LOGGER.info("Test from static commons-logging LoggingUtil CL: " + LoggingUtil.class.getClassLoader());
    }

    public static void infoLog4j(final String category, final String message) {
        org.apache.log4j.Logger.getLogger(category).info(message + " TCCL: " + getClassLoader());
        org.apache.log4j.Logger.getLogger(category).info(message + " LoggingUtil CL: " + LoggingUtil.class.getClassLoader());
        LOG4J_LOGGER.info("Test from static log4j TCCL: " + getClassLoader());
        LOG4J_LOGGER.info("Test from static log4j LoggingUtil CL: " + LoggingUtil.class.getClassLoader());
    }

    public static void infoJul(final String category, final String message) {
        java.util.logging.Logger.getLogger(category).info(message + " TCCL: " + getClassLoader());
        java.util.logging.Logger.getLogger(category).info(message + " LoggingUtil CL: " + LoggingUtil.class.getClassLoader());
        JUL_LOGGER.info("Test from static JUL TCCL: " + getClassLoader());
        JUL_LOGGER.info("Test from static JU LoggingUtil CL: " + LoggingUtil.class.getClassLoader());
    }

    public static void infoSlf4j(final String category, final String message) {
        org.slf4j.LoggerFactory.getLogger(category).info(message + " TCCL: " + getClassLoader());
        org.slf4j.LoggerFactory.getLogger(category).info(message + " LoggingUtil CL: " + LoggingUtil.class.getClassLoader());
        SLF4J_LOGGER.info("Test from static slf4j TCCL: " + getClassLoader());
        SLF4J_LOGGER.info("Test from static slf4j LoggingUtil CL: " + LoggingUtil.class.getClassLoader());
    }

    public static ClassLoader getClassLoader() {
        ClassLoader result = Thread.currentThread().getContextClassLoader();
        if (result == null) {
            JBOSS_LOGGER.info("TCCL was null");
            LoggingUtil.class.getClassLoader();
        }
        return result;
    }
}
