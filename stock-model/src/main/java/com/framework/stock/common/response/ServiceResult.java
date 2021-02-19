package com.framework.stock.common.response;

import java.io.Serializable;

import com.framework.stock.common.exception.AbstractBaseExceptionEnum;

import lombok.Data;

/**
 * 通用请求返回数据 默认返回值可选择Error与Success 请求方法：ServiceResult.success(200, "查询成功", data);
 * 
 * @author lihaoqi
 * @Date 2020/11/26
 */
@Data
public class ServiceResult implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_SUCCESS_MESSAGE = "请求成功";

	public static final String DEFAULT_ERROR_MESSAGE = "请求失败";

	public static final Integer DEFAULT_SUCCESS_CODE = 200;

	public static final Integer DEFAULT_ERROR_CODE = 500;
	
	public static final Integer DEFAULT_PARAMETER_ERROR_CODE = 400;

	private Boolean success;

	private Integer code;

	private String message;

	private Object data;

	public ServiceResult() {
	}

	public ServiceResult(Boolean success, Integer code, String message, Object data) {
		this.success = success;
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static SuccessResponseData success() {
		return new SuccessResponseData();
	}

	public static SuccessResponseData success(Object object) {
		return new SuccessResponseData(object);
	}

	public static SuccessResponseData success(Integer code, String message, Object object) {
		return new SuccessResponseData(code, message, object);
	}

	public static ErrorResponseData error(AbstractBaseExceptionEnum exception) {
		return new ErrorResponseData(exception);
	}
	
	public static ErrorResponseData error(String message) {
		return new ErrorResponseData(message);
	}

	public static ErrorResponseData error(Integer code, String message) {
		return new ErrorResponseData(code, message);
	}

	public static ErrorResponseData error(Integer code, String message, Object object) {
		return new ErrorResponseData(code, message, object);
	}
}
