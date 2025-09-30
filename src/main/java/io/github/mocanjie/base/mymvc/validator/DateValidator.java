package io.github.mocanjie.base.mymvc.validator;

import org.apache.commons.lang3.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.text.SimpleDateFormat;

public class DateValidator implements ConstraintValidator<Date, String> {

	private String message;
    private boolean required;
    private String format;
    private final SimpleDateFormat sdf = new SimpleDateFormat();


	@Override
	public void initialize(Date paramA) {
		this.message = paramA.message();
        this.required = paramA.required();
        this.format = paramA.format();
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

        try{
            sdf.applyPattern(format);
            sdf.parse(requestVal);
            return true;
        }catch (Exception e){
            if(!StringUtils.isBlank(message)){
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            }
            return false;
        }
	}

}
