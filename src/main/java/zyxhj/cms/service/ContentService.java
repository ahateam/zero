package zyxhj.cms.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.cms.domian.Content;
import zyxhj.cms.repository.ContentRepository;
import zyxhj.core.domain.User;
import zyxhj.core.service.UserService;
import zyxhj.utils.IDUtils;
import zyxhj.utils.ServiceUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;

public class ContentService extends Controller{

	private static Logger log = LoggerFactory.getLogger(ContentService.class);
	private DruidDataSource ds;
	private UserService userService;
	private ContentRepository contentRepository;

	public ContentService(String node) {
		super(node);
		try {
			ds = DataSource.getDruidDataSource("rdsDefault.prop");
			contentRepository = Singleton.ins(ContentRepository.class);
			userService = Singleton.ins(UserService.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@POSTAPI(
		path = "addContent", //
		des = "创建内容", //
		ret = "所创建的对象"//
	)
	public Content addContent(
		@P(t = "模块编号") String module, //
		@P(t = "内容类型") Byte type, //
		@P(t = "状态") Byte status, //
		@P(t = "权力:付费，会员等") Byte power, //
		@P(t = "上传用户编号") Long upUserId, //
		@P(t = "上传专栏编号", r = false) Long upChannelId, //
		@P(t = "标签" , r = false) JSONObject tags, //
		@P(t = "标题") String title, //
		@P(t = "数据") String data, //
		@P(t = "私密信息",r = false) String proviteData, //
		@P(t = "扩展信息",r = false) String ext, //
		@P(t = "活动开始时间",r = false) Date activityStart, //
		@P(t = "活动结束时间",r = false) Date activityEnd //
	)
	throws Exception {
		Content c = new Content();
		c.orgModule = module;
		c.id = IDUtils.getSimpleId();
		c.createTime = new Date();
		c.updateTime = c.createTime;
		c.type = type;
		c.status = Content.STATUS_DELETED;
		c.power = power;
		c.title = title;
		c.upUserId = upUserId;
		c.upChannelId = upChannelId;
		c.tags = tags;
		c.data = data;
		c.proviteData = proviteData;
		c.ext = ext;
		c.activityStart = activityStart;
		c.activityEnd = activityEnd;
		try (DruidPooledConnection conn = ds.getConnection()) {
			contentRepository.insert(conn, c);
		}
		return c;
		
	}
	
	
	
	
	@POSTAPI(
		path = "editContent", //
		des = "修改内容", //
		ret = "所创建的对象"//
	)
	public Content editContent(
		@P(t = "内容编号") Long id, //
		@P(t = "模块编号" ,r = false) String module, //
		@P(t = "内容类型" ,r = false) Byte type, // 
		@P(t = "状态" ,r = false) Byte status, //
		@P(t = "权力:付费，会员等" ,r = false) Byte power, //
		@P(t = "上传用户编号" ,r = false) Long upUserId, //
		@P(t = "上传专栏编号", r = false) Long upChannelId, //
		@P(t = "标签" , r = false) JSONObject tags, //
		@P(t = "标题",r = false) String title, //
		@P(t = "数据",r = false) String data, //
		@P(t = "私密信息",r = false) String proviteData, //
		@P(t = "扩展信息",r = false) String ext, //
		@P(t = "活动开始时间",r = false) Date activityStart, //
		@P(t = "活动结束时间",r = false) Date activityEnd //
	)
	throws Exception {
		Content c = new Content();
		c.orgModule = module;
		c.updateTime = new Date();
		c.type = type;
		c.status = status;
		c.power = power;
		c.title = title;
		c.upUserId = upUserId;
		c.upChannelId = upChannelId;
		c.tags = tags;
		c.title = title;
		c.data = data;
		c.proviteData = proviteData;
		c.ext = ext;
		c.activityStart = activityStart;
		c.activityEnd = activityEnd;
		try (DruidPooledConnection conn = ds.getConnection()) {
			contentRepository.update(conn, EXP.INS().key("id", id), c, true);			
		}
		return c;
	}

	@POSTAPI(
		path = "delContentById", //
		des = "删除内容", //
		ret = "所创建的对象"//
	)
	public void delContentById(
		@P(t = "内容编号") Long id //
	) throws Exception {
		Content c = new Content();
		c.id = id;
		try (DruidPooledConnection conn = ds.getConnection()) {
			contentRepository.delete(conn, EXP.INS().key("id", id));			
		}
	}
	
	@POSTAPI(
			path = "auditContent", //
			des = "审核内容", //
			ret = "所创建的对象"//
		)
		public void auditContent(
			@P(t = "内容编号") Long id, //
			@P(t = "是否通过") boolean bool //
		) throws Exception {
			Content c = new Content();
			c.id = id;
			c.updateTime = new Date();
			if(bool) {
				c.status = Content.STATUS_DELETED;				
			}else {
				c.status = Content.STATUS_NORMAL;
			}
			try (DruidPooledConnection conn = ds.getConnection()) {
				contentRepository.update(conn, EXP.INS().key("id", id), c, true);			
			}
		}

	/**
	 * 根据条件查询内容 
	 * 
	 */
	@POSTAPI(
		path = "getContents", //
		des = "根据条件查询内容 获取用户发布内容 ", //
		ret = "所创建的对象"//
	)
	public APIResponse getContents(
		@P(t = "模块")String module, 
		@P(t = "类型",r = false)Byte type, 
		@P(t = "状态编号",r = false)Byte status,
		@P(t = "权力",r = false)Byte power,
		@P(t = "上传用户编号",r = false)Long upUserId,
		@P(t = "上传专栏编号",r = false)Long upChannelId,
		@P(t = "标签",r = false)JSONObject tags, 
		int count, 
		int offset
	)
	throws Exception {
		EXP exp = EXP.INS(false).key("org_module", module).andKey("type", type).andKey("status", status).andKey("power", power).andKey("up_user_id", upUserId).andKey("up_channel_id", upChannelId);
		if(tags !=null && tags.size()>0) {
			exp.and(EXP.JSON_CONTAINS_JSONOBJECT(tags, "tags"));
		}
		try (DruidPooledConnection conn = ds.getConnection()) {			
			List<Content> list = contentRepository.getList(conn,exp, count, offset);
			for(Content c:list) {
				c.user = userService.getUserById(conn, c.upUserId);
			}
			return APIResponse.getNewSuccessResp(ServiceUtils.checkNull(list));			
		}
	}

	@POSTAPI(
		path = "getConntent", //
		des = "据内容id查询返回一个内容对象", //
		ret = ""//
	)
	public Content getConntent(
		@P(t = "内容编号") Long id
	) throws ServerException, SQLException {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return contentRepository.get(conn, EXP.INS().key("id", id));			
		}
	}
	
	@POSTAPI(
		path = "setConntentTag", //
		des = "设置要添加的内容标签", //
		ret = ""//
	)
	public int setConntentTag(
		@P(t = "内容id")Long id,
		@P(t = "标签")JSONObject tags
	) throws ServerException, SQLException {
		EXP where = EXP.INS().key("id", id);
		int ret = 0;
		Set<String> keySet= tags.keySet();
        for (String key : keySet) {
            for(int i=0;i<tags.getJSONArray(key).size();i++) {
            	EXP tagAppend = EXP.JSON_ARRAY_APPEND_ONKEY("tags", key, tags.getJSONArray((key)).get(i), true);
            	try (DruidPooledConnection conn = ds.getConnection()) {
        			ret = contentRepository.update(conn, tagAppend, where);			
        		}
            }
        }
        return ret;
	}
	
		/**
		 * 获取发布信息
		 */
		@POSTAPI(//
				path = "returnTabBar", //
				des = "获取发布类型", //
				ret = "")
		public APIResponse returnTabBar(//
		) throws Exception {
			try (DruidPooledConnection conn = ds.getConnection()) {
				
				JSONArray json = new JSONArray();
				JSONObject jo1 = new JSONObject();
				jo1.put("iconPath", "/static/image/release.png");
				jo1.put("selectedIconPath", "/static/image/release.png");
				jo1.put("text", "发图文");
				jo1.put("active", true);
				jo1.put("url", "/pages/index/addContent/addContent?type=1");
				json.add(jo1);
//				JSONObject jo2 = new JSONObject();
//				jo2.put("iconPath", "/static/image/video.png");
//				jo2.put("selectedIconPath", "/static/image/video.png");
//				jo2.put("text", "发视频");
//				jo2.put("active", false);
//				jo2.put("url", "/pages/index/addContent/addContent?type=0");
//				json.add(jo2);
				return APIResponse.getNewSuccessResp(json);
			}
		}

}

