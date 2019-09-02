package zyxhj.cms.service;

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
	public List<ContentTag> getTagList(Long moduleId, Long groupId, //
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
