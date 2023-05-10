package com.rain.yygh.order;

import com.rain.yygh.order.prop.WeixinProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.rain.yygh.order.mapper")
@ComponentScan(basePackages = {"com.rain"}) // 将rabbit_util模块中的Config也一如了
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.rain"})
@EnableConfigurationProperties(WeixinProperties.class)
public class ServiceOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOrderApplication.class, args);
    }
}