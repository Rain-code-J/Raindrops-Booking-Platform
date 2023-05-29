import request from '@/utils/request'

const API="/admin/cmn/dict";
//es6导出语法
export default {
    // 数据字典列表
    dictList(parentId){
        return request({
            url:`${API}/findChilds/${parentId}`,
            method:'get'
        })
    }
}
