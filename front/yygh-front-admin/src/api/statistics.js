import request from '@/utils/request'

export default {
    getCountMap(searchObj){
        return request({
            url:'/admin/statistics/getCountMap',
            method:'get',
            params:searchObj
        })
    }
}
