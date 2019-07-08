package zyxhj.cms.domian;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.search.FieldType;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSAnnIndex;
import zyxhj.utils.data.ts.TSEntity;

/**
 * 
 * 通用标签
 */
@RDSAnnEntity(alias = "tb_cms_content_tag_group")
public class ContentTagGroup  extends TSEntity  {

	/**
	 * 编号
	 */
	@TSAnnIndex(name = "ContentTagGroup", type = FieldType.LONG, enableSortAndAgg = false, store = true)
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long id;

	/**
	 * 大类
	 */
	@TSAnnIndex(name = "ContentTagGroup", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String type;

	/**
	 * 分组关键字（分类前缀 + 关键字）
	 */
	@TSAnnIndex(name = "ContentTagGroup", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String keyword;

	/**
	 * 备注
	 */
	@TSAnnIndex(name = "ContentTagGroup", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String remark;
}
