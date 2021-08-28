package io.github.mocanjie.base.mymvc.controller;


import io.github.mocanjie.base.mycommon.data.DataPagerResult;
import io.github.mocanjie.base.mycommon.data.DataResult;
import io.github.mocanjie.base.mycommon.data.ResponseResult;
import io.github.mocanjie.base.mycommon.exception.BaseException;
import io.github.mocanjie.base.mycommon.pager.Pager;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
public class BaseController {

	public static String LOGIN_ERROR_MSG = "非法授权,请先登录";
	public static String PERMISSION_ERROR_MSG = "您没有权限，请联系管理员授权";
	public static String REQUEST_ERROR_MSG = "请求参数格式错误";
	public static String DUPLICATEKEY_ERROR_MSG = "系统已经存在该记录";

	protected @ResponseBody
	ResponseResult doJsonPagerOut(Pager pager){
		return new DataPagerResult(pager);
	}

	protected @ResponseBody
	ResponseResult doJsonOut(Object data){
		return new DataResult(data);
	}

	protected @ResponseBody ResponseResult doJsonOut(int code,String msg,Object data){
		return new DataResult(code,msg, data);
	}

	protected @ResponseBody ResponseResult doJsonOut(String msg,Object data){
		return new DataResult(msg, data);
	}

	protected @ResponseBody ResponseResult doJsonMsg(int code,String msg){
		return new ResponseResult(code, msg);
	}

	protected @ResponseBody ResponseResult doJsonMsg(String msg){
		return doJsonMsg(200,msg);
	}

	protected @ResponseBody ResponseResult doJsonDefaultMsg(){
		return doJsonMsg(200,"操作成功");
	}


	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(BaseException.class)
	private ResponseResult handleException(BaseException e){
		return doJsonMsg(e.getCode(), e.getMessage());
	}

	@ExceptionHandler(DuplicateKeyException.class)
	private ResponseResult handleDuplicateKeyException(DuplicateKeyException e){
		return doJsonMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), DUPLICATEKEY_ERROR_MSG);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	private ResponseResult handleDuplicateKeyException(HttpRequestMethodNotSupportedException e){
		return doJsonMsg(HttpStatus.METHOD_NOT_ALLOWED.value(), e.getMessage());
	}
	@ExceptionHandler(HttpMessageNotReadableException.class)
	private ResponseResult handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
		return doJsonMsg(HttpStatus.UNPROCESSABLE_ENTITY.value(), REQUEST_ERROR_MSG);
	}

	@ExceptionHandler(BindException.class)
	private ResponseResult handleException(BindException e){
		BindingResult br = e.getBindingResult();
		String msg = br.getFieldError().getDefaultMessage();
		return doJsonMsg(HttpStatus.EXPECTATION_FAILED.value(), msg);
	}
	
	
	@ExceptionHandler(ClientAbortException.class)
	private void handleException(ClientAbortException e){
	}
	
	@ExceptionHandler(Exception.class)
	private ResponseResult handleException(Exception e){
		Throwable te = ExceptionUtils.getRootCause(e);
		if(te instanceof BaseException) {
			BaseException be = (BaseException)te;
			return doJsonMsg(be.getCode(), be.getMessage());
		}
		log.error("处理异常",e);
		return doJsonMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求失败,请稍后再试");
	}

}