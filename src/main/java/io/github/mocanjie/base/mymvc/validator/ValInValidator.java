package io.github.mocanjie.base.mymvc.validator;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.NodeImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class ValInValidator implements ConstraintValidator<ValIn, String> {
	
	private String message;
    private boolean required;
    private String[] value;
    private String alertMessage;
    private boolean ignoreCase;
	
	@Override
	public void initialize(ValIn paramA) {
		this.message = paramA.message();
        this.required = paramA.required();
        this.value = paramA.value();
        this.ignoreCase = paramA.ignoreCase();
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
            for(String val : value){
                if(ignoreCase?!requestVal.trim().equalsIgnoreCase(val):!requestVal.trim().equals(val)){
                    if(message==null || message.trim().equals("")){
                        alertMessage = String.format("%s 的值不在规定范围",fileName);
                    }else{
                        alertMessage = message;
                    }
                }else {
                    return true;
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
