<template>
    <div>
        <el-row style="margin-top: 50px">

            <el-col :span="16" :offset="4">

                    <div style="border: 1px solid #eee;padding: 15px;margin-top: 20px">
                        <router-link to="/test">
                            <el-button type="primary" >test</el-button>
                        </router-link>
                        <router-link to="test1">
                            <el-button type="primary" >test1</el-button>
                        </router-link>
                        <el-row style="margin-top: 50px">
                            <el-button type="success" @click="send" >开始调用</el-button>
                        </el-row>
                    </div>
                    <div  style="border: 1px solid #eee;padding: 15px;margin-top: 20px">
                        {{jsonData}}
                    </div>
                    <div  style="border: 1px solid #eee;padding: 15px;margin-top: 20px">
                        <el-button type="primary" @click="jsonBtn">+解构JSON</el-button>
                        {{resData}}
                    </div>
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
                let that = this
                let baseUrl = 'http://47.99.209.235/api/jiti/org/getORGUsersLikeRealName'
                let cnt = {
                    count: 10,
                    offset: 0,
                    orgId: "397340015130557",
                    realName: "郭"
                }

                util.call(baseUrl,cnt, function (res) {
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
