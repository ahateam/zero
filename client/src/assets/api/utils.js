import util from 'ahaapi'


// const baseUrl = 'http://47.99.212.32/api/jiti';  //正式服请求地址前缀


// const baseUrl = 'http://192.168.1.182:8080/jiti';  //本地请求地址前缀
// const baseUrl = 'http://47.99.209.235/api/jiti';  //测试服请求地址前缀


// let appId = process.env.appID;//应用编号
// let appId ='';

let api = {};
console.log('开始调用ctrl');



//机构列表
api.getORGs = function (cnt,callback) {
    util.call(baseUrl+'/org/getORGs', cnt, callback)
}

//新增机构
api.createORG = function (cnt,callback) {
    util.call(baseUrl+'/org/createORG', cnt, callback)
}
//修改机构的资金信息
api.editORGExt = function (cnt,callback) {
    util.call(baseUrl+'/org/editORGExt', cnt, callback)
}
//创建组织用户
api.createORGUser = function (cnt,callback) {
    util.call(baseUrl+'/org/createORGUser', cnt, callback)
}
//修改用户基本信息
api.editUser = function (cnt,callback) {
    util.call(baseUrl+'/org/editUser', cnt, callback)
}
//修改组织用户信息-职务修改
api.editORGUser = function (cnt,callback) {
    util.call(baseUrl+'/org/editORGUser', cnt, callback)
}
//导入用户表
api.importORGUsers = function (cnt,callback) {
    util.call(baseUrl+'/org/importORGUsers', cnt, callback)
}
//获取组织成员列表
api.getORGUserByRole = function (cnt,callback) {
    util.call(baseUrl+'/org/getORGUserByRole', cnt, callback)
}
//用户移出组织
api.delORGUser = function (cnt,callback) {
    util.call(baseUrl+'/org/delORGUser', cnt, callback)
}

//根据组织编号和身份证号片段（生日），模糊查询
api.getORGUsersLikeIDNumber = function (cnt,callback) {
    util.call(baseUrl+'/org/getORGUsersLikeIDNumber', cnt, callback)
}

//获取投票的选项列表
api.getVoteOptions = function (cnt,callback) {
    util.call(baseUrl+'/vote/getVoteOptions', cnt, callback)
}
//获取组织的投票项目
api.getVoteProjectsByOrgId = function (cnt,callback) {
    util.call(baseUrl+'/vote/getVoteProjectsByOrgId', cnt, callback)
}
//创建投票
api.addVote = function (cnt,callback) {
    util.call(baseUrl+'/vote/addVote', cnt, callback)
}
//获取投票项目中的投票
api.getVotes = function (cnt,callback) {
    util.call(baseUrl+'/vote/getVotes', cnt, callback)
}
//<创建投票项目
api.createVoteProject = function (cnt,callback) {
    util.call(baseUrl+'/vote/createVoteProject', cnt, callback)
}
//获取投票的选项列表
api.getVoteOptions = function (cnt,callback) {
    util.call(baseUrl+'/vote/getVoteOptions', cnt, callback)
}
//删除投票的选项
api.delVoteOption = function (cnt,callback) {
    util.call(baseUrl+'/vote/delVoteOption', cnt, callback)
}
//新增投票的选项
api.addVoteOption = function (cnt,callback) {
    util.call(baseUrl+'/vote/addVoteOption', cnt, callback)
}
//修改编辑投票
api.editVote = function (cnt,callback) {
    util.call(baseUrl+'/vote/editVote', cnt, callback)
}
//删除投票项目
api.delVote = function (cnt,callback) {
    util.call(baseUrl+'/vote/delVote', cnt, callback)
}
//启用/禁用投票
api.setVoteActivation = function (cnt,callback) {
    util.call(baseUrl+'/vote/setVoteActivation', cnt, callback)
}
//修改编辑编辑投票项目
api.editVoteProject = function (cnt,callback) {
    util.call(baseUrl+'/vote/editVoteProject', cnt, callback)
}

//获取资产列表
api.getAssets = function (cnt,callback) {
    util.call(baseUrl+'/asset/getAssets', cnt, callback)
}

//导入资产列表
api.importAssets = function (cnt,callback) {
    util.call(baseUrl+'/asset/importAssets', cnt, callback)
}



/*重构之后的接口*/
//
api.createVote = function (cnt,callback) {
    util.call(baseUrl+'/vote/createVote', cnt, callback)
}

//用户相关
//注册平台账号
api.registeUser = function (cnt,callback) {
    util.call(baseUrl+'/org/registeUser', cnt, callback)
}
//管理员登录
api.loginByMobileAndPwd = function (cnt,callback) {
    util.call(baseUrl+'/org/loginByMobileAndPwd', cnt, callback)
}
//用户管理员账号的机构列表
api.getUserORGs = function (cnt,callback) {
    util.call(baseUrl+'/org/getUserORGs', cnt, callback)
}
//管理员选取组织的登录
api.adminLoginInORG = function (cnt,callback) {
    util.call(baseUrl+'/org/adminLoginInORG', cnt, callback)
}


//请求系统角色列表
api.getSysORGUserRoles = function (cnt,callback) {
    util.call(baseUrl+'/org/getSysORGUserRoles', cnt, callback)
}

//请求分组信息
api.getORGUserSysTagGroups	 = function (cnt,callback) {
    util.call(baseUrl+'/org/getORGUserSysTagGroups', cnt, callback)
}

//请求分组信息
api.getTagGroupTree = function (cnt,callback) {
    util.call(baseUrl+'/org/getTagGroupTree', cnt, callback)
}

//新增分组
api.createORGUserTagGroup = function (cnt,callback) {
    util.call(baseUrl+'/org/createORGUserTagGroup', cnt, callback)
}
//根据标签信息获取组织成员列表
api.getORGUsersByTags = function (cnt,callback) {
    util.call(baseUrl+'/org/getORGUsersByTags', cnt, callback)
}
//获取组织类所有的用户信息
api.getORGUsers = function (cnt,callback) {
    util.call(baseUrl+'/org/getORGUsers', cnt, callback)
}
//根据分组获取用户列表
api.getORGUsersByGroups = function (cnt,callback) {
    util.call(baseUrl+'/org/getORGUsersByGroups', cnt, callback)
}
//将用户批量新增到某一分组
api.batchEditORGUsersGroups = function (cnt,callback) {
    util.call(baseUrl+'/org/batchEditORGUsersGroups', cnt, callback)
}
//根据姓名模糊搜索用户列表
api.getORGUsersLikeRealName = function (cnt,callback) {
    util.call(baseUrl+'/org/getORGUsersLikeRealName', cnt, callback)
}


//资产相关
//根据条件查询--对应资产列表
api.queryAssets = function (cnt,callback) {
    util.call(baseUrl+'/asset/queryAssets', cnt, callback)
}
//根据分组信息获取资产列表
api.getAssetsByGroups = function (cnt,callback) {
    util.call(baseUrl+'/asset/getAssetsByGroups', cnt, callback)
}
//删除资产
api.delAsset = function (cnt,callback) {
    util.call(baseUrl+'/asset/delAsset', cnt, callback)
}
//移入资产
api.batchEditAssetsGroups = function (cnt,callback) {
    util.call(baseUrl+'/asset/batchEditAssetsGroups', cnt, callback)
}


export default api
