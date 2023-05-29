import request from '@/utils/request'

export default {
    //医院列表
    getPageList(pageNum,pageSize,searchObj) {
        return request({
            url:`/admin/hospital/${pageNum}/${pageSize}`,
            method:'get',
            params:searchObj
        })
    },
    //查询dictCode查询下级数据字典
    findByDictCode(dictCode) {

    },

    //根据id查询下级数据字典
    findByParentId(parentId) {
        return request({
            url:`/admin/cmn/dict/findChilds/${parentId}`,
            method:'get',
        })
    },
    //更新医院状态
    updateStatus(id,status){
        return request({
            url:`/admin/hospital/${id}/${status}`,
            method:'put'
        })
    },
    //获取医院详情
    getHospById(id){
        return request({
            url:`/admin/hospital/${id}`,
            method:'get'
        })
    },
    //查看医院科室
    getDeptByHoscode(hoscode){
        return request({
            url:`/admin/hosp/department/${hoscode}`,
            method:'get'
        })
    }
}
