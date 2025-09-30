package io.github.mocanjie.base.mymvc.validator;

import org.apache.commons.lang3.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdCardValidator implements ConstraintValidator<IdCard, String> {

	private String message;
    private boolean required;
	
	@Override
	public void initialize(IdCard paramA) {
		this.message = paramA.message();
        this.required = paramA.required();
	}

	@Override
	public boolean isValid(String idCard, ConstraintValidatorContext context) {
        if(required && (idCard == null || idCard.trim().isEmpty())){
            if(!StringUtils.isBlank(message)){
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            }
            return false;
        }

        if(idCard == null || idCard.trim().isEmpty()){
            return true;
        }

        boolean valid = isIdCard(idCard);
        if(!valid && !StringUtils.isBlank(message)){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }
        return valid;
	}
	
	public static boolean isIdCard(String num){
		num = num.trim().toLowerCase();
		String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4",
                "3", "2" };     
        String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",     
                "9", "10", "5", "8", "4", "2" };     
        String Ai = "";     
        // ================ 号码的长度 15位或18位 ================     
		if (num.length() != 15 && num.length() != 18) {     
           return false;
        }     
        // =======================(end)========================     
    
        // ================ 数字 除最后以为都为数字 ================     
        if (num.length() == 18) {     
            Ai = num.substring(0, 17);     
        } else if (num.length() == 15) {     
            Ai = num.substring(0, 6) + "19" + num.substring(6, 15);     
        }     
        if (isNumeric(Ai) == false) {     
        	return false;
        }     
        // =======================(end)========================     
    
        // ================ 出生年月是否有效 ================     
        String strYear = Ai.substring(6, 10);// 年份     
        String strMonth = Ai.substring(10, 12);// 月份     
        String strDay = Ai.substring(12, 14);// 月份     
        if (isDataFormat(strYear + "-" + strMonth + "-" + strDay) == false) {     
            return false;  
        }     
        GregorianCalendar gc = new GregorianCalendar();     
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");     
        try {
			if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150 || (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {     
			    return false;
			}
		} catch (Exception e) {
			return false;
		} 
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {     
            return false;
        }     
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {     
           return false;   
        }     
        // =====================(end)=====================     
    
        // ================ 地区码时候有效 ================
        if (!AREA_CODE_MAP.containsKey(Ai.substring(0, 2))) {
           return false;
        }
        // ==============================================     
    
        // ================ 判断最后一位的值 ================     
        int TotalmulAiWi = 0;     
        for (int i = 0; i < 17; i++) {     
            TotalmulAiWi = TotalmulAiWi     
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))     
                    * Integer.parseInt(Wi[i]);     
        }     
        int modValue = TotalmulAiWi % 11;     
        String strVerifyCode = ValCodeArr[modValue];     
        Ai = Ai + strVerifyCode;     
    
        if (num.length() == 18) {     
             if (Ai.equals(num) == false) {     
               return false;     
             }     
         }    
         // =====================(end)=====================     
		return true;
	}
	

	public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");     
        Matcher isNum = pattern.matcher(str);     
        if (isNum.matches()) {     
            return true;     
        } else {     
            return false;     
        }     
    }  

    
	public static boolean isDataFormat(String str){  
    	  boolean flag=false;  
    	  //String regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";  
    	  String regxStr="^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";  
    	  Pattern pattern1=Pattern.compile(regxStr);  
    	  Matcher isNo=pattern1.matcher(str);  
    	  if(isNo.matches()){  
    	   flag=true;  
    	  }  
    	  return flag;  
    	 }  
	   

	
	private static final Map<String, String> AREA_CODE_MAP = Map.ofEntries(
        Map.entry("11", "北京"),
        Map.entry("12", "天津"),
        Map.entry("13", "河北"),
        Map.entry("14", "山西"),
        Map.entry("15", "内蒙古"),
        Map.entry("21", "辽宁"),
        Map.entry("22", "吉林"),
        Map.entry("23", "黑龙江"),
        Map.entry("31", "上海"),
        Map.entry("32", "江苏"),
        Map.entry("33", "浙江"),
        Map.entry("34", "安徽"),
        Map.entry("35", "福建"),
        Map.entry("36", "江西"),
        Map.entry("37", "山东"),
        Map.entry("41", "河南"),
        Map.entry("42", "湖北"),
        Map.entry("43", "湖南"),
        Map.entry("44", "广东"),
        Map.entry("45", "广西"),
        Map.entry("46", "海南"),
        Map.entry("50", "重庆"),
        Map.entry("51", "四川"),
        Map.entry("52", "贵州"),
        Map.entry("53", "云南"),
        Map.entry("54", "西藏"),
        Map.entry("61", "陕西"),
        Map.entry("62", "甘肃"),
        Map.entry("63", "青海"),
        Map.entry("64", "宁夏"),
        Map.entry("65", "新疆"),
        Map.entry("71", "台湾"),
        Map.entry("81", "香港"),
        Map.entry("82", "澳门"),
        Map.entry("91", "国外")
    );

}
