package com.rain.yygh.order.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "weipay")
@Data
@Component
@PropertySource("classpath:weixin.properties")
public class WeixinProperties {
    private String appid;
    private String partner;
    private String partnerkey;
}
