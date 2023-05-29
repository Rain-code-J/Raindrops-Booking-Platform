package com.rain.yygh.sms.service;

import com.rain.yygh.vo.msm.MsmVo;


public interface SmsService {
    boolean sendCode(String phone);

    void sendMessage(MsmVo msmVo);
}
