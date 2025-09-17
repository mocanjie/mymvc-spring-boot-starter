package io.github.mocanjie.base.mymvc.validator;

import org.apache.commons.lang3.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LimitLengthValidator implements ConstraintValidator<LimitLength, String> {

	private boolean required;
	private int chineseLength;
	private long min;
	private long max;

	@Override
	public void initialize(LimitLength paramA) {
		this.required = paramA.required();
		this.chineseLength = paramA.chineseLength();
		this.min = paramA.min();
		this.max = paramA.max();
	}

	@Override
	public boolean isValid(String text, ConstraintValidatorContext context) {
		// 如果字段为空
		if (StringUtils.isBlank(text)) {
			return !required; // 如果必填则返回false，否则返回true
		}

		// 计算字符串长度
		long len = stringLength(text);

		// 检查长度范围
		if (len < min || len > max) {
			// 自定义错误消息
			context.disableDefaultConstraintViolation();
			String message = String.format("字段长度必须在%d~%d之间", min, max);
			context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
			return false;
		}

		return true;
	}
	
	private long stringLength(String value) {
		long valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		for (int i = 0; i < value.length(); i++) {
			String temp = value.substring(i, i + 1);
			if (temp.matches(chinese)) {
				valueLength += chineseLength;
			} else {
				valueLength += 1;
			}
		}
		return valueLength;
	}

}
