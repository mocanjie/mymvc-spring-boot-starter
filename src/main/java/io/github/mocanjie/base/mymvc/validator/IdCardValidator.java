package io.github.mocanjie.base.mymvc.validator;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.NodeImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdCardValidator implements ConstraintValidator<IdCard, String> {
	
	private String message;

    private boolean required;

    private String alertMessage;
	
	@Override
	public void initialize(IdCard paramA) {
		this.message = paramA.message();
        this.required = paramA.required();
	}

	@Override
	public boolean isValid(String idCard,ConstraintValidatorContext paramConstraintValidatorContext) {

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
        if(required && (idCard==null || idCard.trim().equals(""))){
            if(message==null || message.trim().equals("")){
                alertMessage = String.format("%s 不能为空",fileName);
            }else{
                alertMessage = message;
            }
        }else{
            if(idCard==null || idCard.trim().equals("")){
                return true;
            }
            if(isIdCard(idCard)){
                return true;
            }else{
                if(message==null || message.trim().equals("")){
                    alertMessage = String.format("%s 身份证号码格式不正确",fileName);
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
        Hashtable<String, String> h = GetAreaCode();     
        if (h.get(Ai.substring(0, 2)) == null) {     
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
	   

	
	public static Hashtable<String, String> GetAreaCode() {
        Hashtable<String, String> hashtable = new Hashtable<String, String>();     
        hashtable.put("11", "北京");     
        hashtable.put("12", "天津");     
        hashtable.put("13", "河北");     
        hashtable.put("14", "山西");     
        hashtable.put("15", "内蒙古");     
        hashtable.put("21", "辽宁");     
        hashtable.put("22", "吉林");     
        hashtable.put("23", "黑龙江");     
        hashtable.put("31", "上海");     
        hashtable.put("32", "江苏");     
        hashtable.put("33", "浙江");     
        hashtable.put("34", "安徽");     
        hashtable.put("35", "福建");     
        hashtable.put("36", "江西");     
        hashtable.put("37", "山东");     
        hashtable.put("41", "河南");     
        hashtable.put("42", "湖北");     
        hashtable.put("43", "湖南");     
        hashtable.put("44", "广东");     
        hashtable.put("45", "广西");     
        hashtable.put("46", "海南");     
        hashtable.put("50", "重庆");     
        hashtable.put("51", "四川");     
        hashtable.put("52", "贵州");     
        hashtable.put("53", "云南");     
        hashtable.put("54", "西藏");     
        hashtable.put("61", "陕西");     
        hashtable.put("62", "甘肃");     
        hashtable.put("63", "青海");     
        hashtable.put("64", "宁夏");     
        hashtable.put("65", "新疆");     
        hashtable.put("71", "台湾");     
        hashtable.put("81", "香港");     
        hashtable.put("82", "澳门");     
        hashtable.put("91", "国外");     
        return hashtable;     
    }

}
