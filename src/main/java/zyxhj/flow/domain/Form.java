package zyxhj.flow.domain;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.search.FieldType;

import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSAnnIndex;
import zyxhj.utils.data.ts.TSEntity;

/**
 * 表单
 *
 */
@TSAnnEntity(alias = "Form")
public class Form extends TSEntity {

	/**
	 * 分片编号，MD5(id)，避免数据热点
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.STRING)
	public String _id;

	/**
	 * 编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long id;

	/**
	 * 表单名称
	 */
	@TSAnnIndex(name = "PartIndex", type = FieldType.TEXT, enableSortAndAgg = false, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String name;

	/**
	 * 表单宽度
	 */
	public Integer wide;

	/**
	 * 表单列数
	 */
	public Integer column;

	/**
	 * 表单数据
	 */
	public String data;

	/**
	 * 部门id
	 */
	public Long departId;

	/**
	 * 表单类型
	 */
	public Byte type;

}
