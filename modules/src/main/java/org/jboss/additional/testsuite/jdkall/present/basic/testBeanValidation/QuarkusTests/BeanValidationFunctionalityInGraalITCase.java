package org.jboss.additional.testsuite.jdkall.present.basic.testBeanValidation;

import io.quarkus.test.junit.SubstrateTest;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * Test various Bean Validation operations running in SubstrateVM
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Protean/basic/releases/test-configurations2/src/test/java#0.10.0*0.12.0"})
@SubstrateTest
public class BeanValidationFunctionalityInGraalITCase extends BeanValidationFunctionalityTest {

}
