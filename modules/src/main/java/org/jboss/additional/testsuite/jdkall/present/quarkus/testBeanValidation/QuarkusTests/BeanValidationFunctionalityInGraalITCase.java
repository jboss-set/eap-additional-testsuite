package org.jboss.additional.testsuite.jdkall.present.quarkus.testBeanValidation;

import io.quarkus.test.junit.SubstrateTest;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * Test various Bean Validation operations running in SubstrateVM
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Protean/quarkus/protean/test-configurations2/src/test/java#0.10.0*0.12.1","modules/testcases/jdkAll/Protean/quarkus/quark/test-configurations/src/test/java#999.0.0"})
@SubstrateTest
public class BeanValidationFunctionalityInGraalITCase extends BeanValidationFunctionalityTest {

}
