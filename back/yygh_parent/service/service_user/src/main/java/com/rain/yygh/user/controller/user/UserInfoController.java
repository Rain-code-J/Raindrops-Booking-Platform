package com.rain.yygh.user.controller.user;


import com.rain.yygh.common.result.R;
import com.rain.yygh.common.utils.JwtHelper;
import com.rain.yygh.enums.AuthStatusEnum;
import com.rain.yygh.model.user.UserInfo;
import com.rain.yygh.user.service.UserInfoService;
import com.rain.yygh.vo.user.LoginVo;
import com.rain.yygh.vo.user.UserAuthVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author rain
 * @since 2023-05-01
 */
@RestController
@RequestMapping("/user/userinfo")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/login")
    public R login(@RequestBody LoginVo loginVo){
        Map<String,Object> map = userInfoService.login(loginVo);
        return R.ok().data(map);
    }

    @GetMapping("/getUserInfo")
    public R getUserInfo(@RequestHeader("token") String token){
        Long userId = JwtHelper.getUserId(token);
        UserInfo userInfo = userInfoService.getById(userId);
        Map<String,Object> map = userInfo.getParam();
        map.put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        return R.ok().data("userInfo",userInfo);
    }

    @PostMapping("/saveUserAuth")
    public R saveUserAuth(@RequestBody UserAuthVo userAuthVo,@RequestHeader("token") String token){
        Long userId = JwtHelper.getUserId(token);
        userInfoService.saveUserAuth(userId,userAuthVo);
        return R.ok();
    }
}

