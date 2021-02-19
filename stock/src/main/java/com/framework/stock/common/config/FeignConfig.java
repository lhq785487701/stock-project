package com.framework.stock.common.config;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Retryer;

/**
 * @Description feignConfig配置相关
 * @author lihaoqi
 * @date 2020年11月30日 
 *
 */
@Configuration
public class FeignConfig {

	/**
	 * 调用远程服务失败后，会进行重试。重试间隔为1s，最大尝试次数为5
	 * 
	 * @return
	 */
	@Bean
	public Retryer feignRetryer() {
		return new Retryer.Default(100, SECONDS.toMillis(1), 5);
	}
}
