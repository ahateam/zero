package zyxhj.cms.domian;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 
 * 内容标签分组
 */
@RDSAnnEntity(alias = "tb_cms_content_tag_group")
public class ContentTagGroup {

	/**
	 * 所属模块</br>
	 * orgId + moduleKey
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public String orgModule;

	/**
	 * 分组关键字（分类前缀 + 关键字）
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String groupName;

	/**
	 * 备注
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String remark;

}
