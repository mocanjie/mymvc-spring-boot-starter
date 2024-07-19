package io.github.mocanjie.base.mymvc.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * 校验是否为数字，且在范围内
 */
@Constraint(validatedBy = ValInValidator.class)
// 具体的实现
@Target({FIELD, PARAMETER})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface ValIn {
	String message() default "";

	String[] value() default {};

	// 忽略大小写
	boolean ignoreCase() default true;
	boolean required() default true;

	EnumType[] enumType() default {};

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
