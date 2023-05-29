import request from '@/utils/request'

export default {
  // 登录
  login(userInfo) {
    return request({
      url: '/user/userinfo/login',
      method: 'post',
      data:userInfo
    })
  },
  // 获取用户信息
  getUserInfo(){
    return request({
      url:`/user/userinfo/getUserInfo`,
      method:'get'
    })
  },
  // 保存用户认证信息
  saveUserAuah(obj){
    return request({
      url:`/user/userinfo/saveUserAuth`,
      method:'post',
      data:obj
    })
  }

}
