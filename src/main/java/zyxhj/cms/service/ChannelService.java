package zyxhj.cms.service;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;
import zyxhj.cms.domian.Channel;
import zyxhj.cms.domian.ChannelUser;
import zyxhj.cms.repository.ChannelContentTagRepository;
import zyxhj.cms.repository.ChannelRepository;
import zyxhj.cms.repository.ChannelUserRepository;
import zyxhj.cms.repository.ContentRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;

import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;

public class ChannelService extends Controller{

	private static Logger log = LoggerFactory.getLogger(ChannelService.class);
	private DruidDataSource ds;
	private ChannelRepository channelRepository;
	private ChannelContentTagRepository channelContentTagRepository; 
	private ContentRepository contentRepository;
	private ChannelUserRepository channelUserRepository;
	
	public ChannelService(String node) {
		super(node);
		try {
			ds = DataSource.getDruidDataSource("rdsDefault.prop");
			channelRepository = Singleton.ins(ChannelRepository.class);
			contentRepository = Singleton.ins(ContentRepository.class);
			channelContentTagRepository = Singleton.ins(ChannelContentTagRepository.class);
			channelUserRepository = Singleton.ins(ChannelUserRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	
	@POSTAPI(
		path = "createChannel", //
		des = "创建专栏", //
		ret = "所创建的对象"//
	)
	public Channel createChannel(
		@P(t = "模块编号") String module,
		@P(t = "状态") Byte status, 
		@P(t = "标题") String title, 
		@P(t = "标签（json）") String tags, 
		@P(t = "数据",r = false) String data,
		@P(t = "数据",r = false) Byte type
	)throws Exception {
		Channel channel = new Channel();
		channel.id = IDUtils.getSimpleId();
		channel.orgModule = module;
		channel.status = status;
		channel.createTime = new Date();
		channel.title = title;
		channel.tags = tags;
		channel.type = type;
		if(data != null) {
			channel.data = data;			
		}
		try (DruidPooledConnection conn = ds.getConnection()) {
			channelRepository.insert(conn, channel);;			
		}
		return channel;
	}
	
	
	@POSTAPI(
		path = "editChannel", //
		des = "修改专栏", //
		ret = ""//
	)
	public Channel editChannel(
		@P(t = "模块编号")String module,
		@P(t = "专栏编号")Long id,
		@P(t = "状态",r = false)Byte status, 
		@P(t = "标题",r = false)String title,
		@P(t = "标签（json）",r = false)String tags, 
		@P(t = "数据",r = false)String data
	) throws Exception {
		Channel channel = new Channel();
		channel.orgModule = module;
		channel.status = status;
		channel.title = title;
		if(tags != null) {
			channel.tags = tags;			
		}
		channel.data = data;
		try (DruidPooledConnection conn = ds.getConnection()) {
			channelRepository.update(conn, EXP.INS().key("org_module", module) .key("id", id), channel, true);		
		}
		return channel;

	}
	
	@POSTAPI(
		path = "getChannels", //
		des = "根据条件查询获取专栏", //
		ret = ""//
	)
	public List<Channel> getChannels(
		@P(t = "模块编号")Long module,
		@P(t = "状态",r = false)Byte status, 
		@P(t = "标签（json）",r = false)String tags, 
		@P(t = "类型",r = false) Byte type,
		int count,
		int offset
	) throws Exception {
		JSONObject keys = null;
		EXP exp = EXP.INS(false).key("org_module", module).andKey("type", type);
		if(tags !=null) {
			 keys = JSONObject.parseObject(tags);
		}
		if(status == null) {
			exp.andKey("status", Channel.STATUS_ENABLE);
		}else {
			exp.andKey("status", status);
		}
		
		if(tags !=null && keys.size()>0) {
			exp = exp.and(EXP.JSON_CONTAINS_JSONOBJECT(keys, "tags"));
		}
		try (DruidPooledConnection conn = ds.getConnection()) {
			return channelRepository.getList(conn,exp, count, offset);			
		}

	}
	
	
	
	@POSTAPI(
			path = "getChannlContentTagByChannelId", //
			des = "根据专栏id获取专栏内容标签(channlContentTag)", //
			ret = ""//
		)
		public APIResponse getChannlContentTagByChannelId(
			@P(t = "模块编号")Long module, 
			@P(t = "专栏编号")Long channelId, 
			@P(t = "内容状态",r = false)Byte status,
			Integer count, 
			Integer offset
		) throws Exception {
			EXP exp = EXP.INS(false).key("channel_id", channelId).andKey("status", status);
			try (DruidPooledConnection conn = ds.getConnection()) {
				return APIResponse.getNewSuccessResp(channelContentTagRepository.getList(conn, exp, count, offset));			
			}

		}
	
	
	
	@POSTAPI(
			path = "getContentByChannelId", //
			des = "根据专栏内容标签获取内容", //
			ret = ""//
		)
		public APIResponse getContentByChannelId(
			@P(t = "模块编号")String module, 
			@P(t = "专栏编号")Long channelId, 
			@P(t = "专栏内容标签")Long tagName, 
			@P(t = "内容状态",r = false)Byte status,
			Integer count, 
			Integer offset
		) throws Exception {
			EXP exp = EXP.INS(false).key("org_module", module).andKey("up_channel_id", channelId).andKey("status", status);
			try (DruidPooledConnection conn = ds.getConnection()) {
				return APIResponse.getNewSuccessResp(contentRepository.getList(conn,exp, count, offset));			
			}

		}
	
	@POSTAPI(
			path = "getChannlById", //
			des = "根据专栏id获取专栏", //
			ret = ""//
		)
	public Channel getChannlById(@P(t = "专栏id")Long id) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return channelRepository.get(conn, EXP.INS().key("id", id));
		}
	}
	
	@POSTAPI(
			path = "getChannelContentTagPower", //
			des = "查询用户是否购买了课程", //
			ret = ""//
		)
	public JSONObject getChannelContentTagPower(
			@P(t = "模块编号")Long modeuleId,
			@P(t = "专栏id")Long channelId,
			@P(t = "课程名id")Long channelContentTagId,
			@P(t = "用户id")Long userId
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			JSONObject json = new JSONObject();
			ChannelUser cu = channelUserRepository.get(conn, EXP.INS().key("channel_id", channelId)
					.andKey("channel_content_tag_id", channelContentTagId).andKey("user_id", userId));
			if(cu != null) {
				json.put("resultStatus", true);
				json.put("resultMsg", cu);
			}else {
				json.put("resultStatus", false);
			}
			return json;
		}
	}
	
	@POSTAPI(
			path = "PayChannelContentTag", //
			des = "用户购买课程", //
			ret = ""//
		)
	public ChannelUser PayChannelContentTag(
			@P(t = "模块编号")Long modeuleId,
			@P(t = "专栏id")Long channelId,
			@P(t = "课程名id")Long ChannelContentTagId,
			@P(t = "用户id")Long userId
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			ChannelUser c = new ChannelUser();
			c.channelId = channelId;
			c.userId = userId;
			c.ChannelContentTagId = ChannelContentTagId;
			c.createTime = new Date();
			channelUserRepository.insert(conn, c);
			return c;
		}
	}
	@POSTAPI(
			path = "banChannel", //
			des = "是否禁用专栏", //
			ret = ""//
		)
	public Channel banChannel(
			@P(t = "模块编号")Long modeuleId,
			@P(t = "专栏id")Long channelId,
			@P(t = "是否禁用")Boolean bool
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			Channel c = new Channel();
			if(bool) {
				c.status = Channel.STATUS_DISABLE;				
			}else {
				c.status = Channel.STATUS_ENABLE;				
			}
			channelRepository.update(conn, EXP.INS().key("org_module", modeuleId).andKey("id", channelId), c, true);
			return c;
		}
	}
	@POSTAPI(
			path = "delChannel", //
			des = "删除专栏", //
			ret = ""//
		)
	public int banChannel(
			@P(t = "模块编号")Long modeuleId,
			@P(t = "专栏id")Long channelId
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			channelRepository.delete(conn, EXP.INS().key("org_module", modeuleId).andKey("id", channelId));
			contentRepository.delete(conn, EXP.INS().key("up_channel_id", channelId));	
			return 1;
		}
	}
}





















//package zyxhj.cms.service;
//
//import java.util.Date;
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.alibaba.fastjson.JSONObject;
//import com.alicloud.openservices.tablestore.SyncClient;
//import com.alicloud.openservices.tablestore.model.Column;
//import com.alicloud.openservices.tablestore.model.PrimaryKey;
//import com.alicloud.openservices.tablestore.model.search.SearchQuery;
//
//import zyxhj.cms.domian.Channel;
//import zyxhj.cms.repository.ChannelRepository;
//import zyxhj.cms.repository.ContentRepository;
//import zyxhj.utils.IDUtils;
//import zyxhj.utils.Singleton;
//import zyxhj.utils.data.ts.ColumnBuilder;
//import zyxhj.utils.data.ts.PrimaryKeyBuilder;
//import zyxhj.utils.data.ts.TSQL;
//import zyxhj.utils.data.ts.TSQL.OP;
//import zyxhj.utils.data.ts.TSRepository;
//import zyxhj.utils.data.ts.TSUtils;
//
//public class ChannelService {
//
//	private static Logger log = LoggerFactory.getLogger(ChannelService.class);
//
//	private ChannelRepository channelRepository;
//	private ContentRepository contentRepository;
//
//	public ChannelService() {
//		try {
//			channelRepository = Singleton.ins(ChannelRepository.class);
//			contentRepository = Singleton.ins(ContentRepository.class);
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//		}
//	}
//
//	/**
//	 * 创建专栏
//	 */
//	public Channel createChannel(SyncClient client, String module, String title, String tags, String data)
//			throws Exception {
//		Channel channel = new Channel();
//		Long id = IDUtils.getSimpleId();
//		channel._id = TSUtils.get_id(id);
//		channel.id = id;
//		channel.module = module;
//		channel.status = (long) Channel.STATUS.NORMAL.v();
//		channel.createTime = new Date();
//		channel.title = title;
//		channel.tags = tags;
//		channel.data = data;
//
//		channelRepository.insert(client, channel, false);
//		return channel;
//	}
//
//	/**
//	 * 编辑专栏
//	 */
//	public void editChannel(SyncClient client, String _id, Long id, String title, String data) throws Exception {
//		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("id", id).build();
//
//		ColumnBuilder cb = new ColumnBuilder();
//		cb.add("title", title);
//		cb.add("data", data);
//		List<Column> columns = cb.build();
//
//		TSRepository.nativeUpdate(client, channelRepository.getTableName(), pk, true, columns);
//
//	}
//
//	/**
//	 * 修改专栏状态
//	 */
//	public void editChannelStatus(SyncClient client, String _id, Long id, Byte status) throws Exception {
//
//		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("id", id).build();
//
//		ColumnBuilder cb = new ColumnBuilder();
//		cb.add("status", (long) status);
//		List<Column> columns = cb.build();
//
//		TSRepository.nativeUpdate(client, channelRepository.getTableName(), pk, true, columns);
//
//	}
//
//	/**
//	 * 根据专栏编号获取内容
//	 */
//	public JSONObject getContentByChannelId(SyncClient client, String module, Long channelId, Byte status, Byte paid,
//			Integer count, Integer offset) throws Exception {
//
//		TSQL ts = new TSQL();
//		ts.Terms(OP.AND, "module", module).Term(OP.AND, "upChannelId", channelId);
//		if (status != null) {
//			ts.Term(OP.AND, "status", (long) status);
//		}
//		if (paid != null) {
//			ts.Term(OP.AND, "paid", (long) paid);
//		}
//		ts.setLimit(count);
//		ts.setOffset(offset);
//		SearchQuery query = ts.build();
//		return TSRepository.nativeSearch(client, contentRepository.getTableName(), "ContentIndex", query);
//
//	}
//
//	/**
//	 * 获取专栏列表
//	 */
//	public JSONObject getChannel(SyncClient client, String module, Byte status, String tags, Integer count,
//			Integer offset) throws Exception {
//
//		TSQL ts = new TSQL();
//		ts.Term(OP.AND, "module", module);
//		if (status != null) {
//			ts.Term(OP.AND, "status", (long) status);
//		}
//		if (tags != null) {
//			ts.Terms(OP.AND, "tags", tags);
//		}
//		ts.setLimit(count);
//		ts.setOffset(offset);
//		SearchQuery query = ts.build();
//		return TSRepository.nativeSearch(client, channelRepository.getTableName(), "ChannelIndex", query);
//	}
//
////
////	// 获取专栏列表
////	public JSONObject getChannelByStatus(SyncClient client, String module, Byte status, Integer count, Integer offset)
////			throws Exception {
////
////		TSQL ts = new TSQL();
////		ts.Term(OP.AND, "module", module).Term(OP.AND, "status", (long) status).build();
////		ts.setLimit(count);
////		ts.setOffset(offset);
////		SearchQuery query = ts.build();
////		return TSRepository.nativeSearch(client, channelRepository.getTableName(), "ChannelIndex", query);
////	}
////
////	// 根据标签获取专栏
////	public JSONObject getChannelByTags(SyncClient client, String module, Byte status, String tags, Integer count,
////			Integer offset) throws Exception {
////		TSQL ts = new TSQL();
////		ts.Term(OP.AND, "module", module).Term(OP.AND, "status", (long) status).Terms(OP.AND, "tags", tags).build();
////		ts.setLimit(count);
////		ts.setOffset(offset);
////		SearchQuery query = ts.build();
////
////		return TSRepository.nativeSearch(client, channelRepository.getTableName(), "ChannelIndex", query);
////
////	}
//
//}
