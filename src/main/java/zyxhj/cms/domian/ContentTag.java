package zyxhj.cms.domian;

import zyxhj.utils.data.AnnDicField;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 
 * 内容标签
 */
@RDSAnnEntity(alias = "tb_cms_content_tag")
public class ContentTag {

	/**
	 * 所属分组编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String conGroup;

	/**
	 * 标签名称（名称直接配合模块编号和持有者编号做主键，不重复）
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;

	/**
	 * 状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////

	@AnnDicField(alias = "禁用")
	public static final Byte STATUS_DISABLE = 0;

	@AnnDicField(alias = "启用")
	public static final Byte STATUS_ENABLE = 1;
}
