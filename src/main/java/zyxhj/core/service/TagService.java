package zyxhj.core.service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.Tag;
import zyxhj.core.domain.TagGroup;
import zyxhj.core.repository.TagGroupRepository;
import zyxhj.core.repository.TagRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.Controller;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;

public class TagService extends Controller {

	private static Logger log = LoggerFactory.getLogger(TagService.class);

	private DruidDataSource ds;

	private TagRepository tagRepository;
	private TagGroupRepository groupRepository;

	public TagService(String node) {
		super(node);
		try {
			ds = DataSource.getDruidDataSource("rdsDefault.prop");

			tagRepository = Singleton.ins(TagRepository.class);
			groupRepository = Singleton.ins(TagGroupRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@POSTAPI(//
			path = "createTagGroup", //
			des = "创建自定义标签分组", //
			ret = "TagGroup实例" //
	)
	public TagGroup createTagGroup(//
			@P(t = "分组所属模块关键字") String moduleKey, //
			@P(t = "分组类型自定义关键字") String type, //
			@P(t = "分组名称") String name, //
			@P(t = "备注") String remark//
	) throws Exception {
		TagGroup tg = new TagGroup();
		tg.moduleKey = moduleKey;
		tg.type = type;
		tg.id = IDUtils.getSimpleId();
		tg.name = name;
		tg.remark = remark;

		try (DruidPooledConnection conn = ds.getConnection()) {
			groupRepository.insert(conn, tg);
		}
		return tg;
	}

	@POSTAPI(//
			path = "editTagGroup", //
			des = "编辑自定义标签分组", //
			ret = "更新影响的记录行数" //
	)
	public int editTagGroup(//
			@P(t = "分组编号）") Long id, //
			@P(t = "分组名称") String name, //
			@P(t = "备注") String remark//
	) throws Exception {
		TagGroup renew = new TagGroup();
		renew.name = name;
		renew.remark = remark;

		try (DruidPooledConnection conn = ds.getConnection()) {
			return groupRepository.update(conn,EXP.ins().key("id", id), renew, true);
			
		}
	}

	@POSTAPI(//
			path = "getTagGroupTypeList", //
			des = "获取某模块所属的标签分组的类型列表", //
			ret = "List<String>" //
	)
	public List<String> getTagGroupTypeList(//
			@P(t = "分组所属模块关键字") String moduleKey //
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return groupRepository.getTagGroupTypeList(conn, moduleKey);
		}
	}

	@POSTAPI(//
			path = "getTagGroupList", //
			des = "获取某模块所属的标签分组列表", //
			ret = "List<TagGroup>" //
	)
	public List<TagGroup> getTagGroupList(//
			@P(t = "分组所属模块关键字") String moduleKey, //
			@P(t = "分组类型自定义关键字（可选参数），不填表示全选", r = false) String type, //
			@P(t = "数量") Integer count, //
			@P(t = "偏移") Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			// TODO 没有实现不填就全选的功能
			return groupRepository.getList(conn, EXP.ins().key("module_key", moduleKey).andKey("type", type), count,
					offset);
		}
	}

	@POSTAPI(//
			path = "createTag", //
			des = "创建标签", //
			ret = "Tag实例" //
	)
	public Tag createTag(//
			@P(t = "标签分组编号") Long groupId, //
			@P(t = "标签名称（关键字）") String name//
	) throws Exception {
		Tag ct = new Tag();

		ct.groupId = groupId;
		ct.name = name;
		ct.status = Tag.STATUS.ENABLED;

		try (DruidPooledConnection conn = ds.getConnection()) {
			tagRepository.insert(conn, ct);
		}
		return ct;
	}

	@POSTAPI(//
			path = "getTagList", //
			des = "根据分组编号和状态获取标签列表", //
			ret = "List<Tag>" //
	)
	public List<Tag> getTagList(Long groupId, Byte status) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return tagRepository.getList(conn, EXP.ins().key("group_id", groupId).andKey("status", status), 512, 0);
		}
	}

	@POSTAPI(//
			path = "editTag", //
			des = "编辑标签状态", //
			ret = "更新影响的记录行数" //
	)
	public int editTag(Long groupId, String name, Byte status) throws Exception {
		Tag renew = new Tag();
		renew.status = status;

		try (DruidPooledConnection conn = ds.getConnection()) {
			return tagRepository.update(conn,EXP.ins().key("group_id", groupId).andKey("name", name), renew, true);
			
		}
	}

}
