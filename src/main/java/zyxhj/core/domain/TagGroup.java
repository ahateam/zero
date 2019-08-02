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

	@AnnDic
	public static final class TYPE {

		@AnnDicField(alias = "禁用")
		public static final String SYS = "sys";

	}

	/**
	 * 模块
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String moduleKey;

	/**
	 * 分组类型（系统，自定义）
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String type;

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

	private static TagGroup buildSysTagGroup(String moduleKey, Long id, String name, String remark) {
		TagGroup ret = new TagGroup();
		ret.moduleKey = moduleKey;
		ret.type = TYPE.SYS;
		ret.id = id;
		ret.name = name;
		ret.remark = remark;

		return ret;
	}

	private static Long temp = 100L;// 自增编号

	public static final TagGroup flow_asset_desc_table = buildSysTagGroup(Module.FLOW.key, temp++, "流程资产描述——表格", null);
	public static final TagGroup flow_asset_desc_report = buildSysTagGroup(Module.FLOW.key, temp++, "流程资产描述——报告", null);

	public static TreeMap<Long, TagGroup> SYS_TAG_GROUP_MAP = new TreeMap<>();

	static {
		SYS_TAG_GROUP_MAP.put(flow_asset_desc_table.id, flow_asset_desc_table);
		SYS_TAG_GROUP_MAP.put(flow_asset_desc_report.id, flow_asset_desc_report);
	}

}
