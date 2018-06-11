/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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

package org.jboss.additional.testsuite.jdkall.present.core.jca;

import java.io.IOException;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.integration.management.base.AbstractCliTestBase;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.test.integration.management.util.CLIOpResult;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jiri Bilek
 * jbilek@redhat.com on 12/03/18.
 * Test for JBEAP-14635 JBJCA-1375
 *
 * ActiveCount of data-source is not correct after reloaded
 * ActiveCount in data-source statistics is not correct after executing reload CLI command.
 *
 * Sometimes PoolStatisticsImpl is cleared too late during AS reload, and some
 * connections are already counted. That results in wrong stats count (e.g. "ActiveCount").
 * The test trying find out ActiveCount 'COUNT_OF_RETRYING' times
 *
 */
@RunWith(Arquillian.class)
@RunAsClient
//@AdditionalTestsuite({"modules/testcases/jdkAll/Eap71x-Proposed/management/src/main/java#7.1.4","modules/testcases/jdkAll/Eap71x/management/src/main/java#7.1.4","modules/testcases/jdkAll/Wildfly/management/src/main/java#14.0.0"})
public class DatasourcesActiveCountTestCase extends AbstractCliTestBase {

   private static final String OP_PATTERN = "/subsystem=datasources/data-source=ExampleDS:write-attribute(name=%s,value=%s)";
   private static final String OP_PATTERN_RELOAD = "reload";
   private static final String OP_PATTERN_READ = "/subsystem=datasources/data-source=ExampleDS/statistics=pool:read-resource(include-runtime=true)";

   // The test trying find out ActiveCount 'COUNT_OF_RETRYING' times
   private static final int COUNT_OF_RETRYING = 10;

   @BeforeClass
   public static void before() throws Exception {
      initCLI();
   }

   @Before
   public void before2() throws Exception {
      writeAttributeIntoDatasourceViaCli("statistics-enabled",true);
      writeAttributeIntoDatasourceViaCli("min-pool-size", 100);
      writeAttributeIntoDatasourceViaCli("initial-pool-size", 100);
      writeAttributeIntoDatasourceViaCli("max-pool-size", 100);
      writeAttributeIntoDatasourceViaCli("pool-prefill",true);
      reloadServerViaCli();
   }

   @AfterClass
   public static void after() throws Exception {
      closeCLI();
   }

   private void writeAttributeIntoDatasourceViaCli(String name, boolean value) throws IOException {
      writeAttributeIntoDatasourceViaCli(name, String.valueOf(value));
   }

   private void writeAttributeIntoDatasourceViaCli(String name, int value) throws IOException {
      writeAttributeIntoDatasourceViaCli(name, String.valueOf(value));
   }

   private void writeAttributeIntoDatasourceViaCli(String name, String value) throws IOException {
      cli.sendLine(String.format(OP_PATTERN, name, value));
      CLIOpResult opResult = cli.readAllAsOpResult();
      Assert.assertTrue(opResult.isIsOutcomeSuccess());
   }

   private void reloadServerViaCli() {
      cli.sendLine(OP_PATTERN_RELOAD);
   }

   private void checkNumberOfActiveCount() throws IOException {
      cli.sendLine(OP_PATTERN_READ);
      CLIOpResult opResult = cli.readAllAsOpResult();
      Assert.assertTrue(opResult.isIsOutcomeSuccess());
      Assert.assertEquals("(\"ActiveCount\" => 100)", opResult.getResponseNode().get(ModelDescriptionConstants.RESULT).asList().get(0).toString());
   }

   @Test
   public void testCheckCountOfActiveDatasources() throws IOException {

      // try to reload and check ActiveCount more times
      for (int i = 0 ; i < COUNT_OF_RETRYING ; i++) {
         checkNumberOfActiveCount();
         reloadServerViaCli();
      }
   }
}
