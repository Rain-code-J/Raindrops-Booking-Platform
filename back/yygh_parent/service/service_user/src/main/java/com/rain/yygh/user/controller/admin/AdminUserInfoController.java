package com.rain.yygh.user.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rain.yygh.common.result.R;
import com.rain.yygh.model.user.UserInfo;
import com.rain.yygh.user.service.UserInfoService;
import com.rain.yygh.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/administrator/userinfo")
public class AdminUserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/{pageNum}/{pageSize}")
    public R getUserInfoPage(@PathVariable("pageNum") Integer pageNum,
                             @PathVariable("pageSize") Integer pageSize,
                             UserInfoQueryVo userInfoQueryVo){
        Page<UserInfo> page = userInfoService.getUserInfoPage(pageNum,pageSize,userInfoQueryVo);
        return R.ok()
                .data("total",page.getTotal())
                .data("list",page.getRecords());
    }

    @PutMapping("/lock/{id}/{status}")
    public R lock(@PathVariable("id") Long id,
                  @PathVariable("status") Integer status){
        userInfoService.lock(id,status);
        return R.ok();
    }

    @GetMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        Map<String,Object> map = userInfoService.detail(id);
        return R.ok().data(map);
    }

    //认证审批
    @PutMapping("approval/{userId}/{authStatus}")
    public R approval(@PathVariable("userId") Long userId,
                      @PathVariable("authStatus") Integer authStatus){
        userInfoService.approval(userId,authStatus);
        return R.ok();
    }

}
