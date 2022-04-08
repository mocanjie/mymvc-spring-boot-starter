package io.github.mocanjie.base.mymvc.data;

import lombok.Data;

/**
 * 输出类
 * @author mo
 *
 */
@Data
public class ResponseResult<T> {
	protected int code = 200;
	protected String msg = "OK";
	protected T data;

	public ResponseResult(T data) {
		this.data = data;
	}

	public ResponseResult(int code,String msg) {
		this.code = code;
		this.msg = msg;
	}

	public ResponseResult(int code,T data) {
		this.code = code;
		this.data = data;
	}

	public ResponseResult(String msg,T data) {
		this.msg = msg;
		this.data = data;
	}

	public ResponseResult(int code, String msg,T data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public ResponseResult() {
	}
	

}
