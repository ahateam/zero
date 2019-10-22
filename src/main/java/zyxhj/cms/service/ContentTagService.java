package zyxhj.cms.service;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.cms.domian.ContentTag;
import zyxhj.cms.domian.ContentTagGroup;
import zyxhj.cms.repository.ContentTagGroupRepository;
import zyxhj.cms.repository.ContentTagRepository;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.Controller;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;

public class ContentTagService extends Controller{

	private static Logger log = LoggerFactory.getLogger(ContentService.class);
	private DruidDataSource ds;
	private ContentTagRepository contentTagRepository;
	private ContentTagGroupRepository contentTagGroupRepository;
	
	public ContentTagService(String node) {
		super(node);
		try {
			ds = DataSource.getDruidDataSource("rdsDefault.prop");
			contentTagRepository = Singleton.ins(ContentTagRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	
	@POSTAPI(//
			path = "createContentTag", //
			des = "创建内容标签", //
			ret = "" //
	)
	public void createContentTag(
			@P(t = "分组") String group, 
			@P(t = "名称") String name
	) throws ServerException, SQLException {
		ContentTag ct = new ContentTag();
		ct.GroupName = group;
		ct.name = name;
		ct.status = ContentTag.STATUS_ENABLE;
		try (DruidPooledConnection conn = ds.getConnection()) {
			contentTagRepository.insert(conn, ct);
		}
	}
	
	@POSTAPI(//
			path = "editteContentTag", //
			des = "修改内容标签状态0:禁用   1:启用", //
			ret = "" //
	)
	public void editteContentTag(
			@P(t = "模板编号") Long moduleId, 
			@P(t = "分组") String group, 
			@P(t = "名称") String name,
			@P(t = "状态") Byte status
	) throws ServerException, SQLException {
		ContentTag ct = new ContentTag();
		ct.status = status;
		try (DruidPooledConnection conn = ds.getConnection()) {
			contentTagRepository.update(conn, EXP.INS().key("org_module", moduleId).
					andKey("group_name", group).andKey("name", name), ct,true);			
		}
	}
	
	@POSTAPI(//
			path = "getContentTag", //
			des = "根据状态获取内容标签", //
			ret = "" //
	)
	public List<ContentTag> getContentTag(
			@P(t = "模板编号") Long moduleId, 
			@P(t = "分组",r = false) String group,
			@P(t = "状态",r = false) Byte status,
			Integer count,
			Integer offset
			) throws ServerException, SQLException {
		try (DruidPooledConnection conn = ds.getConnection()) {
			if(status ==null) {
				status = ContentTag.STATUS_ENABLE;
			}
			return contentTagRepository.getList(conn, EXP.INS(false).key("group_name", group).andKey("status", status), count, offset);
		}
	}
		
	@POSTAPI(//
			path = "createContentTagGroup", //
			des = "创建内容标签分组", //
			ret = "" //
	)
	public void createContentTagGroup(
			@P(t = "模板编号") String moduleId,
			@P(t = "分组编号") String group, 
			@P(t = "备注") String remark
	) throws ServerException, SQLException {
		ContentTagGroup ctg = new ContentTagGroup();
		ctg.orgModule = moduleId;
		ctg.groupName = group;
		ctg.remark = remark;
		try (DruidPooledConnection conn = ds.getConnection()) {
			contentTagGroupRepository.insert(conn, ctg);
		}
	}
	
	@POSTAPI(//
			path = "delContentTagGroup", //
			des = "删除标签分组", //
			ret = "" //
	)
	public void delContentTagGroup(
			@P(t = "模板编号") Long moduleId,
			@P(t = "分组编号") String group
	) throws ServerException, SQLException {
		try (DruidPooledConnection conn = ds.getConnection()) {
			contentTagGroupRepository.delete(conn, EXP.INS().key("org_module", moduleId).andKey("con_group", group));
		}
	}
	
	@POSTAPI(//
			path = "getContentTagGroup", //
			des = "获取内容标签分组", //
			ret = "" //
	)
	public List<ContentTagGroup> getContentTagGroup(
			@P(t = "模板编号") Long moduleId,
			Integer count,
			Integer offset
	) throws ServerException, SQLException {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return contentTagGroupRepository.getList(conn, EXP.INS().key("org_module", moduleId), count, offset);
		}
	}
}
