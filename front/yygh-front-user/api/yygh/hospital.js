import request from '@/utils/request'

export default {
  getHospitalList(searchObj) {
    return request({
      url: '/user/hosp/hospital/list',
      method: 'get',
      params:searchObj
    })
  },

  findByName(name) {
    return request({
      url: `/user/hosp/hospital/${name}`,
      method: 'get'
    })
  },
  show(hoscode) {
    return request({
      url: `/user/hosp/hospital/detail/${hoscode}`,
      method: 'get'
    })
  },
  findDepartment(hoscode) {
    return request({
      url: `/user/hosp/department/all/${hoscode}`,
      method: 'get'
    })
  },
  getBookingScheduleRule(pageNum,pageSize,hoscode,depcode){
    return request({
      url: `/user/hosp/schedule/auth/getBookingScheduleRule/${pageNum}/${pageSize}/${hoscode}/${depcode}`,
      method: 'get'
    })
  },
  findScheduleList(hoscode,depcode,workDate){
    return request({
      url: `/user/hosp/schedule/auth/findScheduleList/${hoscode}/${depcode}/${workDate}`,
      method: 'get'
    })
  },
  getSchedule(id){
    return request({
      url:`/user/hosp/schedule/getSchedule/${id}`,
      method:'get'
    })
  }

}
