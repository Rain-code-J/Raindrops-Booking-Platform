import request from '@/utils/request'


export default {
  sendCode(phone) {
    return request({
      url: `/user/sms/send/${phone}`,
      method: `post`
    })
  }
}
