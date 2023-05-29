package com.rain.yygh.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rain.yygh.model.user.UserInfo;
import com.rain.yygh.vo.user.LoginVo;
import com.rain.yygh.vo.user.UserAuthVo;
import com.rain.yygh.vo.user.UserInfoQueryVo;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author rain
 * @since 2023-05-01
 */
public interface UserInfoService extends IService<UserInfo> {

    Map<String, Object> login(LoginVo loginVo);

    Map<String, Object> getLoginParam();


    String callback(String code, String state);

    void saveUserAuth(Long userId, UserAuthVo userAuthVo);

    Page<UserInfo> getUserInfoPage(Integer pageNum, Integer pageSize, UserInfoQueryVo userInfoQueryVo);


    void lock(Long id, Integer status);

    Map<String, Object> detail(Long id);

    void approval(Long userId, Integer authStatus);
}
