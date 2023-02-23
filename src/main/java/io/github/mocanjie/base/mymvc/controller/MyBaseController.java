package io.github.mocanjie.base.mymvc.controller;


import io.github.mocanjie.base.mycommon.exception.BaseException;
import io.github.mocanjie.base.mymvc.data.MyResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class MyBaseController {

	public static String LOGIN_ERROR_MSG = "非法授权,请先登录";
	public static String PERMISSION_ERROR_MSG = "您没有权限，请联系管理员授权";
	public static String REQUEST_ERROR_MSG = "请求参数格式错误";
	public static String DUPLICATEKEY_ERROR_MSG = "系统已经存在该记录";

	protected @ResponseBody
	<T> MyResponseResult<T> doJsonPagerOut(T pager){
		return new MyResponseResult(pager);
	}

	protected @ResponseBody
	MyResponseResult doJsonOut(Object data){
		return new MyResponseResult(data);
	}

	protected @ResponseBody MyResponseResult doJsonOut(int code, String msg, Object data){
		return new MyResponseResult(code,msg, data);
	}

	protected @ResponseBody MyResponseResult doJsonOut(String msg, Object data){
		return new MyResponseResult(msg, data);
	}

	protected @ResponseBody MyResponseResult doJsonMsg(int code, String msg){
		return new MyResponseResult(code, msg);
	}

	protected @ResponseBody MyResponseResult doJsonMsg(String msg){
		return doJsonMsg(200,msg);
	}

	protected @ResponseBody MyResponseResult doJsonDefaultMsg(){
		return doJsonMsg(200,"操作成功");
	}


	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(BaseException.class)
	private MyResponseResult handleException(BaseException e){
		return doJsonMsg(e.getCode(), e.getMessage());
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	private MyResponseResult handleDuplicateKeyException(HttpRequestMethodNotSupportedException e){
		return doJsonMsg(HttpStatus.METHOD_NOT_ALLOWED.value(), e.getMessage());
	}
	@ExceptionHandler(HttpMessageNotReadableException.class)
	private MyResponseResult handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
		return doJsonMsg(HttpStatus.UNPROCESSABLE_ENTITY.value(), REQUEST_ERROR_MSG);
	}

	@ExceptionHandler(BindException.class)
	private MyResponseResult handleException(BindException e){
		BindingResult bindingResult = e.getBindingResult();
		return doJsonMsg(HttpStatus.BAD_REQUEST.value(), getErrorsMsg(bindingResult));
	}

	private String getErrorsMsg(BindingResult result) {
		List<String> errMsg = new LinkedList<>();
		List<FieldError> list = result.getFieldErrors();
		for (FieldError error : list) {
			ConstraintViolationImpl source = error.unwrap(ConstraintViolationImpl.class);
			String messageTemplate = source.getMessageTemplate();
			if(StringUtils.isNotBlank(messageTemplate) && messageTemplate.indexOf("{")!=-1 && messageTemplate.indexOf("}")!=-1){
				errMsg.add(String.format("%s %s",error.getField(), error.getDefaultMessage()));
			}else{
				errMsg.add(messageTemplate);
			}
		}
		return errMsg.toString();
	}

	@ExceptionHandler(ClientAbortException.class)
	private void handleException(ClientAbortException e){
	}
	
	@ExceptionHandler(Exception.class)
	private MyResponseResult handleException(Exception e){
		Throwable te = ExceptionUtils.getRootCause(e);
		if(te==null){
			List<Throwable> throwableList = ExceptionUtils.getThrowableList(e);
			te = throwableList.get(0);
		}
		if(te instanceof BaseException) {
			BaseException be = (BaseException)te;
			return doJsonMsg(be.getCode(), be.getMessage());
		}
		if(te.getClass().getName().equalsIgnoreCase("org.springframework.dao.DuplicateKeyException")){
			return doJsonMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), DUPLICATEKEY_ERROR_MSG);
		}
		if(te.getClass().getName().equalsIgnoreCase("org.springframework.security.access.AccessDeniedException")){
			return doJsonMsg(HttpStatus.FORBIDDEN.value(), PERMISSION_ERROR_MSG);
		}
		if(te.getClass().getName().equalsIgnoreCase("org.springframework.security.core.AuthenticationException")){
			return doJsonMsg(HttpStatus.FORBIDDEN.value(), PERMISSION_ERROR_MSG);
		}
		if(te instanceof HttpMediaTypeNotSupportedException){
			return doJsonMsg(HttpStatus.UNPROCESSABLE_ENTITY.value(), REQUEST_ERROR_MSG);
		}

		log.error("处理异常",e);
		return doJsonMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求失败,请稍后再试");
	}

}
