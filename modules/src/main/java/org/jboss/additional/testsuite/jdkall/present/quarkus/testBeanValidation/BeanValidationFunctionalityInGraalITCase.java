package org.jboss.additional.testsuite.jdkall.present.quarkus.testBeanValidation;

import org.jboss.shamrock.test.junit.SubstrateTest;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * Test various Bean Validation operations running in SubstrateVM
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Protean/quarkus/protean/test-configurations/src/test/java#0.9.1*0.9.2"})
@SubstrateTest
public class BeanValidationFunctionalityInGraalITCase extends BeanValidationFunctionalityTest {

}
