package com.framework.stock.common.aop;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.framework.stock.common.exception.ServiceException;
import com.framework.stock.common.response.ServiceResult;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 全局异常捕捉，注意这是要resf对外接口时才会触发，如果是dubbo之间的传递，还是会直接返回ServiceException异常，
 * 	所以对于服务来说，尽量不要使用抛出异常的方式，因为其他服务需要独立处理库存服务的异常
 * 	但是对于库存来说，抛出异常更大的作用是为了事务的回滚
 * @author lihaoqi
 * @date 2020年12月11日 
 *
 */
@ControllerAdvice
@Order(-1)
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler({ ServiceException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ServiceResult bussiness(ServiceException e) {
		log.error("业务异常:", e);
		return ServiceResult.error(e.getCode(), e.getMessage());
	}

	@ExceptionHandler({ BindException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ServiceResult bindException(BindException e) {
		String defaultMessage = ((ObjectError) e.getAllErrors().get(0)).getDefaultMessage();
		log.error("请求参数异常:", e);
		return ServiceResult.error(ServiceResult.DEFAULT_PARAMETER_ERROR_CODE, defaultMessage);
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ServiceResult methodArgumentNotValidException(MethodArgumentNotValidException e) {
		String defaultMessage = ((ObjectError) e.getBindingResult().getAllErrors().get(0)).getDefaultMessage();
		log.error("请求参数异常:", e);
		return ServiceResult.error(ServiceResult.DEFAULT_PARAMETER_ERROR_CODE, defaultMessage);
	}

	@ExceptionHandler({ Exception.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ServiceResult exception(Exception e) {
		String defaultMessage = "系统异常";
		log.error(defaultMessage, e);
		return ServiceResult.error(ServiceResult.DEFAULT_ERROR_CODE, defaultMessage);
	}
}
