<template>
    <div class="app-container">
        <el-form :inline="true" :model="searchObj" class="demo-form-inline">
            <el-form-item label="医院名称">
                <el-input v-model="searchObj.hosname" placeholder="医院名称"></el-input>
            </el-form-item>
            <el-form-item label="医院编号">
                <el-input v-model="searchObj.hoscode" placeholder="医院编号"></el-input>
            </el-form-item>
            <el-button type="primary" @click="query()">查询</el-button>
            <el-button type="primary" @click="empty()">清空</el-button>
        </el-form>

        <el-table
            v-loading="listLoading"
            :data="list"
            element-loading-text="数据加载中"
            border
            fit
            highlight-current-row @selection-change="handleSelectionChange">

            <el-table-column
                type="selection"
                width="55">
            </el-table-column>

            <el-table-column
                label="序号"
                width="70"
                align="center">
                <template slot-scope="scope">
                    {{ (page - 1) * limit + scope.$index + 1 }}
                </template>
            </el-table-column>

            <el-table-column prop="hosname" label="医院名称" width="180" />

            <el-table-column prop="hoscode" label="医院编号" width="120" />

            <el-table-column prop="apiUrl" label="地址" width="200"/>

            <el-table-column prop="contactsName" label="联系人" width="140"/>

            <el-table-column prop="status" label="状态">
                <template slot-scope="scope">
                    {{ scope.row.status===1?'可用':'不可用' }}
                </template>
            </el-table-column>

            <el-table-column label="操作" width="300" align="center">
                <template slot-scope="scope">
                    <router-link :to="'/yygh/hospset/edit/'+scope.row.id">
                        <el-button type="primary" size="mini" icon="el-icon-edit">修改</el-button>
                    </router-link>
                    <el-button type="danger" size="mini" icon="el-icon-delete" @click="removeDataById(scope.row.id)">删除</el-button>
                    <el-button v-if="scope.row.status==1" type="primary" size="mini" icon="el-icon-delete" @click="lockHostSet(scope.row.id,0)">锁定</el-button>
                    <el-button v-if="scope.row.status==0" type="danger" size="mini"  icon="el-icon-delete" @click="lockHostSet(scope.row.id,1)">解锁</el-button>
                </template>

            </el-table-column>
        </el-table>
        <h1></h1>
        <el-button type="danger" @click="batchDelete()">批量删除</el-button>

        <el-pagination
            @current-change="handleCurrentChange"

            :page-size="limit"
            layout="total,  prev, pager, next, jumper"
            :total="total">
        </el-pagination>
    </div>
</template>
<script>
import hospset  from '@/api/hospset.js'
export default {
    data(){
        return {
            //变量：值
            list:[],
            total:0,
            searchObj:{},
            listLoading:false,
            page:1,
            limit:3,
            selection:[]
        }
    },
    methods:{
        lockHostSet(id,status){
            hospset.updateStatus(id,status).then(resp=>{
                this.getPageInfo()
            })
        },
        batchDelete(){
            this.$confirm('此操作将永久删除这些该文件, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(resp=> {
                var ids = [];
                for (let i = 0; i < this.selection.length; i++) {
                    var obj = this.selection[i];
                    ids.push(obj.id);
                }
                hospset.batchDelete(ids).then(resp=>{
                    this.$message.success("批量删除成功")
                    this.getPageInfo();
                });
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },
        handleSelectionChange(selection){
            this.selection = selection
        },
        removeDataById(id){
            this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                hospset.removeDataById(id).then(resp=>{
                    this.$message({
                        type:"success",
                        message:"删除成功"
                    }),
                        this.getPageInfo();
                })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });

        },
        empty(){
            this.searchObj={};
            this.getPageInfo()
        },
        query(){
            this.getPageInfo()
        },
        getPageInfo(val=1){
            this.page=val;
            this.listLoading=true;
            hospset.getHospsetPage(this.page,this.limit,this.searchObj).then(resp=>{
                this.total=resp.data.total;
                this.list=resp.data.rows;
                this.listLoading=false
            })
        },
        handleCurrentChange(val){
            this.getPageInfo(val)
        },


    },
    created(){
        this.getPageInfo()
    }

}
</script>

