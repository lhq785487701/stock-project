package com.framework.stock.common.exception;

/**
 * 服务异常
 * 
 * @author lihaoqi
 * @Date 2020/11/26
 */
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private Integer errorCode;

    private String errorMessage;
    
    public ServiceException() {
    }

    public ServiceException(Exception e) {
    	super(e);
    }
    
    public ServiceException(Integer errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ServiceException(AbstractBaseExceptionEnum exception) {
        super(exception.getMessage());
        this.errorCode = exception.getCode();
        this.errorMessage = exception.getMessage();
    }

    public Integer getCode() {
        return errorCode;
    }

    public void setCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
