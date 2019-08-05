package zyxhj.core.domain;

import java.util.TreeMap;

import zyxhj.utils.data.AnnDic;
import zyxhj.utils.data.AnnDicField;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 
 * 通用标签
 */
@RDSAnnEntity(alias = "tb_tag_group")
public class TagGroup {

	/**
	 * 模块
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String moduleKey;

	/**
	 * 分组类型关键字（系统，自定义）
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String typeKey;

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 分组名（分类前缀 + 关键字）
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;

	/**
	 * 备注
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String remark;

	/////////////////////////////////////
	/////////////////////////////////////
	/////////////////////////////////////

	private static TagGroup buildSysTagGroup(String moduleKey, String tagKey, Long id, String name, String remark) {
		TagGroup ret = new TagGroup();
		ret.moduleKey = moduleKey;
		ret.typeKey = tagKey;
		ret.id = id;
		ret.name = name;
		ret.remark = remark;

		return ret;
	}

	private static Long temp = 100L;// 自增编号

	public static final TagGroup flow_asset_desc_table = buildSysTagGroup(Module.FLOW.key, "table", temp++,
			"流程资产描述——表格", null);
	public static final TagGroup flow_asset_desc_report = buildSysTagGroup(Module.FLOW.key, "report", temp++,
			"流程资产描述——报告", null);
	public static final TagGroup flow_undefinition = buildSysTagGroup(Module.FLOW.key, "undefinition", temp++,
			"未定义分组", null);

	public static TreeMap<String, TagGroup> SYS_TAG_GROUP_MAP = new TreeMap<>();

	static {
		SYS_TAG_GROUP_MAP.put(flow_asset_desc_table.moduleKey, flow_asset_desc_table);
		SYS_TAG_GROUP_MAP.put(flow_asset_desc_report.moduleKey, flow_asset_desc_report);
		SYS_TAG_GROUP_MAP.put(flow_undefinition.moduleKey, flow_undefinition);
	}

}
