package io.github.mocanjie.base.mymvc.data;

import lombok.Data;

/**
 * 输出类
 * @author mo
 *
 */
@Data
public class MyResponseResult<T> {
	protected int code = 200;
	protected String msg = "OK";
	protected T data;

	public MyResponseResult(T data) {
		this.data = data;
	}

	public MyResponseResult(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public MyResponseResult(int code, T data) {
		this.code = code;
		this.data = data;
	}

	public MyResponseResult(String msg, T data) {
		this.msg = msg;
		this.data = data;
	}

	public MyResponseResult(int code, String msg, T data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public MyResponseResult() {
	}
	

}
