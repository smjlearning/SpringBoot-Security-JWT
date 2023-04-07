package com.example.security.common.domain;

import lombok.ToString;

import java.io.Serializable;

/**
 * 统一返回结果类
 * @param <E>
 */
@ToString
public class Result<E> implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;

	private String msg;

	private E data;

	public static <E> Result<E> success() {
		return success((E) null);
	}

	public static <E> Result<E> success(String msg) {
		Result<E> r = new Result<>();
		r.setCode(ResultCode.SUCCESS.getCode());
		r.setMsg(msg);
		return r;
	}

	public static <E> Result<E> success(E data) {
		Result<E> r = new Result<>();
		r.setCode(ResultCode.SUCCESS.getCode());
		r.setData(data);
		r.setMsg("success");
		return r;
	}

	public static <E> Result<E> success(String msg, E data) {
		Result<E> r = new Result<>();
		r.setCode(ResultCode.SUCCESS.getCode());
		r.setData(data);
		r.setMsg(msg);
		return r;
	}

	public static <E> Result<E> fail(String code, String msg) {
		Result<E> r = new Result<>();
		r.setCode(code);
		r.setMsg(msg);
		return r;
	}

	public static <E> Result<E> fail(String code, String msg, E data) {
		Result<E> r = new Result<>();
		r.setCode(code);
		r.setMsg(msg);
		r.setData(data);
		return r;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public E getData() {
		return data;
	}

	public void setData(E data) {
		this.data = data;
	}



	public boolean ok() {
		if (code == null || code.length() == 0) {
			return false;
		}
		if (code.equals(ResultCode.SUCCESS.getCode())) {
			return true;
		}
		return false;
	}

	private Result() {

	}

}
