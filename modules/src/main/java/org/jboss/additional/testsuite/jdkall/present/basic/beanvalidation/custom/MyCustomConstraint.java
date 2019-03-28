package org.jboss.additional.testsuite.jdkall.present.basic.beanvalidation.custom;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/basic/src/main/java","modules/testcases/jdkAll/Eap72x/basic/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/basic/src/main/java","modules/testcases/jdkAll/Protean/basic/releases/test-configurations/src/main/java#0.9.1**0.9.1","modules/testcases/jdkAll/Protean/basic/releases/test-configurations2/src/main/java#0.10.0"})
@Target({ ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MyCustomConstraint.Validator.class)
public @interface MyCustomConstraint {

    String message() default "{MyCustomConstraint.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<MyCustomConstraint, MyOtherBean> {

        @Override
        public boolean isValid(MyOtherBean value, ConstraintValidatorContext context) {
            return value.getName() != null;
        }
    }
}
