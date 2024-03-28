package io.github.mocanjie.base.mymvc.validator;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.NodeImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.math.BigDecimal;

public class NumberValidator implements ConstraintValidator<Number, String> {
	
	private String message;
    private boolean required;

    private String alertMessage;

    private long min;
    private long max;

	@Override
	public void initialize(Number paramA) {
		this.message = paramA.message();
        this.required = paramA.required();
        this.min = paramA.min();
        this.max = paramA.max();
	}

	@Override
	public boolean isValid(String requestVal,ConstraintValidatorContext paramConstraintValidatorContext) {
        ConstraintValidatorContextImpl context = (ConstraintValidatorContextImpl) paramConstraintValidatorContext;
        String fileName = "";
        alertMessage = null;
        try {
            Field basePath = ConstraintValidatorContextImpl.class.getDeclaredField("basePath");
            basePath.setAccessible(true);  //这个起决定作用
            PathImpl pathImpl = (PathImpl)basePath.get(context);
            NodeImpl leafNode = pathImpl.getLeafNode();
            fileName = leafNode.getName();
        }catch (Exception e){
        }
		if(required && (requestVal==null || requestVal.trim().equals(""))){
            if(message==null || message.trim().equals("")){
                alertMessage = String.format("%s 不能为空",fileName);
            }else{
                alertMessage = message;
            }
		}else{
            if(requestVal==null || requestVal.trim().equals("")){
                return true;
            }
            BigDecimal bigDecimal = BigDecimal.ZERO;
            try{
                bigDecimal = new BigDecimal(requestVal);
            }catch (Exception e){
                if(message==null || message.trim().equals("")){
                    alertMessage = String.format("%s 必须是数字",fileName);
                }else{
                    alertMessage = message;
                }
            }

            if (bigDecimal.compareTo(new BigDecimal(min)) < 0) {
                if(message==null || message.trim().equals("")){
                    alertMessage = String.format("%s 必须大于等于 %s",fileName,min);
                }else{
                    alertMessage = message;
                }
            }

            if (bigDecimal.compareTo(new BigDecimal(max)) > 0) {
                if(message==null || message.trim().equals("")){
                    alertMessage = String.format("%s 必须小于等于 %s",fileName,max);
                }else {
                    alertMessage = message;
                }
            }

		}
        if(StringUtils.isBlank(alertMessage)){
            return true;
        }
		paramConstraintValidatorContext.disableDefaultConstraintViolation();
		paramConstraintValidatorContext.buildConstraintViolationWithTemplate(alertMessage).addConstraintViolation();
		return false;
	}

}
