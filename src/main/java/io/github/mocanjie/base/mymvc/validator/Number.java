package io.github.mocanjie.base.mymvc.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * 验证是否位数字
 */
@Constraint(validatedBy = NumberValidator.class)
// 具体的实现
@Target({FIELD, PARAMETER})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface Number {
	String message() default "";

	boolean required() default true;

	long max() default Integer.MAX_VALUE;

	long min() default Integer.MIN_VALUE;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
