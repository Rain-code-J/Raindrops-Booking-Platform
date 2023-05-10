package com.rain.yygh.sms.service.impl;

import com.rain.yygh.sms.service.SmsService;
import com.rain.yygh.sms.utils.HttpUtils;
import com.rain.yygh.sms.utils.RandomUtil;
import com.rain.yygh.vo.msm.MsmVo;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*====================================================
                时间: 2022-06-01
                讲师: 刘  辉
                出品: 尚硅谷教学团队
======================================================*/
@Service
public class SmsServiceImpl  implements SmsService {


    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean sendCode(String phone) {
        if (StringUtils.isEmpty(phone)){
            return false;
        }

        String redisCode = (String) redisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(redisCode)){
            return true;
        }

        String host = "https://jmsms.market.alicloudapi.com";
        String path = "/sms/send";
        String method = "POST";
        String appcode = "88a479b96f034d3dbd6644da429eb87b";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phone);
        querys.put("templateId", "JM1000372");
        String fourBitRandom = RandomUtil.getFourBitRandom();
        querys.put("value", fourBitRandom);
        Map<String, String> bodys = new HashMap<String, String>();


        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
            System.out.println(fourBitRandom);

            // 在redis中放30天，用来进行测试
            redisTemplate.opsForValue().set(phone,fourBitRandom,30, TimeUnit.DAYS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public void sendMessage(MsmVo msmVo) {
        String phone = msmVo.getPhone();
        // 具体业务：略
        // 模板---模板参数
        System.out.println("给就诊人发送短信成功");
    }

}
