package com.framework.stock.common.response;

/**
 * 通用请求成功返回数据
 *
 * @author lihaoqi
 * @Date 2020/11/26
 */
public class SuccessResponseData extends ServiceResult {

	private static final long serialVersionUID = 1L;

	public SuccessResponseData() {
		super(true, DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, null);
	}

	public SuccessResponseData(Object object) {
		super(true, DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, object);
	}

	public SuccessResponseData(Integer code, String message, Object object) {
		super(true, code, message, object);
	}
}
