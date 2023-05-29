import request from '@/utils/request'

export default {
  getChildList(parentId) {
    return request({
      url: `/admin/cmn/dict/findChilds/${parentId}`,
      method: 'get'
    })
  }
}

