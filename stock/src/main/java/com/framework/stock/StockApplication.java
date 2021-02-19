package com.framework.stock;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 此项目包括dubbo和rest的接口，主要以dubbo为主，rest为demo
 * @author lihaoqi
 * @date 2020年12月13日 
 *
 */
@SpringBootApplication
@EnableDubbo
@MapperScan("com.framework.stock.mapper*")
@EnableTransactionManagement
@Slf4j
public class StockApplication {
	
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        ConfigurableApplicationContext applicationContext = SpringApplication.run(StockApplication.class, args);
		Environment environment = applicationContext.getBean(Environment.class);
        long end = System.currentTimeMillis();
        
        log.info("启动端口号:" + environment.getProperty("local.server.port"));
        log.info("********库存服务["+StockApplication.class.getSimpleName() + "] 启动成功!耗时 "+ (end - start)/1000 + " 秒"+"********");
    }

}
