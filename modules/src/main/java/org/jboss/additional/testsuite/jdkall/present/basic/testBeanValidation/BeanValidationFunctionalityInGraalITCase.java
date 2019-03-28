package org.jboss.additional.testsuite.jdkall.present.basic.testBeanValidation;

import org.jboss.shamrock.test.junit.SubstrateTest;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * Test various Bean Validation operations running in SubstrateVM
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Protean/basic/releases/test-configurations/src/test/java#0.9.1*0.9.1"})
@SubstrateTest
public class BeanValidationFunctionalityInGraalITCase extends BeanValidationFunctionalityTest {

}
