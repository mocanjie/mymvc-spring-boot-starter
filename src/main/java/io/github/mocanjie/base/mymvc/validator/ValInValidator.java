package io.github.mocanjie.base.mymvc.validator;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.NodeImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Arrays;

public class ValInValidator implements ConstraintValidator<ValIn, String> {
	
	private String message;
    private boolean required;
    private String[] value;
    private String alertMessage;
    private boolean ignoreCase;
    private EnumType[] enumTypes;

	@Override
	public void initialize(ValIn paramA) {
		this.message = paramA.message();
        this.required = paramA.required();
        this.value = paramA.value();
        this.ignoreCase = paramA.ignoreCase();
        this.enumTypes = paramA.enumType();
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

            for (EnumType enumType : enumTypes) {
                String key = enumType.valKey();
                String[] include = enumType.include();
                String[] exclude = enumType.exclude();
                Class<? extends Enum> enumName = enumType.type();
                Enum[] enumConstants = enumName.getEnumConstants();
                for (Enum enumConstant : enumConstants) {
                    String name = enumConstant.name();
                    if(include!=null && include.length>0){
                        boolean present = Arrays.stream(include).filter(a -> name.equalsIgnoreCase(a)).findFirst().isPresent();
                        if(!present) continue;
                    }
                    if(exclude!=null && exclude.length>0){
                        boolean present = Arrays.stream(exclude).filter(a -> name.equalsIgnoreCase(a)).findFirst().isPresent();
                        if(present) continue;
                    }
                    Class declaringClass = enumConstant.getDeclaringClass();
                    try {
                        Field declaredField = declaringClass.getDeclaredField(key);
                        declaredField.setAccessible(true);
                        Object o = declaredField.get(enumConstant);
                        String val = String.valueOf(o);
                        if(ignoreCase?!requestVal.trim().equalsIgnoreCase(val):!requestVal.trim().equals(val)){
                            if(message==null || message.trim().equals("")){
                                alertMessage = String.format("%s 的值不在规定范围",fileName);
                            }else{
                                alertMessage = message;
                            }
                        }else {
                            return true;
                        }
                    }catch (Exception e){

                    }
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
