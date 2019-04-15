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

package org.jboss.additional.testsuite.jdkall.present.quarkus.basic;


import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import io.quarkus.test.junit.SubstrateTest;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Protean/quarkus/protean/test-configurations2/src/test/java#0.10.0*0.12.0","modules/testcases/jdkAll/Protean/quarkus/quark/test-configurations/src/test/java#0.13.0*999.0.1"})
@SubstrateTest
public class QuarkusSimpleInGraalITCase extends QuarkusSimpleTestCase{

}
