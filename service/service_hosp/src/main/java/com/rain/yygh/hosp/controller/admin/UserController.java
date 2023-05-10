package com.rain.yygh.hosp.controller.admin;

import com.rain.yygh.common.result.R;
import com.rain.yygh.model.acl.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
public class UserController {
    /**
     * 登录
     *
     * @param user 用户
     * @return {@link R}
     */
    @PostMapping("/login")
    public R login(@RequestBody User user) {
        // 暂时不去数据库中查询
        // 做用户系统再去
        return R.ok().data("token", "admin-token");
    }

    /**
     * 获取用户信息
     *
     * @param token 令牌
     * @return {@link R}
     */
    @GetMapping("/info")
    public R info(@RequestParam("token") String token) {

        return R.ok()
                .data("roles", "[admin]")
                .data("introduction", "I am a super administrator")
                .data("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif")
                .data("name", "Super Admin");
    }
}
