package io.github.mocanjie.base.mymvc.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * 身份证号
 * 
 * @author canjie.mo
 * @since 2016年8月19日
 */
@Constraint(validatedBy = IdCardValidator.class)
// 具体的实现
@Target({FIELD, PARAMETER})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface IdCard {
	String message() default "身份证号码格式不正确";

	boolean required() default true;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
