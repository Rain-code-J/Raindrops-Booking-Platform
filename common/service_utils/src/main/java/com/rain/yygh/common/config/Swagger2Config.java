package com.rain.yygh.common.config;

import com.google.common.base.Predicates;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger2配置
 *
 * @author 董俊卓
 * @date 2023/04/16
 */
@SpringBootConfiguration
@EnableSwagger2
public class Swagger2Config {
    /**
     * 得到管理员摘要
     *
     * @return {@link Docket}
     */
    @Bean
    public Docket getAdminDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Admin")
                .apiInfo(getAdminApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))
                .build();
    }

    /**
     * 获取用户摘要
     *
     * @return {@link Docket}
     */
    @Bean
    public Docket getUserDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("User")
                .apiInfo(getUserApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/user/.*")))
                .build();
    }

    /**
     * 获得api记事表
     *
     * @return {@link Docket}
     */
    @Bean
    public Docket getApiDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Api")
                .apiInfo(getApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();
    }

    /**
     * 得到管理员api信息
     *
     * @return {@link ApiInfo}
     */
    public ApiInfo getAdminApiInfo(){
        return new ApiInfoBuilder()
                .title("管理员系统")
                .description("雨滴预约挂号平台系统之管理员系统")
                .version("1.0")
//                .contact(new Contact("董俊卓","https://www.baidu.com/","junzhuo_dong@163.com"))
                .build();
    }

    /**
     * 得到用户api信息
     *
     * @return {@link ApiInfo}
     */
    public ApiInfo getUserApiInfo(){
        return new ApiInfoBuilder()
                .title("用户系统")
                .description("雨滴预约挂号平台系统之用户系统")
                .version("1.0")
//                .contact(new Contact("董俊卓","https://www.baidu.com/","junzhuo_dong@163.com"))
                .build();
    }

    /**
     * 得到api信息
     *
     * @return {@link ApiInfo}
     */
    public ApiInfo getApiInfo(){
        return new ApiInfoBuilder()
                .title("第三方医院对接系统")
                .description("雨滴预约挂号平台系统之第三方医院对接系统")
                .version("1.0")
//                .contact(new Contact("董俊卓","https://www.baidu.com/","junzhuo_dong@163.com"))
                .build();
    }
}
