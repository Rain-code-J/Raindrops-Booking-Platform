package com.rain.yygh.sms.controller;

import com.rain.yygh.common.result.R;
import com.rain.yygh.sms.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*====================================================
                时间: 2022-06-01
                讲师: 刘  辉
                出品: 尚硅谷教学团队
======================================================*/
@RestController
@RequestMapping("/user/sms")
public class SmsController {
    @Autowired
    private SmsService smsService;



    @PostMapping("/send/{phone}")
    public R sendCode(@PathVariable String phone){
        boolean flag=smsService.sendCode(phone);
        if(flag){
            return R.ok();
        }else{
            return R.error();
        }
    }
}