package io.github.mocanjie.base.mymvc.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LimitLengthValidator implements ConstraintValidator<LimitLength, String> {
	
	private String message;
	private int min;
	private int max;

	@Override
	public void initialize(LimitLength paramA) {
		this.message = paramA.message();
		this.min = paramA.min();
		this.max = paramA.max();
	}

	@Override
	public boolean isValid(String text,ConstraintValidatorContext paramConstraintValidatorContext) {
		if(text==null || text.trim().equals("")){
			message = "长度不能为空";
		}else{
			int len = stringLength(text);
			if(len < this.min) return false;
			if(len > this.max) return false;
			return true;
		}
		paramConstraintValidatorContext.disableDefaultConstraintViolation();
		paramConstraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
		return false;
	}
	
	private static int stringLength(String value) {
		int valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		for (int i = 0; i < value.length(); i++) {
			String temp = value.substring(i, i + 1);
			if (temp.matches(chinese)) {
				valueLength += 2;
			} else {
				valueLength += 1;
			}
		}
		return valueLength;
	}

}
