package io.github.mocanjie.base.mymvc.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

@Constraint(validatedBy = LimitLengthValidator.class)
//具体的实现
@Target({FIELD, PARAMETER})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface LimitLength {
	String message() default "不能为空,且长度不能大于100个字";
	int min() default 0;
	int max() default 255;
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
