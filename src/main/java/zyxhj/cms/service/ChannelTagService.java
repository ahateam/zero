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
		des = "查询某个专栏内的标签(根据专栏id查询标签)", //
		ret = ""//
	)
	public List<ChannelContentTag> getChannelContentTag(
		@P(t = "模块编号")Long moduleId,
		@P(t = "专栏编号")Long channelId,
		@P(t = "状态",r = false)Byte status,
		int count,
		int offset
	) throws ServerException, SQLException {
		EXP exp = EXP.INS(false).key("module_id", moduleId).andKey("channelId", channelId).andKey("status", status);
		try (DruidPooledConnection conn = ds.getConnection()) {
			return channelContentTagRepository.getList(conn, exp, count, offset);			
		}
	}
}
