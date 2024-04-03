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
	
	private boolean required;

	private static int chineseLength;

	@Override
	public void initialize(LimitLength paramA) {
		this.required = paramA.required();
		this.chineseLength = paramA.chineseLength();
	}


	@Override
	public boolean isValid(String text,ConstraintValidatorContext paramConstraintValidatorContext) {
		ConstraintValidatorContextImpl context = (ConstraintValidatorContextImpl) paramConstraintValidatorContext;
		ConstraintDescriptor<?> constraintDescriptor = context.getConstraintDescriptor();
		Map<String, Object> attributes = constraintDescriptor.getAttributes();
		Object max = attributes.get("max");
		Object min = attributes.get("min");
		Object message = attributes.get("message");
		String alertMessage = null;
		// 构建提示msg
		try {
			Field basePath = ConstraintValidatorContextImpl.class.getDeclaredField("basePath");
			basePath.setAccessible(true);  //这个起决定作用
			PathImpl pathImpl = (PathImpl)basePath.get(context);
			NodeImpl leafNode = pathImpl.getLeafNode();
			String fileName = leafNode.getName();
			if(required && StringUtils.isBlank(text)){
				if(message==null || message.toString().trim().equals("")){
					alertMessage = String.format("%s 不能为空",fileName);
				}else{
					alertMessage = message.toString();
				}
			}else{
				if(StringUtils.isBlank(text)){
					return true;
				}
			}
			alertMessage = String.format("%s 长度必须在%s~%s之间", fileName,min,max);
		}catch (Exception e){
			alertMessage = String.format("字段长度必须在%s~%s之间", min,max);
		}
		//判断text是否符合,并输出
		paramConstraintValidatorContext.disableDefaultConstraintViolation();
		paramConstraintValidatorContext.buildConstraintViolationWithTemplate(alertMessage).addConstraintViolation();
		long len = stringLength(text);
		if(len < Long.parseLong(min.toString())) return false;
		if(len > Long.parseLong(max.toString())) return false;
		return true;
	}
	
	private static long stringLength(String value) {
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
