package com.rain.yygh.sms.service;

import com.rain.yygh.vo.msm.MsmVo;

/*************************************************
 时间: 2022-06-01
 讲师: 刘  辉
 出品: 尚硅谷教学团队
 **************************************************/
public interface SmsService {
    boolean sendCode(String phone);

    void sendMessage(MsmVo msmVo);
}
