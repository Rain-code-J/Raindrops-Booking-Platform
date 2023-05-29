package com.rain.yygh.common.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 分页配置
 *
 * @author 董俊卓
 * @date 2023/04/16
 */
@SpringBootConfiguration
public class PageConfig {

    /**
     * 得到分页拦截器
     *
     * @return {@link PaginationInterceptor}
     */
    @Bean
    public PaginationInterceptor getPaginationInterceptor(){
        return new PaginationInterceptor();
    }
}
