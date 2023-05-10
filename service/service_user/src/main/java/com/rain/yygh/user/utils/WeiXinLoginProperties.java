package com.rain.yygh.user.utils;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "wx")
@Data
@Component
public class WeiXinLoginProperties implements InitializingBean {

    private String app_id;
    private String app_secret;
    private String redirect_url;

    public static String WX_APP_ID;
    public static String WX_APP_SECRET;
    public static String WX_REDIRECT_URL;

    @Override
    public void afterPropertiesSet() throws Exception {
        WX_APP_ID = app_id;
        WX_APP_SECRET = app_secret;
        WX_REDIRECT_URL = redirect_url;
    }
}
