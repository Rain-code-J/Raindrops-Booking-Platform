import request from '@/utils/request'

export default {
    getPageList(pageNum, pageSize, searchObj) {
        return request({
            url:`/administrator/userinfo/${pageNum}/${pageSize}`,
            method:'get',
            params:searchObj
        })
    },
    lock(id,status){
        return request({
            url:`/administrator/userinfo/lock/${id}/${status}`,
            method:'put'
        })
    },
    detail(id){
        return request({
            url:`/administrator/userinfo/detail/${id}`,
            method:'get'
        })
    },
    //认证审批
    approval(id, authStatus) {
        return request({
            url: `/administrator/userinfo/approval/${id}/${authStatus}`,
            method: 'put'
        })
    }
}
