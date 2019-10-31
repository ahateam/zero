package zyxhj.cms.service;

import java.sql.SQLException;
import java.util.LinkedList;
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
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;
import zyxhj.utils.api.RC;
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
			contentTagGroupRepository = Singleton.ins(ContentTagGroupRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	
	@POSTAPI(//
			path = "createContentTag", //
			des = "创建内容标签", //
			ret = "" //
	)
	public APIResponse createContentTag(
			@P(t = "分组") String group, 
			@P(t = "名称") String name
	) throws ServerException, SQLException {
		ContentTag ct = new ContentTag();
		ct.GroupName = group;
		ct.name = name;
		ct.status = ContentTag.STATUS_ENABLE;
		try (DruidPooledConnection conn = ds.getConnection()) {
			ContentTag c = contentTagRepository.get(conn, EXP.INS().key("group_name", group).andKey("name", name));
			if(c != null) {
				return APIResponse.getNewFailureResp(new RC("fail","该分组下标签已存在，不能添加重复标签"));
			}
			contentTagRepository.insert(conn, ct);
			return APIResponse.getNewSuccessResp();
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
			@P(t = "状态", r =false) Byte status,
			@P(t = "排序大小", r =false) String sort
	) throws ServerException, SQLException {
		ContentTag ct = new ContentTag();
		ct.status = status;
		ct.sortSize = sort;
		try (DruidPooledConnection conn = ds.getConnection()) {
			contentTagRepository.update(conn, EXP.INS().key("group_name", group).andKey("name", name), ct,true);			
		}
	}
	
	@POSTAPI(//
			path = "getContentTag", //
			des = "根据状态获取内容标签", //
			ret = "" //
	)
	public List<ContentTag> getContentTag(
			@P(t = "模板编号", r = false) Long moduleId, 
			@P(t = "分组",r = false) String group,
			@P(t = "状态",r = false) Byte status,
			Integer count,
			Integer offset
			) throws ServerException, SQLException {
		try (DruidPooledConnection conn = ds.getConnection()) {
			List<ContentTagGroup> list = getContentTagGroup(moduleId,count,offset);//先确定该app下的分组
			List<String> g = new LinkedList<String>();
			if(status ==null) {
				status = ContentTag.STATUS_ENABLE;
			}
			if(list != null) {
				for(ContentTagGroup ct:list) {
					if(group != null) {//如果group不为空，查询对应分组下的标签
						if(group.equals(ct.groupName)) {
							return contentTagRepository.getList(conn, EXP.INS(false).key("status", status).
									andKey("group_name", group).append("order by sort_size desc"), count, offset);
						}
					}else {//如果group为空，查询对应模块下的标签
						g.add(ct.groupName);
					}
				}
				return contentTagRepository.getList(conn, EXP.INS(false).key("status", status).
						and(EXP.INS().IN("group_name", g.toArray())).append("order by sort_size desc"), count, offset);
			}
			return null;
		}
	}
		
	@POSTAPI(//
			path = "createContentTagGroup", //
			des = "创建内容标签分组", //
			ret = "" //
	)
	public APIResponse createContentTagGroup(
			@P(t = "模板编号") String moduleId,
			@P(t = "分组编号") String group, 
			@P(t = "备注") String remark
	) throws ServerException, SQLException {
		ContentTagGroup ctg = new ContentTagGroup();
		ctg.orgModule = moduleId;
		ctg.groupName = group;
		ctg.remark = remark;
		try (DruidPooledConnection conn = ds.getConnection()) {
			ContentTagGroup c = contentTagGroupRepository.get(conn, EXP.INS().key("group_name", group).andKey("org_module", moduleId));
			if(c != null) {
				return APIResponse.getNewFailureResp(new RC("fail","该分组已存在，不能添加重复分组"));
			}
			contentTagGroupRepository.insert(conn, ctg);
			return APIResponse.getNewSuccessResp();
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
			path = "delContentTag", //
			des = "删除标签", //
			ret = "" //
	)
	public void delContentTag(
			@P(t = "分组名称") String group,
			@P(t = "标签名称") String tagName
	) throws ServerException, SQLException {
		try (DruidPooledConnection conn = ds.getConnection()) {
			contentTagRepository.delete(conn, EXP.INS().key("group_name", group).andKey("name", tagName));
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
