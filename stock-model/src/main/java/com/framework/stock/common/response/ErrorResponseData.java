package com.framework.stock.common.response;

import com.framework.stock.common.exception.AbstractBaseExceptionEnum;

/**
 * 通用请求失败返回数据
 *
 * @author lihaoqi
 * @Date 2020/11/26
 */
public class ErrorResponseData extends ServiceResult {

	private static final long serialVersionUID = 1L;

	public ErrorResponseData(AbstractBaseExceptionEnum exception) {
        super(false, exception.getCode(), exception.getMessage(), null);
    }

	public ErrorResponseData(String message) {
        super(false, ServiceResult.DEFAULT_ERROR_CODE, message, null);
    }

    public ErrorResponseData(Integer code, String message) {
        super(false, code, message, null);
    }

    public ErrorResponseData(Integer code, String message, Object object) {
        super(false, code, message, object);
    }
}
