import request from '@/utils/request'

export default {
  findList() {
    return request({
      url: `/user/userinfo/patient/findAll`,
      method: 'get'
    })
  },
  save(obj){
    return request({
      url:`/user/userinfo/patient/save`,
      method:'post',
      data:obj
    })
  },
  getById(id){
    return request({
      url:`/user/userinfo/patient/get/${id}`,
      method:'get'
    })
  },
  removeById(id){
    return request({
      url:`/user/userinfo/patient/remove/${id}`,
      method:'delete'
    })
  },
  updateById(obj){
    return request({
      url:`/user/userinfo/patient/update`,
      method:'put',
      data:obj
    })
  }
}

