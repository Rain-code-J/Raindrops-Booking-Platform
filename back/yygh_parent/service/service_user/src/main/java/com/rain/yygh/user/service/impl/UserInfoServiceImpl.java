package com.rain.yygh.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rain.yygh.common.exception.YyghException;
import com.rain.yygh.common.utils.JwtHelper;
import com.rain.yygh.enums.AuthStatusEnum;
import com.rain.yygh.enums.StatusEnum;
import com.rain.yygh.model.user.Patient;
import com.rain.yygh.model.user.UserInfo;
import com.rain.yygh.user.mapper.UserInfoMapper;
import com.rain.yygh.user.service.PatientService;
import com.rain.yygh.user.service.UserInfoService;
import com.rain.yygh.user.utils.HttpClientUtils;
import com.rain.yygh.user.utils.WeiXinLoginProperties;
import com.rain.yygh.vo.user.LoginVo;
import com.rain.yygh.vo.user.UserAuthVo;
import com.rain.yygh.vo.user.UserInfoQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author rain
 * @since 2023-05-01
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private PatientService patientService;

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        Map<String, Object> map = new HashMap<>();
        // 1.获取手机号、验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        String openid = loginVo.getOpenid();
        // 2.判空
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(20001, "手机号或者验证码为空");
        }
        // 对验证码进行校验
        String redisCode = (String) redisTemplate.opsForValue().get(phone);
        if(StringUtils.isEmpty(redisCode) || !redisCode.equals(code)){
            throw new YyghException(20001,"验证码有误");
        }
        // 如果openid为空，说明是手机号登录
        UserInfo userInfo = null;
        if (StringUtils.isEmpty(openid)){
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", phone);
            userInfo = baseMapper.selectOne(queryWrapper);
            if (userInfo == null) {
                userInfo = new UserInfo();
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                userInfo.setName("");
                baseMapper.insert(userInfo);
            }
            // 4.查看是否被禁用
            if (userInfo.getStatus() == 0) {
                throw new YyghException(20001, "该账号已被禁用");
            }
            map = getMap(userInfo);
            return map;
        } else {
            // 说明是微信登录,并且是以前用手机号登录过，然后又进行了微信登录
            QueryWrapper<UserInfo> queryWrapperTogether = new QueryWrapper<>();
            queryWrapperTogether.eq("openid",openid).eq("phone",phone);
            UserInfo userInfoTogether = baseMapper.selectOne(queryWrapperTogether);
            if (userInfoTogether != null){
                map = getMap(userInfoTogether);
                return map;
            }

            UserInfo userInfoFinal = new UserInfo();
            // 先查手机号的信息
            UserInfo userInfoPhone = new UserInfo();
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", phone);
            userInfoPhone = baseMapper.selectOne(queryWrapper);

            if (userInfoPhone != null){
                BeanUtils.copyProperties(userInfoPhone,userInfoFinal);
                baseMapper.deleteById(userInfoPhone.getId());
            }

            // 查询微信信息
            QueryWrapper<UserInfo> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("openid", openid);
            UserInfo userInfoWx = baseMapper.selectOne(queryWrapper1);

            //4 把微信信息封装userInfoFinal
            userInfoFinal.setOpenid(userInfoWx.getOpenid());
            userInfoFinal.setNickName(userInfoWx.getNickName());
            userInfoFinal.setId(userInfoWx.getId());

            // 如果没有手机号的数据
            if (userInfoPhone == null){
                userInfoFinal.setPhone(phone);
                userInfoFinal.setStatus(userInfoWx.getStatus());
            }


            baseMapper.updateById(userInfoFinal);
            // 4.查看是否被禁用
            if (userInfoFinal.getStatus() == 0) {
                throw new YyghException(20001, "该账号已被禁用");
            }
            map = getMap(userInfoFinal);
            return map;
        }
    }

    private Map<String,Object> getMap(UserInfo userInfo){
        //返回页面显示名称
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        //根据userid和name生成token字符串
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);
        return map;
    }

    @Override
    public Map<String, Object> getLoginParam() {
        String redirectUrl = null;
        try {
            redirectUrl = URLEncoder.encode(WeiXinLoginProperties.WX_REDIRECT_URL, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String,Object> map = new HashMap<>();
        map.put("appid", WeiXinLoginProperties.WX_APP_ID);
        map.put("redirectUri",redirectUrl);
        map.put("scope", "snsapi_login");
        map.put("state", System.currentTimeMillis()+"");
        return map;
    }

    @Override
    public String callback(String code, String state) {
        // 通过code获取access_token的接口。
        // http请求方式: GET
        // https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
        StringBuffer baseAccessToken = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token?")
                .append("appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        // 拼接了通过code获取access_token的地址
        String accessTokenUrl = String.format(baseAccessToken.toString(), WeiXinLoginProperties.WX_APP_ID, WeiXinLoginProperties.WX_APP_SECRET, code);
        try {
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);
            JSONObject accessTokenJSONObject = JSONObject.parseObject(accessTokenInfo);
            String access_token = accessTokenJSONObject.getString("access_token");
            String openid = accessTokenJSONObject.getString("openid");
//            System.out.println(accessTokenInfo);

            // 判断数据库中是否有该微信用户信息
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("openid",openid);
            UserInfo userInfo = baseMapper.selectOne(queryWrapper);
            // 如果查询出来的userinfo为null，说明数据库中没有此用户信息，则新建一个用户，并保存在数据库中去
            if (userInfo == null){
                // 此接口用于获取用户个人信息
                // http请求方式: GET
                // https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
                
                StringBuffer baseWeiXinUserInfoUrl = new StringBuffer()
                        .append("https://api.weixin.qq.com/sns/userinfo?")
                        .append("access_token=%s")
                        .append("&openid=%s");
                String weiXinUserInfoUrl = String.format(baseWeiXinUserInfoUrl.toString(), access_token, openid);
                String weiXinUserInfo = HttpClientUtils.get(weiXinUserInfoUrl);
                System.out.println(weiXinUserInfo);
                JSONObject weiXinUserInfoJSONObject = JSONObject.parseObject(weiXinUserInfo);
                //解析用户信息
                //用户昵称
                String nickname = weiXinUserInfoJSONObject.getString("nickname");

                userInfo = new UserInfo();
                userInfo.setOpenid(openid);
                userInfo.setStatus(1);
                userInfo.setNickName(nickname);

                // 保存到数据库中去
                baseMapper.insert(userInfo);
            }

            // 构建返回map
            Map<String,String> map = new HashMap<>();

            //判断userInfo是否有手机号，如果手机号为空，返回openid
            //如果手机号不为空，返回openid值是空字符串
            //前端判断：如果openid不为空，绑定手机号，如果openid为空，不需要绑定手机号
            // 如果查询出来的userinfo电话为空，让其强制绑定手机号
            if (StringUtils.isEmpty(userInfo.getPhone())){
                map.put("openid",openid);
            } else {
                map.put("openid","");
            }
            // 4.查看是否被禁用
            if (userInfo.getStatus() == 0) {
                throw new YyghException(20001, "该账号已被禁用");
            }
            // 5.返回页面显示名称
            String name = userInfo.getName();
            if (StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if (StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }

            map.put("name", name);
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);

            return "redirect:http://localhost:3000/weixin/callback?token="+map.get("token")+ "&openid="+map.get("openid")+"&name="+URLEncoder.encode(map.get("name"),"utf-8");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveUserAuth(Long userId, UserAuthVo userAuthVo) {
        UserInfo userInfo = baseMapper.selectById(userId);
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());

        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());

        baseMapper.updateById(userInfo);
    }

    @Override
    public Page<UserInfo> getUserInfoPage(Integer pageNum, Integer pageSize, UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> page = new Page<>(pageNum,pageSize);
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        String keyword = userInfoQueryVo.getKeyword();
        Integer status = userInfoQueryVo.getStatus();
        Integer authStatus = userInfoQueryVo.getAuthStatus();
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin();
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd();

        if (!StringUtils.isEmpty(keyword)){
            queryWrapper.like("name",keyword).or().eq("phone",keyword);
        }
        if (!StringUtils.isEmpty(status)){
            queryWrapper.eq("status",status);
        }
        if (!StringUtils.isEmpty(authStatus)){
            queryWrapper.eq("auth_status",authStatus);
        }
        if (!StringUtils.isEmpty(createTimeBegin)){
            queryWrapper.ge("create_time",createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)){
            queryWrapper.le("create_time",createTimeEnd);
        }
        Page<UserInfo> page1 = baseMapper.selectPage(page, queryWrapper);
        page1.getRecords().stream().forEach(item->{
            this.packageUserInfo(item);
        });

        return page1;
    }

    @Override
    public void lock(Long id, Integer status) {
        if (status.intValue() == 0 || status.intValue() == 1){
            UserInfo userInfo = baseMapper.selectById(id);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }

    @Override
    public Map<String, Object> detail(Long id) {
        Map<String,Object> map = new HashMap<>();

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",id);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        this.packageUserInfo(userInfo);

        QueryWrapper<Patient> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("user_id",id);
        List<Patient> patients = patientService.list(queryWrapper1);

        map.put("userInfo",userInfo);
        map.put("patients",patients);
        return map;
    }

    @Override
    public void approval(Long userId, Integer authStatus) {
        if (authStatus == 2 || authStatus == -1){
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }

    private void packageUserInfo(UserInfo userInfo) {
        Map<String, Object> map = userInfo.getParam();
        String authStatusString = AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus());
        String statusString = StatusEnum.getStatusNameByStatus(userInfo.getStatus());
        map.put("authStatusString",authStatusString);
        map.put("statusString",statusString);
    }
}
