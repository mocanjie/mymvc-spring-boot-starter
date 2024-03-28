package io.github.mocanjie.base.mymvc.validator;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.NodeImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.reflect.Field;
import java.util.Map;

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
		// 构建提示msg
		if(StringUtils.isBlank(message)){
			ConstraintValidatorContextImpl context = (ConstraintValidatorContextImpl) paramConstraintValidatorContext;
			ConstraintDescriptor<?> constraintDescriptor = context.getConstraintDescriptor();
			Map<String, Object> attributes = constraintDescriptor.getAttributes();
			Object max = attributes.get("max");
			Object min = attributes.get("min");
			try {
				Field basePath = ConstraintValidatorContextImpl.class.getDeclaredField("basePath");
				basePath.setAccessible(true);  //这个起决定作用
				PathImpl pathImpl = (PathImpl)basePath.get(context);
				NodeImpl leafNode = pathImpl.getLeafNode();
				String fileName = leafNode.getName();
				message = String.format("%s 长度必须在%s~%s之间", fileName,min,max);
			}catch (Exception e){
				message = String.format("字段长度必须在%s~%s之间", min,max);
			}
		}
		//判断text是否符合,并输出
		paramConstraintValidatorContext.disableDefaultConstraintViolation();
		paramConstraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
		if(StringUtils.isBlank(text)){
			return false;
		}else{
			int len = stringLength(text);
			if(len < this.min) return false;
			if(len > this.max) return false;
			return true;
		}
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
