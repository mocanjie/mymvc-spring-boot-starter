package io.github.mocanjie.base.mymvc.validator;

import org.apache.commons.lang3.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class NumberValidator implements ConstraintValidator<Number, String> {

	private String message;
    private boolean required;
    private boolean integer;
    private long min;
    private long max;

	@Override
	public void initialize(Number paramA) {
		this.message = paramA.message();
        this.required = paramA.required();
        this.min = paramA.min();
        this.max = paramA.max();
        this.integer = paramA.integer();
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

        BigDecimal bigDecimal;
        try{
            bigDecimal = new BigDecimal(requestVal);
            if(integer && bigDecimal.stripTrailingZeros().scale() > 0){
                if(!StringUtils.isBlank(message)){
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                }
                return false;
            }
        }catch (Exception e){
            if(!StringUtils.isBlank(message)){
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            }
            return false;
        }

        if (bigDecimal.compareTo(new BigDecimal(min)) < 0) {
            if(!StringUtils.isBlank(message)){
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            }
            return false;
        }

        if (bigDecimal.compareTo(new BigDecimal(max)) > 0) {
            if(!StringUtils.isBlank(message)){
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            }
            return false;
        }

        return true;
	}

}
