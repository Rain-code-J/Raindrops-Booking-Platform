<template>
    <div class="app-container">
        <el-table
            :data="list"
            style="width: 100%"
            row-key="id"
            border
            lazy
            :load="getChildren"
            :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
            <el-table-column
                prop="name"
                label="名称"
                width="200">
            </el-table-column>
            <el-table-column
                prop="dictCode"
                label="编码"
                width="200">
            </el-table-column>
            <el-table-column
                prop="value"
                label="值">
            </el-table-column>
            <el-table-column
                prop="createTime"
                label="创建时间">
            </el-table-column>
        </el-table>

        <h1></h1>
        <el-button type="primary" @click="exportData">导出<i class="el-icon-download el-icon--right"></i></el-button>
        <el-button type="primary" @click="importData">导入<i class="el-icon-upload2 el-icon--right"></i></el-button>

        <el-dialog title="导入":visible.sync="dialogImportVisible"width="480px">
            <el-form label-position="right" label-width="170px">
                <el-form-item label="文件">
                    <el-upload
                        :multiple="false"
                        :on-success="onUploadSuccess"
                        :action="'http://localhost:9001/admin/cmn/dict/upload'"
                        class="upload-demo">
                        <el-button size="small" type="primary">点击上传</el-button>
                        <div slot="tip" class="el-upload__tip">只能上传xlsx文件，且不超过500kb</div>
                    </el-upload>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogImportVisible = false">取消</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import dict from '@/api/dict'
export default {
    data(){
        return {
            dialogImportVisible: false,
            list: [] // 数据字典列表
        }
    },

    created() {
        this.getDictList(1)
    },

    methods: {
        // 当导入成功时
        onUploadSuccess(){
            this.$message.success("添加成功")
            this.dialogImportVisible = false
            this.getDictList(1)
        },
        // 导入
        importData(){
            this.dialogImportVisible = true
        },
        // 导出
        exportData(){
            window.open("http://localhost:9001/admin/cmn/dict/download")
        },
        // 数据字典列表
        getDictList(id) {
            dict.dictList(id).then(resp=>{
                this.list = resp.data.dictList
            })
        },
        // 递归获取子结点列表
        // tree:当前行的信息
        // resolve:函数：把当前元素的子元素挂载到当前元素的下面
        getChildren(tree, treeNode, resolve) {
            dict.dictList(tree.id).then(response => {
                resolve(response.data.dictList)
            })
        }
    }
}
</script>
