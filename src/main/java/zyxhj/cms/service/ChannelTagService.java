package zyxhj.cms.service;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.cms.domian.ChannelContentTag;
import zyxhj.cms.domian.ChannelTag;
import zyxhj.cms.repository.ChannelContentTagRepository;
import zyxhj.cms.repository.ChannelTagRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.Controller;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;

public class ChannelTagService extends Controller{
	private static Logger log = LoggerFactory.getLogger(ChannelService.class);
	private DruidDataSource ds;
	private ChannelTagRepository channelTagRepository;
	private ChannelContentTagRepository channelContentTagRepository;

	public ChannelTagService(String node) {
		super(node);
		try {
			ds = DataSource.getDruidDataSource("rdsDefault.prop");
			channelTagRepository = Singleton.ins(ChannelTagRepository.class);
			channelContentTagRepository = Singleton.ins(ChannelContentTagRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void createChannelTag(Long moduleId,String name,Byte status) {
		
	}
	public void delChannelTag(Long moduleId,String name,Byte status) {
			
	}
	public void editChannelTag(Long moduleId,String name,Byte status) {
		
	}
	
	
	@POSTAPI(
		path = "getChannelTags", //
		des = "查询专栏标签", //
		ret = ""//
	)
	public List<ChannelTag> getChannelTags(
		@P(t = "模块编号")Long moduleId,
		@P(t = "状态",r = false)Byte status,
		int count,
		int offset
	) throws ServerException, SQLException {
		EXP exp = EXP.INS(false).key("module_id", moduleId).andKey("status", status);
		try (DruidPooledConnection conn = ds.getConnection()) {
			return channelTagRepository.getList(conn, exp, count, offset);			
		}
	}
	
	
	@POSTAPI(
		path = "getChannelContentTag", //
		des = "查询课程", //
		ret = ""//
	)
	public List<ChannelContentTag> getChannelContentTag(
		@P(t = "模块编号")Long moduleId,
		@P(t = "专栏编号")Long channelId,
		@P(t = "状态",r = false)Byte status,
		int count,
		int offset
	) throws ServerException, SQLException {
		EXP exp = EXP.INS(false).key("module_id", moduleId).andKey("channel_id", channelId).andKey("status", status);
		try (DruidPooledConnection conn = ds.getConnection()) {
			return channelContentTagRepository.getList(conn, exp, count, offset);			
		}
	}
	@POSTAPI(
			path = "updateChannelContentTag", //
			des = "修改课程", //
			ret = ""//
		)
		public int updateChannelContentTag(
				@P(t = "模块编号")Long moduleId,
				@P(t = "编号")Long id,
				@P(t = "名称")String name,
				@P(t = "价格")String price,
				@P(t = "专栏编号")Long channelId,
				@P(t = "状态")Byte status
		) throws ServerException, SQLException {
			EXP exp = EXP.INS(false).key("module_id", moduleId).andKey("id", id);
			ChannelContentTag c = new ChannelContentTag();
			c.name = name;
			c.price = price;
			c.channelId = channelId;
			c.status = status;
			try (DruidPooledConnection conn = ds.getConnection()) {
				return channelContentTagRepository.update(conn, exp, c,true);			
			}
		}
	
	@POSTAPI(
			path = "createChannelContentTag", //
			des = "创建课程（标签）", //
			ret = ""//
		)
		public ChannelContentTag createChannelContentTag(
			@P(t = "模块编号")Long moduleId,
			@P(t = "名称")String name,
			@P(t = "价格")String price,
			@P(t = "专栏编号")Long channelId,
			@P(t = "状态")Byte status
		) throws ServerException, SQLException {
			try (DruidPooledConnection conn = ds.getConnection()) {
				ChannelContentTag c = new ChannelContentTag();
				c.moduleId = moduleId;
				c.id = IDUtils.getSimpleId();
				c.name = name;
				c.price = price;
				c.channelId = channelId;
				c.status = status;
				channelContentTagRepository.insert(conn, c);
				return c;	
			}
		}
}
