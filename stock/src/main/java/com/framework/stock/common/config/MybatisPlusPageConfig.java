package com.framework.stock.common.config;

/**
 * @Description: mybatis-plus分页配置
 * @author: lihaoqi
 * @date: 2020/11/11 16:18
 * @version: v1.0.0
 */

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ConditionalOnClass(value = {PaginationInterceptor.class})
@EnableTransactionManagement
public class MybatisPlusPageConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
