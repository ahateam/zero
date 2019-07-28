package zyxhj.core.domain;

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
	public String module;

	/**
	 * 分组类型（系统，自定义）
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.BYTE)
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

}
