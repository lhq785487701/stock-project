package com.framework.stock.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.framework.stock.common.exception.ServiceException;
import com.framework.stock.common.response.ServiceResult;
import com.framework.stock.exception.StockServiceExceptionEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 访问拦截器 业务类抛出异常，切面处理异常返回ServiceResult,该接口优先于dubboExceptionFilter
 * 	注意@Order(-1)保证比事务优先
 * @author: lihaoqi
 * @date: 2019年4月19日
 * @version: 1.0.0
 *
 */
@Component
@Aspect
@Slf4j
@Order(-1)
public class DubboApiAopHandler {
	private final String POINT_CUT = "execution(* com.framework.stock.api.*.*(..))";

	@Pointcut(POINT_CUT)
	public void pointCut() {
	}

	/**
	 * @Description: 环绕通知： 注意:Spring AOP的环绕通知会影响到AfterThrowing通知的运行,不要同时使用
	 *
	 * @param proceedingJoinPoint
	 * @return
	 */
	@Around(value = POINT_CUT)
	public ServiceResult doAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) {
		log.debug("︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻▶▷►◈华丽的分割线◈◄◀◁︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻︻");
		log.debug("dubbo执行开始：{}", proceedingJoinPoint.getSignature().toString());
		Object obj;

		try {
			obj = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
			log.debug("方法正常执行结束，返回值：{}", obj);
			return (ServiceResult) obj;
		} catch (Throwable ex) {
			log.error("方法调用异常：{}", ex.getMessage());
			if (ex instanceof ServiceException) {
				return ServiceResult.error(((ServiceException) ex).getCode(), ex.getMessage());
			}
			return ServiceResult.error(StockServiceExceptionEnum.CODE_500);
		} finally {
			log.debug("︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼▶▷►◈华丽的分割线◈◄◀◁︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼︼\n");
		}
	}
}
