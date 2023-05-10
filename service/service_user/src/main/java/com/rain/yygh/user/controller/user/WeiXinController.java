package com.rain.yygh.user.controller.user;

import com.rain.yygh.common.result.R;
import com.rain.yygh.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/user/userinfo/wx")
public class WeiXinController {

    @Autowired
    private UserInfoService userInfoService;


    @GetMapping("/getLoginParam")
    @ResponseBody
    public R getLoginParam(){
        Map<String,Object> map = userInfoService.getLoginParam();
        return R.ok().data(map);
    }

    @GetMapping("/callback")
    public String callback(String code,String state){
        return userInfoService.callback(code,state);
    }

}
