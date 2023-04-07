package com.example.security.common.domain;

public enum ResultCode {

	/**
	 * 成功
	 */
	SUCCESS("200","成功"),

	/**
	 * 参数错误
	 */
	PARAM_ERROR("001","参数错误"),

	/**
	 * 服务器异常
	 */
	SERVER_ERROR("500","系统异常");

	String code;

	String message;

	private ResultCode(String code,String message) {
		this.code = code;
		this.code = message;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return code;
	}

	public String getMessage() {
		return message;
	}


	public boolean equalsCode(String code) {
		return this.code.equals(code);
	}

}
