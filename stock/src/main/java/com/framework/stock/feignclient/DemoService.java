package com.framework.stock.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.framework.stock.common.config.FeignConfig;

/**
 * @Description 使用Feign默认是基于Http Restful的调用的
 * @author lihaoqi
 * @date 2020年12月13日 
 *
 */
@FeignClient(value = "other-project", configuration = FeignConfig.class) 
// 指向服务提供者应用 url="${service.url.commodity-service}",name = "CommodityStockService"
public interface DemoService {
	
	@GetMapping("/dubbo/echo")
    String echo(@RequestParam(value = "message") String message);
}
