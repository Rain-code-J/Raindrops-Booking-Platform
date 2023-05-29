import request from '@/utils/request'

export default {
  getLoginParam(){
    return request({
      url:`/user/userinfo/wx/getLoginParam`,
      method:'get'
    })
  },
  createNative(orderId) {
    return request({
      url: `/user/order/weixin/createNative/${orderId}`,
      method: 'get'
    })
  },
  queryPayStatus(orderId){
    return request({
      url:`/user/order/weixin/queryPayStatus/${orderId}`,
      method:'get'
    })
  },
  cancelOrder(orderId) {
    return request({
      url: `/api/order/orderInfo/auth/cancelOrder/${orderId}`,
      method: 'get'
    })
  },

}
