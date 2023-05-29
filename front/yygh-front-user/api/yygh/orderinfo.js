import request from '@/utils/request'


export default {
  submitOrder(scheduleId, patientId) {
    return request({
      url: `/api/order/orderInfo/submitOrder/${scheduleId}/${patientId}`,
      method: 'post'
    })
  },
  getStatusList(){
    return request({
      url:`/api/order/orderInfo/auth/getStatusList`,
      method:'get'
    })
  },
  getPageList(pageNum,pageSize,searchObj){
    return request({
      url:`/api/order/orderInfo/auth/${pageNum}/${pageSize}`,
      method:'get',
      params:searchObj
    })
  },
  getOrders(id){
    return request({
      url:`/api/order/orderInfo/auth/getOrders/${id}`,
      method:'get'
    })
  }
}
