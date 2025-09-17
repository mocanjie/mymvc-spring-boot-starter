package io.github.mocanjie.base.mymvc.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.groups.Default;
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

	boolean integer() default false;

	long max() default Integer.MAX_VALUE;

	long min() default Integer.MIN_VALUE;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
