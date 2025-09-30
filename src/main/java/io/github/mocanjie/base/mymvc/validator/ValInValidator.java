package io.github.mocanjie.base.mymvc.validator;

import org.apache.commons.lang3.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Arrays;

public class ValInValidator implements ConstraintValidator<ValIn, String> {

	private String message;
    private boolean required;
    private String[] value;
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
	public boolean isValid(String requestVal, ConstraintValidatorContext context) {
		if(required && (requestVal == null || requestVal.trim().isEmpty())){
            if(!StringUtils.isBlank(message)){
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            }
            return false;
        }

        if(requestVal == null || requestVal.trim().isEmpty()){
            return true;
        }

        boolean matched = false;
        for(String val : value){
            if(ignoreCase ? requestVal.trim().equalsIgnoreCase(val) : requestVal.trim().equals(val)){
                matched = true;
                break;
            }
        }

        if(!matched && enumTypes.length > 0){
            for (EnumType enumType : enumTypes) {
                String key = enumType.valKey();
                String[] include = enumType.include();
                String[] exclude = enumType.exclude();
                Class<? extends Enum> enumName = enumType.type();
                Enum[] enumConstants = enumName.getEnumConstants();
                for (Enum enumConstant : enumConstants) {
                    String name = enumConstant.name();
                    if(include != null && include.length > 0){
                        boolean present = Arrays.stream(include).anyMatch(a -> name.equalsIgnoreCase(a));
                        if(!present) continue;
                    }
                    if(exclude != null && exclude.length > 0){
                        boolean present = Arrays.stream(exclude).anyMatch(a -> name.equalsIgnoreCase(a));
                        if(present) continue;
                    }
                    Class declaringClass = enumConstant.getDeclaringClass();
                    try {
                        Field declaredField = declaringClass.getDeclaredField(key);
                        declaredField.setAccessible(true);
                        Object o = declaredField.get(enumConstant);
                        String val = String.valueOf(o);
                        if(ignoreCase ? requestVal.trim().equalsIgnoreCase(val) : requestVal.trim().equals(val)){
                            matched = true;
                            break;
                        }
                    }catch (Exception e){
                        // Ignore field access errors
                    }
                }
                if(matched) break;
            }
        }

        if(!matched){
            if(!StringUtils.isBlank(message)){
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            }
            return false;
        }

        return true;
	}
}
