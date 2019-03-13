<template>
    <div>
        <el-row style="margin-top: 50px">
            <el-col :span="16" :offset="4">
               <router-link to="/test">
                   <el-button type="primary" >test</el-button>
               </router-link>
               <router-link to="test1">
                   <el-button type="primary" >test1</el-button>
               </router-link>
                <el-row style="margin-top: 50px">
                    <el-form   label-width="150px" style="border: 1px solid #eee;padding: 15px">
                        <el-form-item lable="基本URL：">
                            <el-input placeholder="请输入前置连接地址：baseUrl" v-model="baseUrl">
                                <template slot="prepend">Http://</template>
                            </el-input>
                        </el-form-item>
                        <el-form-item label="接口地址：">
                            <el-input v-model="api" placeholder="请输入接口地址"> </el-input>
                        </el-form-item>

                    </el-form>

                    <div style="border: 1px solid #eee;padding: 15px;margin-top: 20px">
                        <el-button type="primary" @click="addNode">+新增传值</el-button>
                        <el-table :data="data">
                            <el-table-column prop="key" label="请求的键">
                                <template  slot-scope="scope">
                                    <el-input v-model="data[scope.$index].key"></el-input>
                                </template>
                            </el-table-column>
                            <el-table-column prop="val" label="对应的值">
                                <template  slot-scope="scope">
                                    <el-input v-model="data[scope.$index].val"></el-input>
                                </template>
                            </el-table-column>
                            <el-table-column prop="do" label="操作">
                                <template  slot-scope="scope">
                                    <el-button type="danger" @click="deleteRow(scope.$index)">删除</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                        <el-row >
                            <el-button type="success" @click="send" style="margin-top: 20px;float: right;margin-right: 200px">确认调用</el-button>
                        </el-row>
                    </div>
                    <div  style="border: 1px solid #eee;padding: 15px;margin-top: 20px">
                        {{jsonData}}
                    </div>
                    <div  style="border: 1px solid #eee;padding: 15px;margin-top: 20px">
                        <el-button type="primary" @click="jsonBtn">+解构JSON</el-button>
                        {{resData}}
                    </div>

                </el-row>
            </el-col>
        </el-row>
        <el-row>



        </el-row>

    </div>
</template>

<script>
    import util from 'ahaapi'

    export default {
        name: "test",
        data(){
            return{
                baseUrl:'',
                api:'',
                data:[],
                sendData:{},

                resData:'',
                jsonData:'',
            }
        },
        methods:{
            send(){
                let cnt = { }
                let that = this
                this.data.map(function (e, item) {
                    cnt[e.key] = e.val;
                });
                console.log(this.baseUrl)
                let baseUrl = 'http://'+this.baseUrl
                util.call(baseUrl+this.api, cnt, function (res) {
                    that.resData =res
                })

            },
            jsonBtn(){
              this.jsonData = JSON.parse(this.resData.data.c)
                console.log(this.jsonData)
            },
            addNode(){
                this.data.push({});
            },
            add(){
                this.data.push({});
            },
            deleteRow(index){
                this.data.splice(index,1);
            }
        }
    }
</script>

<style scoped lang="scss">

</style>
