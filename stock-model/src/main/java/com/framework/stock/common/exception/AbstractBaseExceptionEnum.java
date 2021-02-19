package com.framework.stock.common.exception;

/**
 * 基础异常信息接口
 * 
 * @author lihaoqi
 * @Date 2020/11/26
 */
public interface AbstractBaseExceptionEnum {

	/**
	 * 获取编码
	 * 
	 * @return
	 */
	Integer getCode();

    /**
     * 获取编码信息
     * 
     * @return
     */
    String getMessage();
}