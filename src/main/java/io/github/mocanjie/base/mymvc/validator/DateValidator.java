package io.github.mocanjie.base.mymvc.validator;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.NodeImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

public class DateValidator implements ConstraintValidator<Date, String> {
	
	private String message;
    private boolean required;
    private String format;
    private String alertMessage;

    private SimpleDateFormat sdf = new SimpleDateFormat();


	@Override
	public void initialize(Date paramA) {
		this.message = paramA.message();
        this.required = paramA.required();
        this.format = paramA.format();
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
            try{
                sdf.applyPattern(format);
                sdf.parse(requestVal);
            }catch (Exception e){
                if(message==null || message.trim().equals("")){
                    alertMessage = String.format("%s 必须是%s格式的日期",fileName,format);
                }else{
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
