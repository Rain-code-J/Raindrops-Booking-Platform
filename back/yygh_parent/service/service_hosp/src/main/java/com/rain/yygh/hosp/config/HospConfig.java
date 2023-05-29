package com.rain.yygh.hosp.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 医院配置类
 *
 * @author 董俊卓
 * @date 2023/04/16
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.rain.yygh.hosp.mapper")
@ComponentScan(value = "com.rain.yygh")
public class HospConfig {

}