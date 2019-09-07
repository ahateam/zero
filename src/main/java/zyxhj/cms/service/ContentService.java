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
		@P(t = "扩展信息",r = false) String ext //
	)
	throws Exception {
		Content c = new Content();
		c.moduleId = module;
		c.id = IDUtils.getSimpleId();
		c.createTime = new Date();
		c.updateTime = c.createTime;
		c.type = type;
		c.status = status;
		c.power = power;
		c.title = title;
		c.upUserId = upUserId;
		c.upChannelId = upChannelId;
		c.tags = tags;
		c.data = data;
		c.proviteData = proviteData;
		c.ext = ext;
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
		@P(t = "扩展信息",r = false) String ext //
	)
	throws Exception {
		Content c = new Content();
		c.moduleId = module;
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
		try (DruidPooledConnection conn = ds.getConnection()) {
			contentRepository.update(conn, EXP.INS().key("id", id), c, true);			
		}
		return c;
	}

	@POSTAPI(
		path = "delContentById", //
		des = "逻辑删除内容", //
		ret = "所创建的对象"//
	)
	public void delContentById(
		@P(t = "内容编号") Long id //
	) throws Exception {
		Content c = new Content();
		c.id = id;
		c.updateTime = new Date();
		c.status = Content.STATUS_DELETED;
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
		EXP exp = EXP.INS(false).key("module_id", module).andKey("type", type).andKey("status", status).andKey("power", power).andKey("up_user_id", upUserId).andKey("up_channel_id", upChannelId);
		if(tags !=null && tags.size()>0) {
			exp = exp.and(EXP.JSON_CONTAINS_JSONOBJECT(tags, "tags"));
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
				JSONObject jo2 = new JSONObject();
				jo2.put("iconPath", "/static/image/video.png");
				jo2.put("selectedIconPath", "/static/image/video.png");
				jo2.put("text", "发视频");
				jo2.put("active", false);
				jo2.put("url", "/pages/index/addContent/addContent?type=0");
				json.add(jo2);
				return APIResponse.getNewSuccessResp(json);
			}
		}

}

































//-----------------------------------------------------------------------------
/*package zyxhj.cms.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.cms.domian.ContentTag;
import zyxhj.cms.domian.ContentTagGroup;
import zyxhj.cms.repository.ContentRepository;
import zyxhj.cms.repository.ContentTagGroupRepository;
import zyxhj.cms.repository.ContentTagRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.Controller;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;

public class ContentService extends Controller {

	private static Logger log = LoggerFactory.getLogger(ContentService.class);

	private DruidDataSource ds;

	private ContentRepository contentRepository;
	private ContentTagRepository tagRepository;
	private ContentTagGroupRepository groupRepository;

	public ContentService(String node) {
		super(node);

		try {

			ds = DataSource.getDruidDataSource("rdsDefault.prop");

			contentRepository = Singleton.ins(ContentRepository.class);
			tagRepository = Singleton.ins(ContentTagRepository.class);
			groupRepository = Singleton.ins(ContentTagGroupRepository.class);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public ContentTagGroup createTagGroup(Long moduleId, String remark) throws Exception {

		ContentTagGroup group = new ContentTagGroup();
		group.moduleId = moduleId;
		group.id = IDUtils.getSimpleId();
		group.remark = remark;

		try (DruidPooledConnection conn = ds.getConnection()) {
			groupRepository.insert(conn, group);
			return group;
		}
	}

	public int delTagGroup(Long moduleId, Long groupId) throws Exception {

		// 删除标签分组，则原有分组下所有标签失效
		// 因此需要先移除标签
		// 更因此需要先清理所有包含了这些标签的内容上的标签字段

		// TODO 总之，以上操作比较容易产生垃圾数据，日后再好好做

		return 0;
	}

	public List<ContentTagGroup> getTagGroupList(Long moduleId, Integer count, Integer offset) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return groupRepository.getList(conn, EXP.INS().key("module_id", moduleId), count, offset);
		}
	}

	public int editTagGroup(Long moduleId, Long groupId, String remark) throws Exception {
		ContentTagGroup ctg = new ContentTagGroup();
		ctg.remark = remark;

		try (DruidPooledConnection conn = ds.getConnection()) {
			return groupRepository.update(conn, EXP.INS()//
					.key("module_id", moduleId).andKey("group_id", groupId), ctg, true);
		}
	}

	////////////////////////////

	public ContentTag createTag(Long moduleId, Long groupId, String name) throws Exception {
		ContentTag tag = new ContentTag();

		tag.moduleId = moduleId;
		tag.groupId = groupId;
		tag.name = name;
		tag.status = ContentTag.STATUS_ENABLE;
		try (DruidPooledConnection conn = ds.getConnection()) {
			tagRepository.insert(conn, tag);
			return tag;
		}
	}

	public int delTag() {
		// 删除标签，则标签失效
		// 因此需要先清理所有包含了这些标签的内容上的标签字段

		// TODO 总之，以上操作比较容易产生垃圾数据，日后再好好做

		return 0;
	}

	/**
	 * 根据状态获取标签列表
	 */
	/*public List<ContentTag> getTagList(Long moduleId, Long groupId, //
			@P(t = "", r = false) Byte status, //
			Integer count, Integer offset) throws Exception {

		try (DruidPooledConnection conn = ds.getConnection()) {
			return tagRepository.getList(conn, EXP.INS()//
					.key("module_id", moduleId).andKey("group_id", groupId).andKey("status", status), count, offset);
		}

	}

	public int editTag(Long moduleId, Long groupId, String name, Byte status) throws Exception {
		ContentTag tag = new ContentTag();
		tag.status = status;

		try (DruidPooledConnection conn = ds.getConnection()) {
			return tagRepository.update(conn, EXP.INS()//
					.key("module_id", moduleId).andKey("group_id", groupId).andKey("name", name), tag, true);
		}
	}

	////////////////////
	
	

}
*/